package com.group3.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.Treatment.TreatmentAssignDrugReponse;
import com.group3.backend.mapper.TreatmentMapper;
import com.group3.backend.model.AssignDrug;
import com.group3.backend.service.AssignDrugService;
import com.group3.backend.utils.CurrentUserUtils;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assign-drug")
@RequiredArgsConstructor
public class AssignDrugController {
    private final AssignDrugService assignDrugService;

    private final CurrentUserUtils currentUserUtils;

    private final TreatmentMapper treatmentMapper;

    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<Page<TreatmentAssignDrugReponse>>> getAssignDrugByPatientId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "PENDING") List<AssignDrug.Status> status

    ){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new Response<>(assignDrugService.getAssignDrugByPatientId(currentUserUtils.getCurrentUserId(), status, pageable).map(treatmentMapper::toTreatmentAssignDrugResponse), "Assign drug retrieved successfully"));
    }

    @GetMapping("/patient/{assignDrugId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<TreatmentAssignDrugReponse>> getAssignDrugByIdAndPatientId(@PathVariable UUID assignDrugId){
        return ResponseEntity.ok(new Response<>(treatmentMapper.toTreatmentAssignDrugResponse(assignDrugService.getAssignDrugByIdAndPatientId(currentUserUtils.getCurrentUserId(), assignDrugId)), "Assign drug retrieved successfully"));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Page<TreatmentAssignDrugReponse>>> getAssignDrugByManagerId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "PENDING") List<AssignDrug.Status> status
    ){
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new Response<>(assignDrugService.getAssignDrugByStatus(status, pageable).map(treatmentMapper::toTreatmentAssignDrugResponse), "Assign drug retrieved successfully"));
    }

    @GetMapping("/manager/{assignDrugId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<TreatmentAssignDrugReponse>> getAssignDrugById(@PathVariable UUID assignDrugId){
        return ResponseEntity.ok(new Response<>(treatmentMapper.toTreatmentAssignDrugResponse(assignDrugService.getAssignDrugById(assignDrugId)), "Assign drug retrieved successfully"));
    }

    @PutMapping("/manager/{assignDrugId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<TreatmentAssignDrugReponse>> completeAssignDrug(@PathVariable UUID assignDrugId){
        return ResponseEntity.ok(new Response<>(treatmentMapper.toTreatmentAssignDrugResponse(assignDrugService.completeAssignDrug(assignDrugId)), "Assign drug completed successfully"));
    }
}
