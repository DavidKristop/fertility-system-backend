package com.group3.backend.dto.request.Treatment;

import lombok.Data;
import java.util.UUID;

@Data
public class TreatmentDrugRequest {
    private UUID id;
    private String dosage;
    private String usageInstructions;
    private int amount;
}
