package com.group3.backend.dto.response.Protocol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProtocolResponse {
    private UUID id;
    private String name;
    private String description;
    private List<ProtocolPhaseResponse> phases;
}
