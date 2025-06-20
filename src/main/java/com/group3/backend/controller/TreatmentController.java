package com.group3.backend.controller;

import com.group3.backend.dto.request.Treatment.POST.TreatmentCreateRequest;
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

    @PostMapping
    public ResponseEntity<Response<Treatment>> createTreatment(@RequestBody TreatmentCreateRequest request) {
        Treatment treatment = treatmentService.createTreatment(request);
        Response<Treatment> response = new Response<Treatment>(treatment, "Treatment created successfully");
        return ResponseEntity.ok(response);
    }
}
