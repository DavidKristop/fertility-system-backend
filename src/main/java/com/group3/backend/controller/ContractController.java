package com.group3.backend.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.group3.backend.dto.response.ContractResponse;
import com.group3.backend.mapper.ContractMapper;
import com.group3.backend.model.Contract;
import com.group3.backend.model.DocuSealContract;
import com.group3.backend.service.ContractService;
import com.group3.backend.service.DocuSealService;
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

    @Autowired
    private DocuSealService docuSealService;

    @GetMapping("/patient")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<Page<ContractResponse>>> getAllContractsByPatientId(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "false") boolean isSigned
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Contract> contracts = contractService.getPatientContract(currentUserUtils.getCurrentUserId(), isSigned, pageable);
        return ResponseEntity.ok(new Response<>(contracts.map(contractMapper::toResponse), "Contracts retrieved successfully"));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<Page<ContractResponse>>> getAllContracts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "false") boolean isSigned
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Contract> contracts = contractService.getAllContract(isSigned, pageable);
        return ResponseEntity.ok(new Response<>(contracts.map(contractMapper::toResponse), "Contracts retrieved successfully"));
    }
    
    @GetMapping("/patient/{contractId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<ContractResponse>> getContractById(@PathVariable UUID contractId) {
        return ResponseEntity.ok(new Response<>(
            contractMapper.toResponse(
                    contractService.getContractByIdAndPatientId(contractId, currentUserUtils.getCurrentUserId()
                )
            ),
            "Contract retrieved successfully"));
    }

    @GetMapping("/manager/{contractId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<ContractResponse>> getContractByIdForManager(@PathVariable UUID contractId) {
        return ResponseEntity.ok(new Response<>(
            contractMapper.toResponse(
                    contractService.getContractById(contractId)
                ),
            "Contract retrieved successfully"));
    }

    @GetMapping("/template/{contractId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<String>> getContractTemplate(@PathVariable UUID contractId) throws Exception {
        Contract contract = contractService.getContractByIdAndPatientId(contractId, currentUserUtils.getCurrentUserId());
        String html = contractService.getContractTemplate(contract);

        DocuSealContract docuSealContract = DocuSealContract.builder()
                .submissionName("Hợp đồng thực hiện điều trị "+contract.getTreatment().getTreatmentProtocol().getTitle())
                .documentName("Hợp đồng thực hiện điều trị "+contract.getTreatment().getTreatmentProtocol().getTitle())
                .documentHtml(html)
                .submitterRole("Patient")
                .submitterEmail(contract.getTreatment().getPatient().getEmail())
                .build();
        JSONObject submissionData = new JSONObject(docuSealService.generateSubmissionBasedOnHtml(docuSealContract));

        String slug = submissionData.getJSONArray("submitters").getJSONObject(0).getString("slug");
        
        return ResponseEntity.ok(new Response<>(
            slug,
            "Contract template retrieved successfully"));
    }

    @PutMapping("/sign/{contractId}")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<ContractResponse>> signContract(@PathVariable UUID contractId) {
        return ResponseEntity.ok(new Response<>(
            contractMapper.toResponse(
                    contractService.signedContract(contractId, currentUserUtils.getCurrentUserId())
                ),
            "Contract signed successfully"));
    }
}
