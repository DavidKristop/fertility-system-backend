package com.group3.backend.dto.response;

import java.time.LocalDate;

import lombok.Data;

@Data
public class PatientDrugBriefResponse {
    private String usageInstructions;
    private Integer amount;
    private String dosage;
    private String drugName;
    private Double drugPrice;
    private LocalDate startDate;
    private LocalDate endDate;
}