package com.group3.backend.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group3.backend.constants.TreatmentStatus;
import com.group3.backend.dto.request.ContractRequest;
import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.model.Contract;
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
                .signDeadline(new Timestamp(new Timestamp(System.currentTimeMillis()).getTime() + 2 * 24 * 60 * 60 * 1000))
                .treatment(treatment)
                .contractUrl(contractRequest.getContractUrl())
                .build();
        
        if(contractRequest.isSigned())createPaymentBasedOnPaymentMode(treatment);
        return contractRepository.save(contract);
    }

    public Contract signedContract(UUID contractId, UUID patientId){
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        Treatment treatment = contract.getTreatment();
        if(contract.getTreatment().getPatient().getId() != patientId){
            throw new UnauthorizedAccessException("You are not authorized to sign this contract");
        }
        contract.setSigned(true);
        createPaymentBasedOnPaymentMode(contract.getTreatment());
        treatment.setStatus(Treatment.Status.IN_PROGRESS);
        contract.setTreatment(treatment);
        return contractRepository.save(contract);
    }

    public Contract getContractByIdAndPatientId(UUID contractId, UUID patientId){
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        if(contract.getTreatment().getPatient().getId() != patientId){
            throw new UnauthorizedAccessException("You are not authorized to sign this contract");
        }
        return contract;
    }

    public List<Contract> getAllContractByPatientId(UUID patientId){
        return contractRepository.findByTreatmentPatientId(patientId);
    }

    private void createPaymentBasedOnPaymentMode(Treatment treatment){
        PaymentRequest paymentRequest = PaymentRequest.builder()
            .paymentDeadline(new Timestamp(new Timestamp(System.currentTimeMillis()).getTime() + 2 * 24 * 60 * 60 * 1000))
            .userId(treatment.getPatient().getId())
            .build();
        if(treatment.getPaymentMode().equals(Treatment.PaymentMode.BY_PHASE)){
            List<UUID> treatmentPhaseIds = new ArrayList<>();
            TreatmentPhase firstPhase = treatment.getPhases().get(0);
            treatmentPhaseIds.add(firstPhase.getId());
            paymentRequest.setTreatmentPhaseIds(treatmentPhaseIds);
            paymentRequest.setAmount(firstPhase.getTotalAmount());
            paymentRequest.setDescription("Payment for phase: "+firstPhase.getTitle());
        }
        else{
            paymentRequest.setTreatmentPhaseIds(treatment.getPhases().stream().map(TreatmentPhase::getId).collect(Collectors.toList()));
            paymentRequest.setAmount(calculateTotalAmount(treatment.getPhases()));
            paymentRequest.setDescription("Payment for your full treatment");
        }
        paymentService.createPayment(paymentRequest);
    }

    private BigDecimal calculateTotalAmount(List<TreatmentPhase> phases) {
        return phases.stream()
                .map(TreatmentPhase::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
