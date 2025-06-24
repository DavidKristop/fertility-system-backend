package com.group3.backend.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.ContractResponse;
import com.group3.backend.mapper.ContractMapper;
import com.group3.backend.service.ContractService;
import com.group3.backend.utils.CurrentUserUtils;

@RestController
@RequestMapping("/api/contracts")
public class ContractController {
    @Autowired
    private ContractService contractService;

    @Autowired
    private CurrentUserUtils currentUserUtils;

    @Autowired
    private ContractMapper contractMapper;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<List<ContractResponse>>> getAllContractsByPatientId() {
        return ResponseEntity.ok(new Response<>(contractService.getAllContractByPatientId(currentUserUtils.getCurrentUserId()).stream().map(contractMapper::toResponse).collect(Collectors.toList()), "Contracts retrieved successfully"));
    }
    
    @GetMapping("/{contractId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<ContractResponse>> getContractById(@PathVariable UUID contractId) {
        return ResponseEntity.ok(new Response<>(
            contractMapper.toResponse(
                    contractService.getContractByIdAndPatientId(contractId, currentUserUtils.getCurrentUserId()
                )
            ),
            "Contract retrieved successfully"));
    }
}
