package com.group3.backend.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.request.BlogRequest;
import com.group3.backend.dto.response.BlogResponse;
import com.group3.backend.model.Blog;
import com.group3.backend.service.BlogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<BlogResponse>> saveBlog(@RequestBody BlogRequest blogRequest) {
        BlogResponse blogResponse = BlogResponse.from(blogService.saveBlog(blogRequest));
        return ResponseEntity.ok(new Response<>(blogResponse,"Blog saved successfully"));
    }

    @GetMapping
    public ResponseEntity<Response<Page<BlogResponse>>> getBlogs(
        @RequestParam(defaultValue = "") String title, 
        @RequestParam(defaultValue = "0") int page, 
        @RequestParam(defaultValue = "10") int size) {
        Page<Blog> blogs = blogService.getBlogsByTitle(title, PageRequest.of(page, size));
        Page<BlogResponse> blogResponses = blogs.map(BlogResponse::from);
        return ResponseEntity.ok(new Response<>(blogResponses,"Blogs retrieved successfully"));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<BlogResponse>> getBlogById(@PathVariable UUID id) {
        Blog blog = blogService.getBlogById(id);
        BlogResponse blogResponse = BlogResponse.from(blog);
        return ResponseEntity.ok(new Response<>(blogResponse,"Blog retrieved successfully"));
    }
}
