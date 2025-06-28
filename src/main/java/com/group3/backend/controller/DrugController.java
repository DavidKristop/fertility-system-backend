package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.DrugCreateRequest;
import com.group3.backend.dto.request.DrugUpdateRequest;
import com.group3.backend.dto.response.DrugResponse;
import com.group3.backend.mapper.DrugMapper;
import com.group3.backend.model.Drug;
import com.group3.backend.service.DrugService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/drugs")
public class DrugController {
    @Autowired
    private DrugService drugService;

    @Autowired
    private DrugMapper drugMapper;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_DOCTOR')")
    public ResponseEntity<Response<Page<DrugResponse>>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "true") boolean isActive) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Drug> drugs = drugService.searchDrugs(name, isActive, pageable);
        Page<DrugResponse> drugResponses = drugs.map(drugMapper::toResponse);
        return ResponseEntity.ok(new Response<>(drugResponses, "Drugs retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_DOCTOR')")
    public ResponseEntity<Response<DrugResponse>> get(@PathVariable UUID id) {
        DrugResponse drugResponse = drugMapper.toResponse(drugService.getDrugById(id));
        return ResponseEntity.ok(new Response<>(drugResponse, "Drug retrieved successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<DrugResponse>> create(@RequestBody @Valid DrugCreateRequest request) {
        Drug drug = drugService.createDrug(request);
        DrugResponse response = drugMapper.toResponse(drug);
        return ResponseEntity.ok(new Response<>(response, "Drug created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<DrugResponse>> update(@PathVariable UUID id, @RequestBody @Valid DrugUpdateRequest request) {
        Drug drug = drugService.updateDrug(id, request);
        DrugResponse response = drugMapper.toResponse(drug);
        return ResponseEntity.ok(new Response<>(response, "Drug updated successfully"));
    }

    @PostMapping("/deactivate/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Void>> deactivate(@PathVariable UUID id) {
        drugService.deactivateDrug(id);
        return ResponseEntity.ok(new Response<>(null, "Drug deactivated successfully"));
    }

    @PostMapping("/reactivate/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Void>> reactivate(@PathVariable UUID id) {
        drugService.reactivateDrug(id);
        return ResponseEntity.ok(new Response<>(null, "Drug reactivated successfully"));
    }
}
