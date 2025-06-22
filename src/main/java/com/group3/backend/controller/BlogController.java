package com.group3.backend.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<BlogResponse> createBlog(@RequestBody BlogRequest blogRequest) {
        if (blogRequest.getTitle() == null || blogRequest.getTitle().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (blogRequest.getContent() == null || blogRequest.getContent().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<User> authorOptional = userRepository.findById(blogRequest.getAuthorId());
        if (authorOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        if (blogRequest.getThumbnailUrl() != null && !blogRequest.getThumbnailUrl().matches("^(http|https)://.*$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Blog blog = new Blog();
        blog.setTitle(blogRequest.getTitle());
        blog.setContent(blogRequest.getContent());
        blog.setThumbnailUrl(blogRequest.getThumbnailUrl());
        blog.setAuthor(authorOptional.get());

        Blog savedBlog = blogRepository.save(blog);

        // Trả về BlogResponse thay vì Blog entity
        BlogResponse blogResponse = BlogResponse.from(savedBlog);
        return ResponseEntity.status(HttpStatus.CREATED).body(blogResponse);
    }
}
