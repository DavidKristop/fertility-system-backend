package com.group3.backend.service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group3.backend.dto.request.ContractRequest;
import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.model.AssignDrug;
import com.group3.backend.model.Contract;
import com.group3.backend.model.ScheduleService;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.repository.ContractRepository;

@Service
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private PaymentService paymentService;

    public Contract createContract(ContractRequest contractRequest,Treatment treatment){
        Contract contract = Contract.builder()
                .isSigned(contractRequest.isSigned())
                .signDeadline(LocalDateTime.now().plusHours(48))
                .treatment(treatment)
                .contractUrl(contractRequest.getContractUrl())
                .build();
        
        return contractRepository.save(contract);
    }

    public Contract signedContract(UUID contractId, UUID patientId){
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        Treatment treatment = contract.getTreatment();
        if(!contract.getTreatment().getPatient().getId().equals(patientId)){
            throw new UnauthorizedAccessException("You are not authorized to sign this contract");
        }
        contract.setIsSigned(true);
        PaymentRequest paymentRequest = PaymentService.createPaymentBasedOnTreatment(contract.getTreatment());
        paymentService.createPayment(paymentRequest);
        treatment.setStatus(Treatment.Status.IN_PROGRESS);
        contract.setTreatment(treatment);
        return contractRepository.save(contract);
    }

    public Contract getContractByIdAndPatientId(UUID contractId, UUID patientId){
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        if(!contract.getTreatment().getPatient().getId().equals(patientId)){
            throw new UnauthorizedAccessException("You are not authorized to view this contract");
        }
        return contract;
    }

    public Contract getContractById(UUID contractId){
        return contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
    }

    public Page<Contract> getPatientContract(UUID patientId, boolean isSigned, Pageable pageable){
        return contractRepository.findByIsSignedAndTreatmentPatientId(isSigned, patientId, pageable);
    } 

    public List<Contract> getAllContractByPatientId(UUID patientId){
        return contractRepository.findByTreatmentPatientId(patientId);
    }

    public Page<Contract> getAllContract(boolean isSigned, Pageable pageable){
        return contractRepository.findByIsSigned(isSigned, pageable);
    }

}
