package com.group3.backend.service;

import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;
import org.springframework.transaction.annotation.Transactional;
import com.group3.backend.model.Schedule;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.RequestAppointmentRepository;

import org.springframework.stereotype.Service;

import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.User;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.dto.request.RequestAppointmentRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.ResourceConflictException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestAppointmentService {

    private final RequestAppointmentRepository requestAppointmentRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;

    public RequestAppointment createRequestAppointment(RequestAppointmentRequest dto) {
        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        User patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        RequestAppointment request = RequestAppointment.builder()
                .doctor(doctor)
                .patient(patient)
                .reason(dto.getReason())
                .appointmentDatetime(dto.getAppointmentDatetime())
                .status(RequestAppointment.Status.PENDING) // mặc định là Pending
                .build();

        return requestAppointmentRepository.save(request);
    }

    public List<RequestAppointment> getAppointmentsByDoctorId(UUID doctorId) {
        return requestAppointmentRepository.findByDoctorId(doctorId);
    }

    public List<RequestAppointment> getAppointmentsByPatientId(UUID patientId) {
        return requestAppointmentRepository.findByPatientId(patientId); 
    }

    @Transactional
    public RequestAppointment acceptAppointment(UUID appointmentId) {
        RequestAppointment appointment = requestAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        // Kiểm tra trạng thái cuộc hẹn (phải là Pending mới có thể chấp nhận)
        if (!appointment.getStatus().equals(RequestAppointment.Status.PENDING)) {
            throw new IllegalStateException("Appointment is already accepted or cancelled");
        }

        // Check for schedule overlap
        Timestamp appointmentStart = appointment.getAppointmentDatetime();
        Timestamp appointmentEnd = new Timestamp(appointmentStart.getTime() + 45 * 60 * 1000);

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

}
