package com.group3.backend.dto.response.Treatment;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class TreatmentServiceResponse {
    private UUID id;
    private UUID serviceId;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isActive;
    private String unit;
}
