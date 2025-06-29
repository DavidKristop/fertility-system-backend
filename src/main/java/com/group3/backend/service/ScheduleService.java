package com.group3.backend.service;

import com.group3.backend.dto.request.Schedule.ScheduleChangeRequest;
import com.group3.backend.dto.request.Schedule.ScheduleCreateRequest;
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
import com.group3.backend.model.Payment;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.ServiceRepository;
import com.group3.backend.repository.TreatmentPhaseRepository;

import com.group3.backend.config.TimeZoneConfig;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    private TimeZoneConfig timeZoneConfig;

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

    public Schedule addScheduleToPhase(ScheduleCreateRequest scheduleCreateRequest, UUID doctorId) {
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
        
        if(schedule.getEstimatedTime().getTime() <= schedule.getAppointmentDateTime().getTime()){
            throw new ResourceConflictException("Estimated time must be greater than appointment time");
        }

        if(schedule.getEstimatedTime().getTime() - schedule.getAppointmentDateTime().getTime() > 8 * 60 * 60 * 1000){
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
            scheduleService.setAmount(serviceRequest.getAmount());
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
                    LocalDate appointmentDate = schedule.getAppointmentDateTime().toLocalDateTime().toLocalDate();
                    return !appointmentDate.isBefore(startOfMonth) && !appointmentDate.isAfter(endOfMonth);
                })
                .collect(Collectors.toList());
        return schedules;
    }

    private boolean checkOverlappingSchedule(UUID doctorId, Timestamp appointmentDateTime, Timestamp estimatedTime) {
        List<Schedule> overlappingSchedules = scheduleRepository.findByDoctorIdAndAppointmentDateTimeBetween(
            doctorId,
            appointmentDateTime,
            estimatedTime
        );
        return !overlappingSchedules.isEmpty();
    }

    // Overloaded method to exclude a specific scheduleId (for updates)
    private boolean checkOverlappingSchedule(UUID doctorId, Timestamp newStart, Timestamp newEnd, UUID excludeScheduleId) {
        List<Schedule> schedules = scheduleRepository.findByDoctorId(doctorId);

    for (Schedule existing : schedules) {
        if (existing.getId().equals(excludeScheduleId)) continue;
        if (existing.getStatus() == Schedule.Status.CANCELLED) continue;

        Timestamp existStart = existing.getAppointmentDateTime();
        Timestamp existEnd = existing.getEstimatedTime();

        // Check overlap logic
        if (newStart.before(existEnd) && newEnd.after(existStart)) {
            return true;
        }
    }

    return false;
    }

    public List<Schedule> getTodayScheduleForDoctor(UUID doctorId) {
        LocalDate today = LocalDate.now(timeZoneConfig.defaultZoneId());
        Timestamp start = Timestamp.valueOf(today.atTime(8, 0));
        Timestamp end = Timestamp.valueOf(today.atTime(18, 0));
        return scheduleRepository.findByDoctorIdAndAppointmentDateTimeBetween(doctorId, start, end);
    }

    public List<Schedule> getTodayScheduleForPatient(UUID patientId) {
        LocalDate today = LocalDate.now(timeZoneConfig.defaultZoneId());
        Timestamp start = Timestamp.valueOf(today.atTime(8, 0));
        Timestamp end = Timestamp.valueOf(today.atTime(18, 0));
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

        if(schedule.getPayment() == null || schedule.getPayment().getStatus() != Payment.Status.COMPLETED) {
            throw new ResourceConflictException("Schedule payment is not completed");
        }

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

    if (schedule.getPayment().getStatus() == Payment.Status.CANCELED) {
        throw new ResourceConflictException("Cannot modify schedule with cancelled payment");
    }

    // Check thời gian hợp lệ
    Timestamp now = Timestamp.valueOf(LocalDateTime.now());
    Treatment treatment = schedule.getTreatmentPhase().getTreatment();
    if (request.getAppointmentDateTime().before(now) ||
        request.getAppointmentDateTime().after(Timestamp.valueOf(treatment.getEndDate().toInstant().atZone(timeZoneConfig.defaultZoneId()).toLocalDate().atStartOfDay()))) {
        throw new ResourceConflictException("Appointment time must be within treatment period");
    }

    // EstimatedTime phải sau appointmentTime
    if (!request.getEstimatedTime().after(request.getAppointmentDateTime())) {
        throw new ResourceConflictException("Estimated time must be after appointment time");
    }

    // Thời gian khám <= 8 giờ
    long duration = request.getEstimatedTime().getTime() - request.getAppointmentDateTime().getTime();
    if (duration > 8 * 60 * 60 * 1000) {
        throw new ResourceConflictException("Estimated time must not exceed 8 hours");
    }

    // Kiểm tra trùng lịch
    if (checkOverlappingSchedule(doctorId, request.getAppointmentDateTime(), request.getEstimatedTime(), scheduleId)) {
        throw new ResourceConflictException("This schedule conflicts with another");
    }

    // Cập nhật lịch
    schedule.setAppointmentDateTime(request.getAppointmentDateTime());
    schedule.setEstimatedTime(request.getEstimatedTime());

    return scheduleRepository.save(schedule);
}

}
