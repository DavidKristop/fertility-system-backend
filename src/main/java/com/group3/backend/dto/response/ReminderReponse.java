package com.group3.backend.dto.response;

import java.util.UUID;

import lombok.Data;

@Data
public class ReminderReponse {
    private UUID id;
    private String title;
    private String content;
    private UserRespones sendTo;
}
