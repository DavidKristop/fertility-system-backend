package com.group3.backend.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class ContractResponse {
    private UUID id;
    private boolean isSigned;
    private LocalDateTime signDeadline;
    private UUID treatmentId;
    private String contractUrl;
}
