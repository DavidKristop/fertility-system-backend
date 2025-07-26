package com.group3.backend.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Base64ImageRequest {
    private String base64Image;
    private String fileName;
}
