package com.group3.backend.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.Range;

import com.group3.backend.constraints.WorkingHours;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ScheduleCreateRequest {
    @NotNull
    private UUID patientId;
    @NotNull
    private UUID doctorId;
    
    @WorkingHours
    private LocalDateTime appointmentDateTime;
    
    @WorkingHours
    private LocalDateTime estimatedTime;

    private List<ScheduleServiceCreateRequest> services;
}
