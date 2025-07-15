
package com.group3.backend.dto.response.Treatment;

import java.util.List;
import java.util.UUID;

import com.group3.backend.dto.response.PaymentPreviewResponse;
import com.group3.backend.model.AssignDrug;

import lombok.Data;

@Data
public class TreatmentAssignDrugReponse {
    private UUID id;
    private AssignDrug.Status status;
    private List<TreatmentPatientDrugResponse> patientDrugs;
    private PaymentPreviewResponse payment;
}
