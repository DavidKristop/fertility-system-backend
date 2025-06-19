package com.group3.backend.dto.response.Treatment;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class DrugResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
}
