package com.group3.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group3.backend.dto.response.ServiceResponse;
import com.group3.backend.model.Service;

@Mapper(componentModel = "spring")
public interface ServiceMapper {
    @Mapping(source = "active", target = "isActive")
    ServiceResponse toResponse(Service service);
}
