package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.TreatmentProtocolCreateRequest;
import com.group3.backend.dto.response.TreatmentProtocolResponse;
import com.group3.backend.exception.ValidationException;
import com.group3.backend.mapper.ProtocolMapper;
import com.group3.backend.model.TreatmentProtocol;
import com.group3.backend.service.TreatmentProtocolServiceService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/protocols")
public class TreatmentProtocolController {

    @Autowired
    private TreatmentProtocolServiceService protocolService;

    @Autowired
    private ProtocolMapper protocolMapper;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<TreatmentProtocolResponse>> createProtocol(@RequestBody @Valid TreatmentProtocolCreateRequest request) {
        TreatmentProtocol protocol = protocolService.createProtocol(request);
        TreatmentProtocolResponse response = protocolMapper.toResponse(protocol);
        return ResponseEntity.ok(new Response<>(response, "Protocol created successfully"));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Page<TreatmentProtocolResponse>>> getAllProtocolsManager(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "true") boolean active) {
        return getAllProtocols(page, size, search, active);
    }

    @GetMapping
    public ResponseEntity<Response<Page<TreatmentProtocolResponse>>> getAllProtocolsActive(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String search) {
        return getAllProtocols(page, size, search, true);
    }
            

    private ResponseEntity<Response<Page<TreatmentProtocolResponse>>> getAllProtocols(
            int page,
            int size,
            String search,
            boolean active) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<TreatmentProtocol> protocols = protocolService.getProtocols(search, active, pageable);

        Page<TreatmentProtocolResponse> responses = protocols.map(protocolMapper::toResponse);
        return ResponseEntity.ok(new Response<>(responses, "Protocols retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<TreatmentProtocolResponse>> getProtocolById(@PathVariable UUID id) {
        TreatmentProtocol protocol = protocolService.getProtocolById(id);
        if(!protocol.isActive()){
            throw new ValidationException("Protocol is not active");
        }
        TreatmentProtocolResponse response = protocolMapper.toResponse(protocol);
        return ResponseEntity.ok(new Response<>(response, "Protocol retrieved successfully"));
    }

    @GetMapping("/manager/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<TreatmentProtocolResponse>> getProtocolByIdManager(@PathVariable UUID id) {
        TreatmentProtocol protocol = protocolService.getProtocolById(id);
        TreatmentProtocolResponse response = protocolMapper.toResponse(protocol);
        return ResponseEntity.ok(new Response<>(response, "Protocol retrieved successfully"));
    }

    @PostMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Void>> deactivateProtocol(@PathVariable UUID id) {
        protocolService.deactivateProtocol(id);
        return ResponseEntity.ok(new Response<>(null, "Protocol deactivated successfully"));
    }

    @PostMapping("/activate/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Void>> activateProtocol(@PathVariable UUID id) {
        protocolService.activateProtocol(id);
        return ResponseEntity.ok(new Response<>(null, "Protocol activated successfully"));
    }
}
