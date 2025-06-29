package com.group3.backend.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ScheduleServiceCreateRequest {
    @NotNull
    private UUID serviceId;
    
    @Min(value = 1, message = "Notes must be at least 1 character")
    @Max(value = 50, message = "Notes must be at most 50 characters")
    private String notes;
}
