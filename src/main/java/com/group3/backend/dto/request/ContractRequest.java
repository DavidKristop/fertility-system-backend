package com.group3.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractRequest {
    @NotNull
    private String contractUrl;
    @NotNull
    private boolean isSigned;
}
