package com.group3.backend.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ContractPreviewResponse {
    UUID id;
    boolean isSigned;
}
