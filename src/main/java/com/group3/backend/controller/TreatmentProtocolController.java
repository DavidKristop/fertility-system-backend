package com.group3.backend.controller;

import com.group3.backend.dto.response.Protocol.ProtocolResponse;
import com.group3.backend.mapper.ProtocolMapper;
import com.group3.backend.model.TreatmentProtocol;
import com.group3.backend.service.TreatmentProtocolServiceService;

import jakarta.validation.Valid;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.Protocol.ProtocolCreateRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/protocols")
public class TreatmentProtocolController {

    @Autowired
    private TreatmentProtocolServiceService protocolService;

    @Autowired
    private ProtocolMapper protocolMapper;

    @PostMapping
    public ResponseEntity<Response<ProtocolResponse>> createProtocol(@RequestBody @Valid ProtocolCreateRequest request) {
        TreatmentProtocol protocol = protocolService.createProtocol(request);
        ProtocolResponse response = protocolMapper.toResponse(protocol);
        return ResponseEntity.ok(new Response<>(response, "Protocol created successfully"));
    }

    @GetMapping
    public ResponseEntity<Response<List<ProtocolResponse>>> getAllProtocols() {
        List<ProtocolResponse> protocols = protocolService.getAllProtocol()
            .stream()
            .map(protocolMapper::toResponse)
            .toList();
        return ResponseEntity.ok(new Response<>(protocols, "Protocols retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ProtocolResponse>> getProtocolById(@PathVariable UUID id) {
        ProtocolResponse protocol = protocolMapper.toResponse(protocolService.getProtocolById(id));
        return ResponseEntity.ok(new Response<>(protocol, "Protocol retrieved successfully"));
    }
}
