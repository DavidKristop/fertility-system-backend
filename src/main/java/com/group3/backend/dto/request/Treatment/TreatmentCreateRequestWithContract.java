package com.group3.backend.dto.request.Treatment;

import com.group3.backend.dto.request.ContractRequest;

import lombok.Data;
import jakarta.validation.constraints.NotEmpty;

@Data
public class TreatmentCreateRequestWithContract {
    @NotEmpty
    private TreatmentCreateRequest treatmentCreateRequest;
    @NotEmpty
    private ContractRequest contractCreateRequest;
}
