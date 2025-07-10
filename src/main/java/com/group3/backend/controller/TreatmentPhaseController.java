package com.group3.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.Treatment.TreatmentPhaseSetRequest;
import com.group3.backend.dto.response.Treatment.TreatmentPhaseResponse;
import com.group3.backend.mapper.TreatmentMapper;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.service.TreatmentPhaseService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/treatment-phases")
public class TreatmentPhaseController {
    @Autowired
    private TreatmentPhaseService treatmentPhaseService;

    @Autowired
    private TreatmentMapper treatmentMapper;

    @PutMapping("set")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<TreatmentPhaseResponse>> setTreatmentPhase(@RequestBody TreatmentPhaseSetRequest request) {
        TreatmentPhase treatmentPhase = treatmentPhaseService.setTreatmentPhase(request);
        return ResponseEntity.ok(new Response<>(treatmentMapper.map(treatmentPhase), "Treatment phase set successfully"));
    }
}
