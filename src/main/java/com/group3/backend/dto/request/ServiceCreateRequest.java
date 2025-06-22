package com.group3.backend.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    private String description;
    
    @DecimalMin(value = "0.00", message = "Price must be non-negative")
    private BigDecimal price;
    
    private String unit;
    
}
