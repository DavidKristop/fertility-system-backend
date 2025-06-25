package com.group3.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContractRequest {
    @NotNull
    private String contractUrl;
    @NotNull
    private boolean isSigned;
}
