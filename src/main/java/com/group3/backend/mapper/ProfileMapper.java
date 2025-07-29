package com.group3.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group3.backend.dto.response.ProfileResponse;
import com.group3.backend.model.User;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    @Mapping(source = "role.name", target = "role")
    ProfileResponse toProfileResponse(User user);
}
