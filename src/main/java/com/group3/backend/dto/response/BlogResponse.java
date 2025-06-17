package com.group3.backend.dto.response;

import java.time.Instant;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogResponse {

    private UUID id;
    private String title;
    private String content;
    private String thumbnailUrl;

    private UUID authorId;
    private String authorName;

    private Instant createdAt;
    private Instant updatedAt;
}
