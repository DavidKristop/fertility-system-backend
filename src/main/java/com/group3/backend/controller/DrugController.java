package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.DrugCreateRequest;
import com.group3.backend.dto.response.DrugResponse;
import com.group3.backend.mapper.DrugMapper;
import com.group3.backend.model.Drug;
import com.group3.backend.service.DrugService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/drugs")
public class DrugController {
    @Autowired
    private DrugService drugService;

    @Autowired
    private DrugMapper drugMapper;

    @GetMapping
    public ResponseEntity<Response<List<DrugResponse>>> list(@RequestParam(defaultValue = "true") boolean onlyActive){
        return ResponseEntity.ok(new Response<>(drugService.getAllDrugs(onlyActive).stream()
                .map(drugMapper::toResponse)
                .collect(Collectors.toList()), 
                "Drugs retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<DrugResponse>> get(@PathVariable UUID id){
        DrugResponse drugResponse = drugMapper.toResponse(drugService.getDrugById(id));
        return ResponseEntity.ok(new Response<>(drugResponse, "Drug retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<Response<DrugResponse>> create(@RequestBody @Valid DrugCreateRequest request) {
        Drug drug = drugService.createDrug(request);
        DrugResponse response = drugMapper.toResponse(drug);
        return ResponseEntity.ok(new Response<>(response, "Drug created successfully"));
    }
}
