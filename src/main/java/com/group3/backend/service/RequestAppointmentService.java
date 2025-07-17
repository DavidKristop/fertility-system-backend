package com.group3.backend.service;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.Treatment;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.TreatmentRepository;
import com.group3.backend.repository.ReminderRepository;
import com.group3.backend.repository.RequestAppointmentRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group3.backend.model.Reminder;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.User;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.utils.Constants;
import com.group3.backend.dto.request.RequestAppointmentRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.exception.ResourceConflictException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestAppointmentService {

    private final RequestAppointmentRepository requestAppointmentRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final TreatmentRepository treatmentRepository;
    private final ReminderRepository reminderRepository;

    public RequestAppointment createRequestAppointment(RequestAppointmentRequest dto, UUID patientId) {
        
        if(requestAppointmentRepository.existsByPatientIdAndStatusIn(patientId, List.of(RequestAppointment.Status.PENDING))) {
            throw new ResourceConflictException("Patient already has a pending appointment");
        }

        if(requestAppointmentRepository.existsByPatientIdAndScheduleStatusIn(patientId, List.of(Schedule.Status.PENDING))) {
            throw new ResourceConflictException("Patient already has a pending consultation schedule");
        }

        if(treatmentRepository.existsByPatientIdAndStatusIn(patientId, List.of(Treatment.Status.IN_PROGRESS, Treatment.Status.AWAITING_CONTRACT_SIGNED))) {
            throw new ResourceConflictException("Patient already has an in-progress or awaiting contract signed treatment");
        }

        if(dto.getAppointmentDatetime().isBefore(LocalDateTime.now().plusDays(2))){
            throw new ResourceConflictException("Appointment must be at least 3 days in advance");
        }

        if(dto.getAppointmentDatetime().isAfter(LocalDateTime.now().plusDays(119))) {
            throw new ResourceConflictException("Appointment time must be in the future by at most 120 days");
        }

        
        User doctor = userRepository.findById(dto.getDoctorId())
        .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        User patient = userRepository.findById(patientId)
        .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        
        List<Schedule> overlappingSchedules = scheduleRepository.findByDoctorIdAndStatus(doctor.getId(), Schedule.Status.PENDING);

        if (ScheduleService.checkOverlappingSchedule(doctor.getId(), dto.getAppointmentDatetime(), dto.getAppointmentDatetime().plusMinutes(Constants.REQUEST_APPOINTMENT_ESTIMATED_TIME), overlappingSchedules)) {
            throw new ResourceConflictException("Doctor is already scheduled for another appointment during this time");
        }
     
        RequestAppointment request = RequestAppointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDatetime(dto.getAppointmentDatetime())
                .status(RequestAppointment.Status.PENDING) 
                .build();

        return requestAppointmentRepository.save(request);
    }

    public List<RequestAppointment> getAppointmentsByDoctorId(UUID doctorId) {
        return requestAppointmentRepository.findByDoctorId(doctorId);
    }

    public List<RequestAppointment> getAppointmentsByPatientId(UUID patientId) {
        return requestAppointmentRepository.findByPatientId(patientId); 
    }

    public Page<RequestAppointment> getDoctorsAppointments(UUID doctorId, String patientEmail, List<RequestAppointment.Status> statuses, LocalDateTime deadline, Pageable pageable) {
        return requestAppointmentRepository.findByDoctorIdAndPatientEmailContainingIgnoreCaseAndStatusInAndAppointmentDatetimeAfter(doctorId, patientEmail, statuses, deadline, pageable);
    }

    public Page<RequestAppointment> getPatientsAppointments(UUID patientId, String doctorEmail, List<RequestAppointment.Status> statuses, LocalDateTime deadline, Pageable pageable) {
        return requestAppointmentRepository.findByPatientIdAndDoctorEmailContainingIgnoreCaseAndStatusInAndAppointmentDatetimeAfter(patientId, doctorEmail, statuses, deadline, pageable);
    }

    @Transactional
    public RequestAppointment acceptAppointment(UUID appointmentId, UUID doctorId) {
        RequestAppointment appointment = requestAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        if(appointment.getAppointmentDatetime().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new ResourceConflictException("Appointment must be accepted 24 hours before.");
        }
        
        if(!appointment.getDoctor().getId().equals(doctorId)) {
            throw new UnauthorizedAccessException("Doctor is not authorized to accept this appointment");
        }
            
        if (!appointment.getStatus().equals(RequestAppointment.Status.PENDING)) {
            throw new IllegalStateException("Appointment is already accepted or cancelled");
        }
        // Check for schedule overlap
        LocalDateTime appointmentStart = appointment.getAppointmentDatetime();
        LocalDateTime appointmentEnd = appointmentStart.plusMinutes(Constants.REQUEST_APPOINTMENT_ESTIMATED_TIME);

        // Find overlapping schedules for the same doctor
        List<Schedule> overlappingSchedules = scheduleRepository.findByDoctorIdAndStatus(doctorId, Schedule.Status.PENDING);

        if (ScheduleService.checkOverlappingSchedule(doctorId, appointmentStart, appointmentEnd, overlappingSchedules)) {
            throw new ResourceConflictException("Doctor is already scheduled for another appointment during this time");
        }

        // Find overlapping schedules for the same patient
        List<Schedule> overlappingAppointments = scheduleRepository.findByPatientIdAndAppointmentDateTimeBetween(
            appointment.getPatient().getId(),
            appointmentStart,
            appointmentEnd
        );

        if (!overlappingAppointments.isEmpty()) {
            throw new ResourceConflictException("Patient already has another appointment during this time");
        }

        setAllOverlappingAppointmentsToDenied(appointment.getDoctor().getId(), appointment.getId(), appointmentStart, appointmentEnd);

        // Cập nhật trạng thái của cuộc hẹn
        appointment.setStatus(RequestAppointment.Status.ACCEPTED);
        reminderRepository.save(Reminder.builder()
                .title("Appointment Reminder")
                .content("Congratulations! Your requested appointment with " + appointment.getDoctor().getFullName() + " on " + appointment.getAppointmentDatetime() +" has been accepted.")
                .sendTo(appointment.getPatient())
                .build());
        return requestAppointmentRepository.save(appointment);
    }

    public RequestAppointment cancelAppointment(UUID appointmentId, UUID doctorId, String rejectedReason) {
        RequestAppointment appointment = requestAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        if(!appointment.getDoctor().getId().equals(doctorId)) {
            throw new UnauthorizedAccessException("Doctor is not authorized to cancel this appointment");
        }
            
        if (!appointment.getStatus().equals(RequestAppointment.Status.PENDING)) {
            throw new IllegalStateException("Appointment has already been accepted or cancelled");
        }

        appointment.setStatus(RequestAppointment.Status.DENIED);
        appointment.setRejectedReason(rejectedReason);
        reminderRepository.save(Reminder.builder()
                .title("Appointment request Denied")
                .content("We're sorry, but your appointment request with " + appointment.getDoctor().getFullName() + " on " + appointment.getAppointmentDatetime() + " has been cancelled. Reason: " + rejectedReason)
                .sendTo(appointment.getPatient())
                .build());
        return requestAppointmentRepository.save(appointment);
    }

    private void setAllOverlappingAppointmentsToDenied(UUID doctorId, UUID excludeAppointmentId, LocalDateTime newStart, LocalDateTime newEnd){

        List<RequestAppointment> overlappingAppointments = requestAppointmentRepository.findByDoctorIdAndStatusAndIdNot(doctorId, RequestAppointment.Status.PENDING, excludeAppointmentId);

        for (RequestAppointment appointment : overlappingAppointments) {
            LocalDateTime appointmentStart = appointment.getAppointmentDatetime();
            LocalDateTime appointmentEnd = appointmentStart.plusMinutes(Constants.REQUEST_APPOINTMENT_ESTIMATED_TIME);

            // Check if the appointment overlaps with the new time
            if ((newStart.isBefore(appointmentEnd) && newEnd.isAfter(appointmentStart))||
                (newStart.isBefore(appointmentStart) && newEnd.isAfter(appointmentStart))||
                (newStart.isBefore(appointmentEnd) && newEnd.isAfter(appointmentEnd))||
                (newStart.isBefore(appointmentStart) && newEnd.isAfter(appointmentEnd))) {
                appointment.setStatus(RequestAppointment.Status.DENIED);
                appointment.setRejectedReason("Your request has been rejected because the doctor has already accepted another appointment that overlaps with your schedule.");
                reminderRepository.save(Reminder.builder()
                        .title("Appointment request Denied")
                        .content("We're sorry, but your appointment request with " + appointment.getDoctor().getFullName() + " on " + appointment.getAppointmentDatetime() + " has been denied. Because the doctor has already accepted another appointment that overlaps with your schedule.")
                        .sendTo(appointment.getPatient())
                        .build());
            }
        }

        requestAppointmentRepository.saveAll(overlappingAppointments);
    }

}
