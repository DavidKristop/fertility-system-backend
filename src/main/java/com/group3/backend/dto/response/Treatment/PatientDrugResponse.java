package com.group3.backend.dto.response.Treatment;

import java.util.UUID;

import lombok.Data;

@Data
public class PatientDrugResponse{
    private UUID id;
    private DrugResponse drug;
    private String dosage;
    private String usageInstructions;
    private int amount;
    private String unit;
}