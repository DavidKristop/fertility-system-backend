package com.group3.backend.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group3.backend.dto.response.Treatment.TreatmentResponse;
import lombok.Data;

@Data
public class ContractResponse {
    private UUID id;
    private boolean isSigned;
    private LocalDateTime signDeadline;
    private TreatmentResponse treatment;
    private String contractUrl;
}
