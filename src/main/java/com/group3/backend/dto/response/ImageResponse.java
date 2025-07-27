package com.group3.backend.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ImageResponse {
    private String url;
    private String fileName;
}
