package com.group3.backend.controller;

import com.group3.backend.dto.request.Treatment.POST.TreatmentCreateRequest;
import com.group3.backend.dto.response.Treatment.TreatmentResponse;
import com.group3.backend.mapper.TreatmentMapper;
import com.group3.backend.dto.Response;
import com.group3.backend.model.Treatment;
import com.group3.backend.service.TreatmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/treatments")
public class TreatmentController {
    
    @Autowired
    private TreatmentService treatmentService;

    @Autowired
    private TreatmentMapper treatmentMapper;

    @PostMapping
    public ResponseEntity<Response<TreatmentResponse>> createTreatment(@RequestBody TreatmentCreateRequest request) {
        Treatment treatment = treatmentService.createTreatment(request);
        TreatmentResponse response = treatmentMapper.toResponse(treatment);
        return ResponseEntity.ok(new Response<>(response, "Treatment created successfully"));
    }
}
