package com.group3.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.User;
import com.group3.backend.repository.RequestAppointmentRepository;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.dto.request.RequestAppointmentRequest;
import lombok.RequiredArgsConstructor;

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

    public List<RequestAppointment> getAppointmentsByDoctorId(UUID doctorId) {
        return requestAppointmentRepository.findByDoctorId(doctorId); // Sử dụng đối tượng repository đã inject
    }

        // Phương thức để doctor chấp nhận cuộc hẹn
    public RequestAppointment acceptAppointment(UUID appointmentId) {
        RequestAppointment appointment = requestAppointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("Appointment not found"));

        // Kiểm tra trạng thái cuộc hẹn (phải là Pending mới có thể chấp nhận)
        if (!appointment.getStatus().equals(RequestAppointment.Status.Pending)) {
            throw new IllegalStateException("Appointment is already accepted or cancelled");
        }

        // Cập nhật trạng thái của cuộc hẹn
        appointment.setStatus(RequestAppointment.Status.Accept);

        return requestAppointmentRepository.save(appointment); // Lưu lại sự thay đổi
    }

}
