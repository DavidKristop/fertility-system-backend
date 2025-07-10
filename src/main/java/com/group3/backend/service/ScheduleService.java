package com.group3.backend.service;

import com.group3.backend.constants.Roles;
import com.group3.backend.dto.request.ScheduleCreateRequest;
import com.group3.backend.dto.request.ScheduleServiceCreateRequest;
import com.group3.backend.dto.request.Schedule.ScheduleChangeRequest;
import com.group3.backend.dto.request.Schedule.ScheduleResultRequest;
import com.group3.backend.exception.ResourceConflictException;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.ScheduleResult;
import com.group3.backend.model.Service;
import com.group3.backend.model.User;
import com.group3.backend.model.Payment;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.repository.RequestAppointmentRepository;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.ServiceRepository;
import com.group3.backend.repository.TreatmentPhaseRepository;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.config.EnvironmentConfig;
import com.group3.backend.config.TimeZoneConfig;

import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service

public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private RequestAppointmentRepository requestAppointmentRepository;
    
    @Autowired
    private EnvironmentConfig environmentConfig;
    

    @Autowired
    private TimeZoneConfig timeZoneConfig;

    @Autowired
    private UserRepository userRepository;

    public List<Schedule> getAvailableDoctors(Integer year, Integer month) {
        List<Schedule> schedules = scheduleRepository.findAll();
        if(year == null) year = LocalDate.now().getYear();
        if(month == null) month = LocalDate.now().getMonthValue();
        
        return filterDate(schedules, year, month);
    }

    public List<Schedule> getSchedulesByDoctorId(UUID doctorId, Integer year, Integer month) {
        List<Schedule> schedules = scheduleRepository.findByDoctorId(doctorId);
        if(year == null) year = LocalDate.now().getYear();
        if(month == null) month = LocalDate.now().getMonthValue();
        
        return filterDate(schedules, year, month);
    }

    public List<Schedule> getSchedulesByPatientId(UUID patientId, Integer year, Integer month) {
        List<Schedule> schedules = scheduleRepository.findByPatientId(patientId);
        if(year == null) year = LocalDate.now().getYear();
        if(month == null) month = LocalDate.now().getMonthValue();
       
        return filterDate(schedules, year, month);
    }

    public Schedule getScheduleById(UUID id) {
        return scheduleRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
    }

    public List<Schedule> getAllSchedule(Integer year, Integer month){
        List<Schedule> schedules = scheduleRepository.findAll();
        if(year == null) year = LocalDate.now().getYear();
        if(month == null) month = LocalDate.now().getMonthValue();
        
        return filterDate(schedules, year, month);
    }

    public Schedule createScheduleBasedOnRequest (RequestAppointment requestAppointment){
        Schedule schedule = createSchedule(
            ScheduleCreateRequest.builder()
                .patientId(requestAppointment.getPatient().getId())
                .doctorId(requestAppointment.getDoctor().getId())
                .appointmentDateTime(requestAppointment.getAppointmentDatetime())
                .estimatedTime(requestAppointment.getAppointmentDatetime().plusMinutes(30))
                .services(List.of(
                    ScheduleServiceCreateRequest.builder()
                    .serviceId(UUID.fromString(environmentConfig.getConsultationServiceId()))
                    .build(),
                    ScheduleServiceCreateRequest.builder()
                    .serviceId(UUID.fromString(environmentConfig.getUltrasoundServiceId()))
                    .build()
                ))
                .build()
            );
        
        requestAppointment.setSchedule(schedule);
        requestAppointmentRepository.save(requestAppointment);
        return schedule;
    }

    public Schedule createSchedule(ScheduleCreateRequest scheduleCreateRequest) {
        
        User patient = userRepository.findById(scheduleCreateRequest.getPatientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        User doctor = userRepository.findById(scheduleCreateRequest.getDoctorId())
            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        if(patient.getRole().getName() != Roles.ROLE_PATIENT){
            throw new ResourceConflictException("The id for patient does not have the role of patient");
        }
        if(doctor.getRole().getName() != Roles.ROLE_DOCTOR){
            throw new ResourceConflictException("The id for doctor does not have the role of doctor");
        }

        if(scheduleCreateRequest.getAppointmentDateTime().isAfter(LocalDateTime.now().plusDays(120))) {
            throw new ResourceConflictException("Appointment time must be in the future by at most 120 days");
        }
        
        if(scheduleCreateRequest.getAppointmentDateTime().isBefore(LocalDateTime.now(timeZoneConfig.defaultZoneId()).plusDays(2))) {
            throw new ResourceConflictException("Appointment time must be in the future by at least 3 days");
        }

        if(!scheduleCreateRequest.getEstimatedTime().isAfter(scheduleCreateRequest.getAppointmentDateTime().plusMinutes(10))) {
            throw new ResourceConflictException("Estimated time must be after appointment time by at least 10 minutes");
        }

        if(scheduleCreateRequest.getEstimatedTime().isAfter(scheduleCreateRequest.getAppointmentDateTime().plusHours(2))){
            throw new ResourceConflictException("Estimated time must be at most 2 hours after appointment time");
        }
        
        List<Schedule> existingSchedules = scheduleRepository.findByDoctorIdAndStatus(
            doctor.getId(), Schedule.Status.PENDING
        );
        if(checkOverlappingSchedule(doctor.getId(),scheduleCreateRequest.getAppointmentDateTime(),scheduleCreateRequest.getEstimatedTime(),existingSchedules)){
            throw new ResourceConflictException("Doctor is already scheduled for another appointment during this time");
        }


        Schedule schedule = Schedule.builder()
        .appointmentDateTime(scheduleCreateRequest.getAppointmentDateTime())
        .estimatedTime(scheduleCreateRequest.getEstimatedTime())
        .doctor(doctor)
        .patient(patient)
        .status(Schedule.Status.PENDING)
        .build();
        
        List<com.group3.backend.model.ScheduleService> scheduleServices = scheduleCreateRequest.getServices().stream().map(scheduleServiceCreateRequest -> {
            Service service = serviceRepository.findById(scheduleServiceCreateRequest.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
            return com.group3.backend.model.ScheduleService.builder()
                .service(service)
                .schedule(schedule)
                .build();
        }).collect(Collectors.toList());

        schedule.setScheduleServices(scheduleServices);
        scheduleRepository.save(schedule);
        return schedule;
    }

    public Schedule addScheduleResult(ScheduleResultRequest scheduleResultRequest) {
        Schedule schedule = scheduleRepository.findById(scheduleResultRequest.getScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        if(schedule.getStatus() != Schedule.Status.DONE){
            throw new ResourceConflictException("Schedule is not done");
        }

        ScheduleResult scheduleResult = ScheduleResult.builder()
            .doctorsNote(scheduleResultRequest.getDoctorsNote())
            .schedule(schedule)
            .build();
        schedule.setStatus(Schedule.Status.DONE);
        schedule.setScheduleResult(scheduleResult);
        return scheduleRepository.save(schedule);
    }

    public Schedule cancelSchedule(UUID scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        schedule.setStatus(Schedule.Status.CANCELLED);
        return scheduleRepository.save(schedule);
    }

    private List<Schedule> filterDate(List<Schedule> schedules, Integer year, Integer month) {
        LocalDate startOfMonth = LocalDate.of(year, month, 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        
        schedules = schedules.stream()
                .filter(schedule -> {
                    LocalDate appointmentDate = schedule.getAppointmentDateTime().toLocalDate();
                    return !appointmentDate.isBefore(startOfMonth) && !appointmentDate.isAfter(endOfMonth);
                })
                .collect(Collectors.toList());
        return schedules;
    }

    public static boolean checkOverlappingSchedule(UUID doctorId, LocalDateTime newStart, LocalDateTime newEnd, List<Schedule> schedules) {

        for (Schedule existing : schedules) {
            LocalDateTime existStart = existing.getAppointmentDateTime();
            LocalDateTime existEnd = existing.getEstimatedTime();

            // Check overlap logic
            if ((newStart.isBefore(existEnd) && newEnd.isAfter(existStart))||
                (newStart.isBefore(existStart) && newEnd.isAfter(existStart))||
                (newStart.isBefore(existEnd) && newEnd.isAfter(existEnd))||
                (newStart.isBefore(existStart) && newEnd.isAfter(existEnd))) {
                return true;
            }
        }

        return false;
    }

    
    public List<Schedule> getAppointmentsByDoctorId(UUID doctorId, LocalDateTime start, LocalDateTime end, Schedule.Status status) {
        return scheduleRepository.findByDoctorIdAndAppointmentDateTimeBetweenAndStatus(doctorId, start, end, status);
    }

    public List<Schedule> getTodayScheduleForDoctor(UUID doctorId) {
        LocalDate today = LocalDate.now(timeZoneConfig.defaultZoneId());
        LocalDateTime start = today.atTime(8, 0);
        LocalDateTime end = today.atTime(18, 0);
        return scheduleRepository.findByDoctorIdAndAppointmentDateTimeBetween(doctorId, start, end);
    }

    public List<Schedule> getTodayScheduleForPatient(UUID patientId) {
        LocalDate today = LocalDate.now(timeZoneConfig.defaultZoneId());
        LocalDateTime start = today.atTime(8, 0);
        LocalDateTime end = today.atTime(18, 0);
        return scheduleRepository.findByPatientIdAndAppointmentDateTimeBetween(patientId, start, end);
    }

    public Schedule markScheduleAsDone(UUID scheduleId, UUID doctorId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        
        if(!schedule.getDoctor().getId().equals(doctorId)) {
            throw new UnauthorizedAccessException("You are not authorized to mark this schedule as done");
        }

        if(schedule.getStatus() != Schedule.Status.PENDING) {
            throw new ResourceConflictException("Schedule is not in pending status");
        }

        schedule.getScheduleServices().forEach(scheduleService ->{
            if(scheduleService.getPayment().getStatus() != Payment.Status.COMPLETED) {
                throw new ResourceConflictException("The payment for this schedule is not completed.");
            }
        });

        schedule.setStatus(Schedule.Status.DONE);
        return scheduleRepository.save(schedule);
    }

    public Schedule changeScheduleTime(UUID scheduleId, UUID doctorId, ScheduleChangeRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));

        if (!schedule.getDoctor().getId().equals(doctorId)) {
            throw new UnauthorizedAccessException("You are not allowed to modify this schedule");
        }

        if (schedule.getStatus() != Schedule.Status.PENDING) {
            throw new ResourceConflictException("Only PENDING schedules can be changed");
        }

        if(request.getAppointmentDateTime().isAfter(LocalDateTime.now().plusDays(120))) {
            throw new ResourceConflictException("Appointment time must be in the future by at most 120 days");
        }
        
        if(request.getAppointmentDateTime().isBefore(LocalDateTime.now(timeZoneConfig.defaultZoneId()).plusDays(2))) {
            throw new ResourceConflictException("Appointment time must be in the future by at least 3 days");
        }
        
        if(!request.getEstimatedTime().isAfter(request.getAppointmentDateTime().plusMinutes(10))) {
            throw new ResourceConflictException("Estimated time must be after appointment time by at least 10 minutes");
        }
        
        if(request.getEstimatedTime().isAfter(request.getAppointmentDateTime().plusHours(2))){
            throw new ResourceConflictException("Estimated time must be at most 2 hours after appointment time");
        }
        
        List<Schedule> existingSchedules = scheduleRepository.findByDoctorIdAndStatusAndIdNot(
            doctorId, Schedule.Status.PENDING, scheduleId
        );
        // Kiểm tra trùng lịch
        if (checkOverlappingSchedule(doctorId, request.getAppointmentDateTime(), request.getEstimatedTime(), existingSchedules)) {
            throw new ResourceConflictException("This schedule conflicts with another");
        }
        
        // Cập nhật lịch
        schedule.setAppointmentDateTime(request.getAppointmentDateTime());
        schedule.setEstimatedTime(request.getEstimatedTime());

        return scheduleRepository.save(schedule);
    }

}
