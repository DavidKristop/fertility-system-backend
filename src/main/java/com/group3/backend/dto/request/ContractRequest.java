package com.group3.backend.dto.request;

import lombok.Data;

@Data
public class ContractRequest {
    private String contractUrl;
    private boolean isSigned;
}
