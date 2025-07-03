
package com.group3.backend.dto.response.Schedule;

import java.math.BigDecimal;
import java.util.UUID;

import lombok.Data;

@Data
public class ScheduleServiceRespone {
    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isActive;
}