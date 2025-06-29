package com.group3.backend.service;

import com.group3.backend.constants.Roles;
import com.group3.backend.dto.request.ScheduleCreateRequest;
import com.group3.backend.dto.request.Schedule.AddScheduleToPhaseRequest;
import com.group3.backend.dto.request.Schedule.ScheduleResultRequest;
import com.group3.backend.dto.request.Treatment.TreatmentServiceRequest;
import com.group3.backend.exception.ResourceConflictException;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.ScheduleResult;
import com.group3.backend.model.Service;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.model.User;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.ServiceRepository;
import com.group3.backend.repository.TreatmentPhaseRepository;
import com.group3.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
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
    private TreatmentPhaseRepository treatmentPhaseRepository;

    @Autowired
    private ServiceRepository serviceRepository;

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

        if(checkOverlappingSchedule(doctor.getId(),scheduleCreateRequest.getAppointmentDateTime(),scheduleCreateRequest.getEstimatedTime())){
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
                .notes(scheduleServiceCreateRequest.getNotes())
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

    public Schedule addScheduleToPhase(AddScheduleToPhaseRequest scheduleCreateRequest, UUID doctorId) {
        TreatmentPhase treatmentPhase = treatmentPhaseRepository.findById(scheduleCreateRequest.getPhaseId())
            .orElseThrow(() -> new ResourceNotFoundException("Treatment phase not found"));
        Treatment treatment = treatmentPhase.getTreatment();
        User doctor = treatment.getDoctor();
        User patient = treatment.getPatient();
        
        if(doctor.getId() != doctorId) {
            throw new UnauthorizedAccessException("Doctor is not authorized to add schedule to this treatment phase");
        }

        if(treatment.getStatus() != Treatment.Status.IN_PROGRESS){
            throw new ResourceConflictException("Treatment is not in progress");
        }

        Schedule schedule = Schedule.builder()
            .appointmentDateTime(scheduleCreateRequest.getAppointmentDateTime())
            .estimatedTime(scheduleCreateRequest.getEstimatedTime())
            .doctor(doctor)
            .patient(patient)
            .treatmentPhase(treatmentPhase)
            .status(Schedule.Status.PENDING)
            .build();
        
        if(schedule.getEstimatedTime().isBefore(schedule.getAppointmentDateTime())){
            throw new ResourceConflictException("Estimated time must be greater than appointment time");
        }

        if(schedule.getEstimatedTime().isAfter(schedule.getAppointmentDateTime().plusHours(8))){
            throw new ResourceConflictException("Estimated time must be at most 8 hours after appointment time");
        }

        if(checkOverlappingSchedule(doctor.getId(),schedule.getAppointmentDateTime(),schedule.getEstimatedTime())){
            throw new ResourceConflictException("Doctor is already scheduled for another appointment during this time");
        }
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (TreatmentServiceRequest serviceRequest : scheduleCreateRequest.getServices()){
            com.group3.backend.model.ScheduleService scheduleService = new com.group3.backend.model.ScheduleService();
            Service service = serviceRepository.findById(serviceRequest.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
            scheduleService.setService(service);
            scheduleService.setSchedule(schedule);
            scheduleService.setNotes(serviceRequest.getNotes());
            schedule.getScheduleServices().add(scheduleService);
            totalAmount = totalAmount.add(service.getPrice().multiply(BigDecimal.valueOf(serviceRequest.getAmount())));
        }
        treatmentPhase.setTotalAmount(totalAmount.add(treatmentPhase.getTotalAmount()));
        treatmentPhaseRepository.save(treatmentPhase);
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

    private boolean checkOverlappingSchedule(UUID doctorId,LocalDateTime appointmentDateTime, LocalDateTime estimatedTime){
        List<Schedule> overlappingSchedules = scheduleRepository.findByDoctorIdAndAppointmentDateTimeBetween(
            doctorId,
            appointmentDateTime,
            estimatedTime
        );
        return !overlappingSchedules.isEmpty();
    }
}
