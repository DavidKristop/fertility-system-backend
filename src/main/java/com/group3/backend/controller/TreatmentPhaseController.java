package com.group3.backend.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.Treatment.TreatmentPhaseSetRequest;
import com.group3.backend.dto.response.Treatment.TreatmentPhaseResponse;
import com.group3.backend.mapper.TreatmentMapper;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.service.TreatmentPhaseService;

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

    @DeleteMapping("delete-schedule-service/{id}")
    @PreAuthorize("hasAuthority('ROLE_DOCTOR')")
    public ResponseEntity<Response<Boolean>> deleteScheduleService(@PathVariable UUID id) {
        return ResponseEntity.ok(new Response<>(treatmentPhaseService.deleteScheduleService(id),"Schedule service deleted successfully"));
    }
}
