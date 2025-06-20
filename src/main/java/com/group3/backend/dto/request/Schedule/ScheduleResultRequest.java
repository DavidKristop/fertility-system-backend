package com.group3.backend.dto.request.Schedule;

import java.util.UUID;

import lombok.Data;

@Data
public class ScheduleResultRequest {
    private String doctorsNote;
    private UUID scheduleId;
}
