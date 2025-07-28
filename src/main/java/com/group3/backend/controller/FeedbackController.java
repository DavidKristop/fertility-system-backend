package com.group3.backend.controller;

import com.group3.backend.dto.request.CreateFeedbackRequest;
import com.group3.backend.dto.response.FeedbackResponse;
import com.group3.backend.dto.Response;
import com.group3.backend.service.FeedbackService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<Response<String>> createFeedback(@RequestBody CreateFeedbackRequest request) {
        feedbackService.createFeedback(request);
        return ResponseEntity.ok(new Response<>(null,"Tạo feedback thành công.", true));
    }

    @GetMapping("/getAll")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_STAFF')")
    public ResponseEntity<Response<Page<FeedbackResponse>>> getAllFeedbacks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(new Response<>(feedbackService.getAllFeedbacks(page, size), "Lấy toàn bộ feedback thành công.", true));
    }
}

