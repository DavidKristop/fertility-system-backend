package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.ServiceCreateRequest;
import com.group3.backend.dto.request.ServiceUpdateRequest;
import com.group3.backend.dto.response.ServiceResponse;
import com.group3.backend.mapper.ServiceMapper;
import com.group3.backend.service.ServiceItemService;
import com.group3.backend.model.Service;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private ServiceMapper serviceMapper;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_DOCTOR')")
    public ResponseEntity<Response<Page<ServiceResponse>>> list(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String name,
        @RequestParam(defaultValue = "true") boolean isActive){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Service> services = serviceItemService.getServices(name, isActive, pageable);
        return ResponseEntity.ok(new Response<>(services.map(serviceMapper::toResponse), "Services retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_DOCTOR')")
    public ResponseEntity<Response<ServiceResponse>> get(@PathVariable UUID id){
        ServiceResponse serviceResponse = serviceMapper.toResponse(serviceItemService.getServiceById(id));
        return ResponseEntity.ok(new Response<>(serviceResponse, "Service retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<ServiceResponse>> create(@RequestBody @Valid ServiceCreateRequest request) {
        Service service = serviceItemService.createService(request);
        ServiceResponse response = serviceMapper.toResponse(service);
        return ResponseEntity.ok(new Response<>(response, "Service created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<ServiceResponse>> update(@PathVariable UUID id, @RequestBody @Valid ServiceUpdateRequest request) {
        Service service = serviceItemService.updateService(id, request);
        ServiceResponse response = serviceMapper.toResponse(service);
        return ResponseEntity.ok(new Response<>(response, "Service updated successfully"));
    }

    @PostMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Void>> deactivate(@PathVariable UUID id) {
        serviceItemService.deactivateService(id);
        return ResponseEntity.ok(new Response<>(null, "Service deactivated successfully"));
    }

    @PostMapping("/reactivate/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Void>> reactivate(@PathVariable UUID id) {
        serviceItemService.reActivateService(id);
        return ResponseEntity.ok(new Response<>(null, "Service reactivated successfully"));
    }
}
