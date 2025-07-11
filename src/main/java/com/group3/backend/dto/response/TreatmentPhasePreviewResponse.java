package com.group3.backend.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentPhasePreviewResponse {
    private UUID id;
    private String title; 
}
