package com.group3.backend.dto.request;

import com.group3.backend.constants.Roles;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(callSuper=true)
@Getter
public class CreateStaffRequest extends RegistrationRequest {
    private final Roles role = Roles.ROLE_STAFF;
}
