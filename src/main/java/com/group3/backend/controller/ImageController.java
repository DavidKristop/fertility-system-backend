package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Response<String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String url = imageService.uploadImage(file);
            return ResponseEntity.ok(new Response<>(url, "Upload success"));
        } catch (Exception e) {
            Response<String> response = new Response<>(null, "Upload failed: " + e.getMessage());
            response.setSuccess(false);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
