package com.group3.backend.controller;

import com.group3.backend.dto.request.RequestAppointmentRequest;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.service.RequestAppointmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
