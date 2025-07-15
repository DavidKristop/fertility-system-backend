package com.group3.backend.dto.response.Treatment;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class TreatmentPatientDrugResponse{
    private UUID id;
    private TreatmentDrugResponse drug;
    private String dosage;
    private String usageInstructions;
    private int amount;
    private LocalDate startDate;
    private LocalDate endDate;
}