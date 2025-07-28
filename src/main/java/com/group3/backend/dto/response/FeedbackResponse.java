package com.group3.backend.dto.response;

import lombok.Data;
import java.util.UUID;

@Data
public class FeedbackResponse {
    private UUID id;
    private String content;
    private UUID treatmentId;
    private String treatmentName;
    private String patientName;
}