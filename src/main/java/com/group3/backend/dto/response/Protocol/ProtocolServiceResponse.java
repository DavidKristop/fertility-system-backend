package com.group3.backend.dto.response.Protocol;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolServiceResponse {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String unit;
}
