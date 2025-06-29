
package com.group3.backend.dto.response.Treatment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.group3.backend.model.AssignDrug;

import lombok.Data;

@Data
public class TreatmentAssignDrugReponse {
    private UUID id;
    private AssignDrug.Status status;
    private LocalDateTime completeDate;
    private List<TreatmentPatientDrugResponse> patientDrugs;
}
