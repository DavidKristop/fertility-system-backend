package com.group3.backend.dto.request.Treatment;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class TreatmentAssignDrugSetRequest {
    private UUID assignDrugId;
    private List<TreatmentPatientDrugSetRequest> patientDrugs;
}
