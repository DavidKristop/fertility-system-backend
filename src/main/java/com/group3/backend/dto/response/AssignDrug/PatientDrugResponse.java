package com.group3.backend.dto.response.AssignDrug;

import java.time.LocalDate;
import java.util.UUID;

import com.group3.backend.dto.response.DrugResponse;

import lombok.Data;

@Data
public class PatientDrugResponse {
    private UUID id;
    private DrugResponse drug;
    private String dosage;
    private String usageInstructions;
    private Integer amount;
    private LocalDate startDate;
    private LocalDate endDate;
}
