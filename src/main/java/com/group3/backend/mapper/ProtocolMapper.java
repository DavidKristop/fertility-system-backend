package com.group3.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group3.backend.dto.response.TreatmentProtocolReponse.TreatmentProtocolResponse;
import com.group3.backend.dto.response.TreatmentProtocolReponse.TreatmentProtocolPhaseResponse;
import com.group3.backend.dto.response.ServiceResponse;
import com.group3.backend.dto.response.DrugResponse;
import com.group3.backend.model.TreatmentProtocol;
import com.group3.backend.model.TreatmentProtocolDrug;
import com.group3.backend.model.TreatmentProtocolPhase;
import com.group3.backend.model.TreatmentProtocolService;
import java.util.List;

import com.group3.backend.service.TreatmentProtocolServiceService;

@Mapper(componentModel = "spring", uses = TreatmentProtocolServiceService.class)
public interface ProtocolMapper {
    @Mapping(source = "active", target = "isActive")
    @Mapping(target = "estimatedPrice", expression = "java(TreatmentProtocolServiceService.calculateEstimatedPrice(protocol))")
    TreatmentProtocolResponse toResponse(TreatmentProtocol protocol);

    @Mapping(source = "service.name", target = "name")
    @Mapping(source = "service.description", target = "description")
    @Mapping(source = "service.price", target = "price")
    @Mapping(source = "service.unit", target = "unit")
    @Mapping(source = "service.active", target = "isActive")
    ServiceResponse toServiceResponse(TreatmentProtocolService service);

    @Mapping(source = "drug.name", target = "name")
    @Mapping(source = "drug.description", target = "description")
    @Mapping(source = "drug.price", target = "price")
    @Mapping(source = "drug.unit", target = "unit")
    @Mapping(source = "drug.active", target = "isActive")
    DrugResponse toDrugResponse(TreatmentProtocolDrug drug);

    TreatmentProtocolPhaseResponse toPhaseResponse(TreatmentProtocolPhase phase);

    List<TreatmentProtocolResponse> toResponseList(List<TreatmentProtocol> protocols);
}
