package com.group3.backend.dto.request.Protocol;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
    @Size(min = 1, max = 8, message = "Protocol must have between 1 and 8 phases")
    private List<ProtocolPhaseCreateRequest> phases;
}
