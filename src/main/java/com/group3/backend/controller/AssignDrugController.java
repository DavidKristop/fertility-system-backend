package com.group3.backend.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.AssignDrug.AssignDrugResponse;
import com.group3.backend.mapper.AssignDrugMapper;
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

    private final AssignDrugMapper assignDrugMapper;

    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<Page<AssignDrugResponse>>> getAssignDrugByPatientId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "PENDING") List<AssignDrug.Status> status,
        @RequestParam(defaultValue = "") String title
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(new Response<>(assignDrugService.getAssignDrugByPatientId(currentUserUtils.getCurrentUserId(), status, title, pageable).map(assignDrugMapper::toAssignDrugResponse), "Assign drug retrieved successfully"));
    }

    @GetMapping("/patient/{assignDrugId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<AssignDrugResponse>> getAssignDrugByIdAndPatientId(@PathVariable UUID assignDrugId){
        return ResponseEntity.ok(new Response<>(assignDrugMapper.toAssignDrugResponse(assignDrugService.getAssignDrugByIdAndPatientId(currentUserUtils.getCurrentUserId(), assignDrugId)), "Assign drug retrieved successfully"));
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<Page<AssignDrugResponse>>> getAssignDrugByDoctorId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "PENDING") List<AssignDrug.Status> status,
        @RequestParam(defaultValue = "") String title
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(new Response<>(assignDrugService.getAssignDrugByDoctorId(currentUserUtils.getCurrentUserId(), status, title, pageable).map(assignDrugMapper::toAssignDrugResponse), "Assign drug retrieved successfully"));
    }

    @GetMapping("/doctor/{assignDrugId}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<AssignDrugResponse>> getAssignDrugByIdAndDoctorId(@PathVariable UUID assignDrugId){
        return ResponseEntity.ok(new Response<>(assignDrugMapper.toAssignDrugResponse(assignDrugService.getAssignDrugByIdAndDoctorId(currentUserUtils.getCurrentUserId(), assignDrugId)), "Assign drug retrieved successfully"));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Page<AssignDrugResponse>>> getAssignDrugByManagerId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "PENDING") List<AssignDrug.Status> status,
        @RequestParam(defaultValue = "") String title
    ){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return ResponseEntity.ok(new Response<>(assignDrugService.getAssignDrugByStatus(status, title, pageable).map(assignDrugMapper::toAssignDrugResponse), "Assign drug retrieved successfully"));
    }

    @GetMapping("/manager/{assignDrugId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<AssignDrugResponse>> getAssignDrugById(@PathVariable UUID assignDrugId){
        return ResponseEntity.ok(new Response<>(assignDrugMapper.toAssignDrugResponse(assignDrugService.getAssignDrugById(assignDrugId)), "Assign drug retrieved successfully"));
    }

    @PutMapping("/manager/complete/{assignDrugId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<AssignDrugResponse>> completeAssignDrug(@PathVariable UUID assignDrugId){
        return ResponseEntity.ok(new Response<>(assignDrugMapper.toAssignDrugResponse(assignDrugService.completeAssignDrug(assignDrugId)), "Assign drug completed successfully"));
    }

    @PutMapping("/manager/cancel/{assignDrugId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<AssignDrugResponse>> cancelAssignDrug(@PathVariable UUID assignDrugId){
        return ResponseEntity.ok(new Response<>(assignDrugMapper.toAssignDrugResponse(assignDrugService.cancelAssignDrug(assignDrugId)), "Assign drug cancelled successfully"));
    }
}
