package com.group3.backend.mapper;

import com.group3.backend.dto.response.AssignDrugResponse;
import com.group3.backend.model.AssignDrug;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = {PatientDrugMapper.class}, // dùng để map nested
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface AssignDrugMapper {

    @Mapping(source = "status", target = "status")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "treatmentPhase.title", target = "treatmentPhaseName")
    @Mapping(source = "treatmentPhase.treatment.patient.fullName", target = "patientName")
    @Mapping(source = "patientDrugs", target = "patientDrugs")
    AssignDrugResponse toAssignDrugResponse(AssignDrug assignDrug);
}
