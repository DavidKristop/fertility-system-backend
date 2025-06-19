package com.group3.backend.dto.request.Treatment.POST;

import lombok.Data;
import java.util.UUID;

@Data
public class DrugRequest {
    private UUID id;
    private String dosage;
    private String usageInstructions;
    private int amount;
}
