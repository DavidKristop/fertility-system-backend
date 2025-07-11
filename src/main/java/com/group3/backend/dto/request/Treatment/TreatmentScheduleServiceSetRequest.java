package com.group3.backend.dto.request.Treatment;

import java.util.Optional;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TreatmentScheduleServiceSetRequest {
    private Optional<UUID> id;
    @NotNull
    private UUID serviceId;

}
