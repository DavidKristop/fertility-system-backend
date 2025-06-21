package com.group3.backend.service;

import com.group3.backend.model.*;
import com.group3.backend.repository.ContractRepository;
import com.group3.backend.repository.TreatmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final TreatmentRepository treatmentRepository;

    public List<Contract> getContractsByPatientId(UUID patientId) {
        return contractRepository.findByTreatment_Patient_Id(patientId);
    }

    public Contract createContract(UUID treatmentId, Timestamp deadline, String pdfUrl) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
                .orElseThrow(() -> new IllegalArgumentException("Treatment not found"));

        Contract contract = Contract.builder()
                .treatment(treatment)
                .contractUrl(pdfUrl)
                .signDeadline(deadline)
                .isSigned(false)
                .build();

        return contractRepository.save(contract);
    }

    public Contract signContract(UUID contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        contract.setIsSigned(true);
        return contractRepository.save(contract);
    }
}
