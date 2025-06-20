package com.group3.backend.dto.response;

import java.time.Instant;
import java.util.UUID;

import com.group3.backend.model.Blog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogResponse {

    private UUID id;
    private String title;
    private String content;
    private String thumbnailUrl;
    private UUID authorId;
    private String authorName;
    private Instant createdAt;
    private Instant updatedAt;

    // Phương thức để chuyển Blog entity sang BlogResponse
    public static BlogResponse from(Blog blog) {
        return new BlogResponse(
                blog.getId(),
                blog.getTitle(),
                blog.getContent(),
                blog.getThumbnailUrl(),
                blog.getAuthor().getId(),  // Lấy ID của tác giả
                blog.getAuthor().getFullName(), // Lấy tên của tác giả
                blog.getCreatedAt(),
                blog.getUpdatedAt()
        );
    }
}
