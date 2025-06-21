package com.group3.backend.controller;

import com.group3.backend.model.Contract;
import com.group3.backend.service.ContractService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping("/patient/{id}")
    public ResponseEntity<List<Contract>> getContractsByPatient(@PathVariable UUID id) {
        return ResponseEntity.ok(contractService.getContractsByPatientId(id));
    }

    @PostMapping
    public ResponseEntity<Contract> createContract(@RequestBody CreateContractRequest request) {
        Contract created = contractService.createContract(
                request.getTreatmentId(),
                request.getSignDeadline(),
                request.getContractUrl()
        );
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}/sign")
    public ResponseEntity<Contract> signContract(@PathVariable UUID id) {
        return ResponseEntity.ok(contractService.signContract(id));
    }

    @Data
    public static class CreateContractRequest {
        private UUID treatmentId;
        private Timestamp signDeadline;
        private String contractUrl;
    }
}
