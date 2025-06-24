package com.group3.backend.mapper;

import org.mapstruct.Mapper;

import com.group3.backend.dto.response.ContractResponse;
import com.group3.backend.model.Contract;

@Mapper(componentModel = "spring", uses = TreatmentMapper.class)
public interface ContractMapper {
    ContractResponse toResponse(Contract contract);
}
