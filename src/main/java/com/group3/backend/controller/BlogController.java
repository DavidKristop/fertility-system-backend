package com.group3.backend.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.request.BlogRequest;
import com.group3.backend.dto.response.BlogResponse;
import com.group3.backend.model.Blog;
import com.group3.backend.model.User;
import com.group3.backend.repository.BlogRepository;
import com.group3.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogRepository blogRepository;
    private final UserRepository userRepository;

    // POST /blogs – Tạo blog
    @PostMapping
    public ResponseEntity<BlogResponse> createBlog(@RequestBody BlogRequest request) {
        User author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("Author not found"));

        Blog blog = new Blog();
        blog.setTitle(request.getTitle());
        blog.setContent(request.getContent());
        blog.setThumbnailUrl(request.getThumbnailUrl());
        blog.setAuthor(author);

        Blog savedBlog = blogRepository.save(blog);

        return ResponseEntity.ok(toResponse(savedBlog));
    }

    // GET /blogs – Lấy tất cả blogs
    @GetMapping
    public ResponseEntity<List<BlogResponse>> getAllBlogs() {
        List<Blog> blogs = blogRepository.findAll();
        List<BlogResponse> responses = blogs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // GET /blogs/{id} – Lấy 1 blog theo ID
    @GetMapping("/{id}")
    public ResponseEntity<BlogResponse> getBlogById(@PathVariable UUID id) {
        Blog blog = blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Blog not found"));
        return ResponseEntity.ok(toResponse(blog));
    }

    // Helper – chuyển từ Blog entity sang BlogResponse DTO
    private BlogResponse toResponse(Blog blog) {
        return BlogResponse.builder()
                .id(blog.getId())
                .title(blog.getTitle())
                .content(blog.getContent())
                .thumbnailUrl(blog.getThumbnailUrl())
                .authorId(blog.getAuthor().getId())
                .authorName(blog.getAuthor().getFullName())
                .createdAt(blog.getCreatedAt())
                .updatedAt(blog.getUpdatedAt())
                .build();
    }
}
