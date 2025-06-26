package com.group3.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group3.backend.dto.response.Protocol.ProtocolDrugResponse;
import com.group3.backend.dto.response.Protocol.ProtocolResponse;
import com.group3.backend.dto.response.Protocol.ProtocolServiceResponse;
import com.group3.backend.model.TreatmentProtocol;
import com.group3.backend.model.TreatmentProtocolDrug;
import com.group3.backend.model.TreatmentProtocolService;

@Mapper(componentModel = "spring")
public interface ProtocolMapper {
    ProtocolResponse toResponse(TreatmentProtocol protocol);

    @Mapping(source = "service.name", target = "name")
    @Mapping(source = "service.description", target = "description")
    @Mapping(source = "service.price", target = "price")
    @Mapping(source = "service.unit", target = "unit")
    ProtocolServiceResponse toServiceResponse(TreatmentProtocolService service);

    @Mapping(source = "drug.name", target = "name")
    @Mapping(source = "drug.description", target = "description")
    @Mapping(source = "drug.price", target = "price")
    @Mapping(source = "drug.unit", target = "unit")
    ProtocolDrugResponse toDrugResponse(TreatmentProtocolDrug drug);
}
