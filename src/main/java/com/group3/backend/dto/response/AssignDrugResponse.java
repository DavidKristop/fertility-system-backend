package com.group3.backend.dto.response;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AssignDrugResponse {
    private UUID id;
    private String title;
    private String status;
    private LocalDateTime completeDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String patientName;
    private String treatmentPhaseName;
    private List<PatientDrugBriefResponse> patientDrugs;
}
