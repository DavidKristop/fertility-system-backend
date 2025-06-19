package com.group3.backend.service;

import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.User;
import com.group3.backend.repository.RequestAppointmentRepository;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.dto.request.RequestAppointmentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequestAppointmentService {

    private final RequestAppointmentRepository requestAppointmentRepository;
    private final UserRepository userRepository;

    public RequestAppointment createRequestAppointment(RequestAppointmentRequest dto) {
        User doctor = userRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));
        User patient = userRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new IllegalArgumentException("Patient not found"));

        RequestAppointment request = RequestAppointment.builder()
                .doctor(doctor)
                .patient(patient)
                .reason(dto.getReason())
                .appointmentDatetime(dto.getAppointmentDatetime())
                .status(RequestAppointment.Status.Pending) // mặc định là Pending
                .build();

        return requestAppointmentRepository.save(request);
    }
}
