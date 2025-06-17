package com.group3.backend.dto.request;

import java.util.UUID;

import org.hibernate.validator.constraints.URL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogRequest {
    private String title;
    private String content;

    @URL
    private String thumbnailUrl;

    private UUID authorId;
}
