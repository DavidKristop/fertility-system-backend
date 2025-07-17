package com.group3.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.group3.backend.dto.response.ManagedUserResponse;
import com.group3.backend.model.User;

@Mapper(componentModel = "spring")
public interface ManagedUserMapper {

    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().getName().name() : null)")
    ManagedUserResponse toManagedUserResponse(User user);
}

