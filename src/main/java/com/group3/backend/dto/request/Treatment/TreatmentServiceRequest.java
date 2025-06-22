package com.group3.backend.dto.request.Treatment;

import lombok.Data;
import java.util.UUID;

@Data
public class TreatmentServiceRequest {
    private UUID id;
    private Integer amount;
    private String notes;
}
