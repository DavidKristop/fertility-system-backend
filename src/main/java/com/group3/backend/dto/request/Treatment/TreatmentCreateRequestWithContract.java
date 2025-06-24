package com.group3.backend.dto.request.Treatment;

import com.group3.backend.dto.request.ContractRequest;

import lombok.Data;

@Data
public class TreatmentCreateRequestWithContract {
    private TreatmentCreateRequest treatmentCreateRequest;
    private ContractRequest contractCreateRequest;
}
