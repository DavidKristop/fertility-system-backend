package com.group3.backend.dto.request.Treatment;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import lombok.Data;

@Data
public class TreatmentPatientDrugSetRequest {
    private Optional<UUID> patientDrugId;
    private UUID drugId;
    private String usageInstructions;
    private LocalDate startDate;
    private LocalDate endDate;
    private String dosage;
    private Integer amount;
}
