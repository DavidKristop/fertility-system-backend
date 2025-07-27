package com.group3.backend.dto.request;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateFeedbackRequest {
    private UUID treatmentId;
    private String content;
}
