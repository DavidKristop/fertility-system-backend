package com.group3.backend.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceCreateRequest {
    @NotEmpty(message = "Name is required")
    private String name;
    
    @Min(value = 1, message = "Description must be at least 1 character")
    @Max(value = 200, message = "Description must be at most 200 characters")
    private String description;
    
    @DecimalMin(value = "1.00", message = "Price must be at least 1")
    private BigDecimal price;

    @Min(value = 1, message = "Unit must be at least 1 character")
    @Max(value = 50, message = "Unit must be at most 50 characters")
    private String unit;
    
}
