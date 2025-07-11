package com.group3.backend.dto.request.Treatment;

import java.sql.Date;
import java.util.Optional;
import java.util.UUID;

import lombok.Data;

@Data
public class TreatmentPatientDrugSetRequest {
    private Optional<UUID> patientDrugId;
    private UUID drugId;
    private String usageInstructions;
    private Date startDate;
    private Date endDate;
    private String dosage;
    private Integer amount;
}
