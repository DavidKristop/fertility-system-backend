package com.group3.backend.dto.response.Schedule;

import java.time.LocalDateTime;
import java.util.UUID;

import com.group3.backend.model.Schedule;

import lombok.Data;

@Data
public class ScheduleResponse {
    private UUID id;
    private LocalDateTime appointmentDateTime;
    private LocalDateTime estimatedTime;
    private Schedule.Status status;
}
