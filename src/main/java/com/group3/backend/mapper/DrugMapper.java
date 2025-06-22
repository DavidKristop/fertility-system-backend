package com.group3.backend.mapper;

import org.mapstruct.Mapper;

import com.group3.backend.dto.response.DrugResponse;
import com.group3.backend.model.Drug;

@Mapper(componentModel = "spring")
public interface DrugMapper {

    DrugResponse toResponse(Drug drug);
}
