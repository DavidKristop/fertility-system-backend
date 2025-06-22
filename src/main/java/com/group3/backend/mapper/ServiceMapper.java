package com.group3.backend.mapper;

import org.mapstruct.Mapper;

import com.group3.backend.dto.response.ServiceResponse;
import com.group3.backend.model.Service;

@Mapper(componentModel = "spring")
public interface ServiceMapper {

    ServiceResponse toResponse(Service service);
}
