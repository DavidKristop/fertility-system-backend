package com.group3.backend.mapper;

import com.group3.backend.dto.response.PatientDrugBriefResponse;
import com.group3.backend.model.PatientDrug;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientDrugMapper {

    @Mapping(source = "usageInstructions", target = "usageInstructions")
    @Mapping(source = "amount", target = "amount")
    @Mapping(source = "dosage", target = "dosage")
    @Mapping(source = "drug.name", target = "drugName")
    @Mapping(source = "drug.price", target = "drugPrice")
    PatientDrugBriefResponse toBriefResponse(PatientDrug patientDrug);
}
