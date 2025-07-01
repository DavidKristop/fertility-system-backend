package com.group3.backend.service;

import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.Treatment;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.TreatmentRepository;
import com.group3.backend.repository.RequestAppointmentRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.User;
import com.group3.backend.repository.UserRepository;
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

        if(dto.getAppointmentDatetime().isBefore(LocalDateTime.now().plusDays(3))){
            throw new ResourceConflictException("Appointment must be at least 3 days in advance");
        }

        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
     
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
        LocalDateTime appointmentEnd = appointmentStart.plusMinutes(45);

        // Find overlapping schedules for the same doctor
        List<Schedule> overlappingSchedules = scheduleRepository.findByDoctorIdAndAppointmentDateTimeBetween(
            appointment.getDoctor().getId(),
            appointmentStart,
            appointmentEnd
        );

        if (!overlappingSchedules.isEmpty()) {
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

        // Cập nhật trạng thái của cuộc hẹn
        appointment.setStatus(RequestAppointment.Status.ACCEPTED);

        return requestAppointmentRepository.save(appointment);
    }

    public RequestAppointment cancelAppointment(UUID appointmentId, UUID doctorId) {
        RequestAppointment appointment = requestAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
        
        if(!appointment.getDoctor().getId().equals(doctorId)) {
            throw new UnauthorizedAccessException("Doctor is not authorized to cancel this appointment");
        }
            
        if (!appointment.getStatus().equals(RequestAppointment.Status.PENDING)) {
            throw new IllegalStateException("Appointment is already accepted or cancelled");
        }

        appointment.setStatus(RequestAppointment.Status.DENIED);

        return requestAppointmentRepository.save(appointment);
    }

}
