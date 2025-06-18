package com.group3.backend.controller;

import com.group3.backend.dto.response.DrugResponse;
import com.group3.backend.service.DrugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/drugs")
public class DrugController {
    @Autowired
    private DrugService drugService;

    @GetMapping
    public List<DrugResponse> list(@RequestParam(defaultValue = "true") boolean onlyActive){
        return drugService.getAllDrugs(onlyActive);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DrugResponse> get(@PathVariable UUID id){
        DrugResponse drugResponse = drugService.getDrugById(id);
        return drugResponse != null ? ResponseEntity.ok(drugResponse) : ResponseEntity.notFound().build();
    }
}
