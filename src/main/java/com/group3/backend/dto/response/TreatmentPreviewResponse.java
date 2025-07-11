package com.group3.backend.dto.response;

import java.util.UUID;

import com.group3.backend.model.Treatment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentPreviewResponse {
    private UUID id;
    private Treatment.Status status;
    private UUID contractId;
}
