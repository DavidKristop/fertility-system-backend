package com.group3.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.request.RequestAppointmentRequest;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.service.RequestAppointmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/request-appointments")
@RequiredArgsConstructor
public class RequestAppointmentController {

    private final RequestAppointmentService service;

    @PostMapping
    public ResponseEntity<RequestAppointment> createRequest(@RequestBody RequestAppointmentRequest request) {
        RequestAppointment result = service.createRequestAppointment(request);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<RequestAppointment>> getAppointmentsByDoctor(@PathVariable("doctorId") UUID doctorId) {
        // Lấy danh sách các cuộc hẹn theo doctorId
        List<RequestAppointment> appointments = service.getAppointmentsByDoctorId(doctorId);

        // Kiểm tra nếu không có cuộc hẹn nào
        if (appointments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }



        return ResponseEntity.ok(appointments);
    }

        // API mới để doctor chấp nhận cuộc hẹn
    @PutMapping("/accept/{appointmentId}")
    public ResponseEntity<RequestAppointment> acceptAppointment(@PathVariable UUID appointmentId) {
        RequestAppointment acceptedAppointment = service.acceptAppointment(appointmentId);
        return ResponseEntity.ok(acceptedAppointment);
    }

}
