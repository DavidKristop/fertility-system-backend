package com.group3.backend.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class DrugResponse {
    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String unit;
    private final boolean isActive;
}
