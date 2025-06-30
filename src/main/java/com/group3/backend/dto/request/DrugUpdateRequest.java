package com.group3.backend.dto.request;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DrugUpdateRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 1, max = 50, message = "Name must be between 1 and 50 characters")
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @DecimalMin(value = "1.00", message = "Price must be at least 1")
    private BigDecimal price;

    @Size(min = 1, max = 50, message = "Unit must be between 1 and 50 characters")
    private String unit;
}
