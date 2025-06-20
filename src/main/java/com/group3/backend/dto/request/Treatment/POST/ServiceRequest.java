package com.group3.backend.dto.request.Treatment.POST;

import lombok.Data;
import java.util.UUID;

@Data
public class ServiceRequest {
    private UUID id;
    private Integer amount;
    private String notes;
}
