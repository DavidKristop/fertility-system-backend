package com.group3.backend.dto.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import com.group3.backend.constraints.WorkingHours;
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
    
    @Length(min = 1, max = 50)
    private String title;

    @WorkingHours
    private LocalDateTime appointmentDateTime;
    
    @WorkingHours
    private LocalDateTime estimatedTime;

    @Builder.Default
    private List<ScheduleServiceCreateRequest> services = new ArrayList<>();
}
