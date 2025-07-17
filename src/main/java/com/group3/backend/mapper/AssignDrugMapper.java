package com.group3.backend.mapper;

import com.group3.backend.dto.response.ContractPreviewResponse;
import com.group3.backend.dto.response.DrugResponse;
import com.group3.backend.dto.response.PaymentPreviewResponse;
import com.group3.backend.dto.response.TreatmentPhasePreviewResponse;
import com.group3.backend.dto.response.TreatmentPreviewResponse;
import com.group3.backend.dto.response.AssignDrug.AssignDrugResponse;
import com.group3.backend.dto.response.AssignDrug.PatientDrugResponse;
import com.group3.backend.model.AssignDrug;
import com.group3.backend.model.Drug;
import com.group3.backend.model.PatientDrug;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface AssignDrugMapper {

    @Mapping(source = "treatmentPhase.treatment.patient", target = "patient", qualifiedByName = "toUserPatientResponse")
    @Mapping(source = "treatmentPhase.treatment.doctor", target = "doctor", qualifiedByName = "toUserDoctorResponse")
    @Mapping(target = "payment", expression = "java(getPaymentOfSchdule(assignDrug))")
    @Mapping(target = "treatment", expression = "java(getTreatmentPreview(assignDrug))")
    @Mapping(target = "treatmentPhase", expression = "java(getTreatmentPhasePreview(assignDrug))")
    @Mapping(target = "contract", expression = "java(getContractPreview(assignDrug))")
    AssignDrugResponse toAssignDrugResponse(AssignDrug assignDrug);

    PatientDrugResponse toPatientDrugResponse(PatientDrug patientDrug);

    @Mapping(source = "active", target = "isActive")
    DrugResponse toDrugResponse(Drug drug);

    default ContractPreviewResponse getContractPreview(AssignDrug assignDrug) {
        if(assignDrug.getTreatmentPhase() != null){
            return ContractPreviewResponse.builder()
                .id(assignDrug.getTreatmentPhase().getTreatment().getContract().getId())
                .isSigned(assignDrug.getTreatmentPhase().getTreatment().getContract().getIsSigned())
                .build();
        }
        return null;
    }

    default TreatmentPreviewResponse getTreatmentPreview(AssignDrug assignDrug) {
        if(assignDrug.getTreatmentPhase() != null){
            return TreatmentPreviewResponse.builder()
                .id(assignDrug.getTreatmentPhase().getTreatment().getId())
                .status(assignDrug.getTreatmentPhase().getTreatment().getStatus())
                .contractId(assignDrug.getTreatmentPhase().getTreatment().getContract().getId())
                .build();
            }
        return null;
    }

    default TreatmentPhasePreviewResponse getTreatmentPhasePreview(AssignDrug assignDrug) {
        if(assignDrug.getTreatmentPhase() != null){
            return TreatmentPhasePreviewResponse.builder()
                .id(assignDrug.getTreatmentPhase().getId())
                .title(assignDrug.getTreatmentPhase().getTitle())
                .build();
        }
        return null;
    }

    default PaymentPreviewResponse getPaymentOfSchdule(AssignDrug assignDrug) {
        if(assignDrug.getPayment() != null){
            return PaymentPreviewResponse.builder()
                .id(assignDrug.getPayment().getId())
                .amount(assignDrug.getPayment().getAmount())
                .paymentDeadline(assignDrug.getPayment().getPaymentDeadline())
                .status(assignDrug.getPayment().getStatus())
                .build();
        }
        return null;
    }
}
