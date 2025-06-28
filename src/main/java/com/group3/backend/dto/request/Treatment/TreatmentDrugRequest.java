package com.group3.backend.dto.request.Treatment;

import lombok.Builder;
import lombok.Data;

import java.sql.Date;
import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;


@Data
@Builder
public class TreatmentDrugRequest {
    @NotEmpty
    private UUID id;
    private String dosage;
    private String usageInstructions;
    @NotEmpty
    private Date startDate;
    @NotEmpty
    private Date endDate;
    @Min(value = 1, message = "Amount of drug must be greater than 0")
    private int amount;
}
