package com.group3.backend.dto.request.Protocol;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolCreateRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private List<ProtocolPhaseCreateRequest> phases;
}
