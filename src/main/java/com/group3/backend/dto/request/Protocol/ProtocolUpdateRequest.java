package com.group3.backend.dto.request.Protocol;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolUpdateRequest {
    @NotEmpty
    private UUID id;
    @NotBlank(message = "Name is required")
    private String name;
    private String description;

    private List<ProtocolPhaseUpdateRequest> phases;
}
