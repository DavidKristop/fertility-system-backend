package com.group3.backend.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
    @NotEmpty(message = "Name is required")
    private String name;
    
    @NotEmpty(message = "Description is required")
    @Size(min = 1, max = 200, message = "Description must be between 1 and 200 characters")
    private String description;
    
    @DecimalMin(value = "1.00", message = "Price must be at least 1")
    private BigDecimal price;

    @NotEmpty(message = "Unit is required")
    @Size(min = 1, max = 50, message = "Unit must be between 1 and 50 characters")
    private String unit;
    
}
