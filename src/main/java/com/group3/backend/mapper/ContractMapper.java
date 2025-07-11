package com.group3.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group3.backend.dto.response.ContractResponse;
import com.group3.backend.model.Contract;

@Mapper(componentModel = "spring", uses = {TreatmentMapper.class})
public interface ContractMapper {
    @Mapping(source = "treatment.id", target = "treatmentId")
    @Mapping(source = "isSigned", target = "signed")
    ContractResponse toResponse(Contract contract);
}
