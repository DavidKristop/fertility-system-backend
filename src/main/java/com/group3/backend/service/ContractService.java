package com.group3.backend.service;


import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.cloudinary.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.exception.ResourceConflictException;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.UnauthorizedAccessException;
import com.group3.backend.model.Contract;
import com.group3.backend.model.DocuSealContract;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.TreatmentProtocolDrug;
import com.group3.backend.model.TreatmentProtocolPhase;
import com.group3.backend.model.TreatmentProtocolService;
import com.group3.backend.repository.ContractRepository;
import com.group3.backend.utils.Constants;
import com.mashape.unirest.http.exceptions.UnirestException;

@Service
public class ContractService {
    @Autowired
    private ContractRepository contractRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private DocuSealService docuSealService;

    public Contract createContract(Treatment treatment) throws IOException, UnirestException{
        DocuSealContract docuSealContract = DocuSealContract.builder()
                .submissionName("Hợp đồng thực hiện điều trị "+treatment.getTreatmentProtocol().getTitle())
                .documentName("Hợp đồng thực hiện điều trị "+treatment.getTreatmentProtocol().getTitle())
                .documentHtml(getContractTemplateByTreatment(treatment))
                .submitterRole("Bệnh nhân")
                .submitterEmail(treatment.getPatient().getEmail())
                .build();
        JSONObject submissionData = new JSONObject(docuSealService.generateSubmissionBasedOnHtml(docuSealContract));
        String contractUrl = submissionData.getJSONArray("submitters").getJSONObject(0).getString("embed_src");        
        Contract contract = Contract.builder()
                .isSigned(false)
                .signDeadline(LocalDateTime.now().plusHours(Constants.DEADLINE_SIGN_DATE_IN_HOURS))
                .treatment(treatment)
                .contractUrl(contractUrl)
                .build();
        
        return contractRepository.save(contract);
    }

    public Contract signedContract(UUID contractId, UUID patientId, String signedUrl){
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contract not found"));
        
        if(contract.getIsSigned()){
            throw new ResourceConflictException("Contract is already signed");
        }

        Treatment treatment = contract.getTreatment();
        if(!contract.getTreatment().getPatient().getId().equals(patientId)){
            throw new UnauthorizedAccessException("You are not authorized to sign this contract");
        }
        contract.setIsSigned(true);
        contract.setContractUrl(signedUrl);

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

    public String getContractTemplateByTreatment(Treatment treatment) throws IOException {
        File contractTemplateFile = ResourceUtils.getFile("classpath:templates/contract-template.html");

        Document doc = Jsoup.parse(contractTemplateFile);
        Element drugAndServicesTable = doc.selectFirst("#drug-and-services tbody");
        Element drugAndServicesTableRowBase = drugAndServicesTable.selectFirst(".phase-row").clone();

        drugAndServicesTable.html("");

        for(TreatmentProtocolPhase treatmentProtocolPhase : treatment.getTreatmentProtocol().getPhases()){
            Element newDrugAndServicesTableRow = drugAndServicesTableRowBase.clone();
            newDrugAndServicesTableRow.selectFirst(".phase-name").text(treatmentProtocolPhase.getTitle());
            newDrugAndServicesTableRow.selectFirst(".phase-total").text(TreatmentProtocolServiceService.calculateEstimatedPriceByPhase(treatmentProtocolPhase, treatment.getPaymentMode().equals(Treatment.PaymentMode.BY_PHASE)).toString());
            newDrugAndServicesTableRow.selectFirst(".refund-amount").text(treatmentProtocolPhase.getRefundPercentage().toString());
            for(TreatmentProtocolService treatmentProtocolService : treatmentProtocolPhase.getServices()){
                newDrugAndServicesTableRow.selectFirst(".service-drug-name").text(treatmentProtocolService.getService().getName());
                newDrugAndServicesTableRow.selectFirst(".service-drug-unit").text("Lần");
                newDrugAndServicesTableRow.selectFirst(".service-drug-quantity").text("x1");
                newDrugAndServicesTableRow.selectFirst(".service-drug-unit-price").text(treatmentProtocolService.getService().getPrice().toString());
                newDrugAndServicesTableRow.selectFirst(".service-drug-total-price").text(treatmentProtocolService.getService().getPrice().toString());
            }
            for(TreatmentProtocolDrug treatmentProtocolDrug : treatmentProtocolPhase.getDrugs()){
                newDrugAndServicesTableRow.selectFirst(".service-drug-name").text(treatmentProtocolDrug.getDrug().getName());
                newDrugAndServicesTableRow.selectFirst(".service-drug-unit").text(treatmentProtocolDrug.getDrug().getUnit());
                newDrugAndServicesTableRow.selectFirst(".service-drug-quantity").text(String.valueOf(treatmentProtocolDrug.getAmount()));
                newDrugAndServicesTableRow.selectFirst(".service-drug-unit-price").text(treatmentProtocolDrug.getDrug().getPrice().toString());
                newDrugAndServicesTableRow.selectFirst(".service-drug-total-price").text(treatmentProtocolDrug.getDrug().getPrice().multiply(BigDecimal.valueOf(treatmentProtocolDrug.getAmount())).toString());
            }
            drugAndServicesTable.appendChild(newDrugAndServicesTableRow);
        }

        doc.selectFirst("#total").text("Tổng cộng: " + TreatmentProtocolServiceService.calculateEstimatedPrice(treatment.getTreatmentProtocol(), treatment.getPaymentMode().equals(Treatment.PaymentMode.BY_PHASE)).toString());
        if(treatment.getPaymentMode().equals(Treatment.PaymentMode.BY_PHASE)){
            doc.selectFirst("#by-phase").attr("checked", "checked");
        }
        else doc.selectFirst("#full-payment").attr("checked", "checked");
        doc.select(".max-payment-date").forEach(element->element.text(String.valueOf(Constants.DEADLINE_PAYMENT_DEADLINE_IN_HOURS)));
        doc.selectFirst("#sign_date").text("Ngày ký: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        return doc.html();
    }

}
