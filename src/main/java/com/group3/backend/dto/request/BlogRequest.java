package com.group3.backend.dto.request;

import java.util.Optional;
import java.util.UUID;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogRequest {

    private Optional<UUID> id;

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    @URL(message = "Invalid URL format")
    private String thumbnailUrl;

    @NotNull(message = "Author ID cannot be null")
    private UUID authorId;
}
