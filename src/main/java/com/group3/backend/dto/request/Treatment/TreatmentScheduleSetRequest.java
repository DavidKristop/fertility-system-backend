package com.group3.backend.dto.request.Treatment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import com.group3.backend.constraints.MinDaysAhead;
import com.group3.backend.constraints.WorkingHours;

import lombok.Data;

@Data
public class TreatmentScheduleSetRequest {
    private Optional<UUID> scheduleId;

    @Length(min = 1, max = 50)
    private String title;

    @WorkingHours
    @MinDaysAhead
    private LocalDateTime appointmentDateTime;
    @WorkingHours
    @MinDaysAhead
    private LocalDateTime estimatedTime;
    private List<TreatmentScheduleServiceSetRequest> scheduleServices;
}
