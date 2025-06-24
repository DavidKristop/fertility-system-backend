package com.group3.backend.dto.request.Schedule;

import java.util.UUID;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ScheduleResultRequest {
    private String doctorsNote;
    @NotEmpty
    private UUID scheduleId;
}
