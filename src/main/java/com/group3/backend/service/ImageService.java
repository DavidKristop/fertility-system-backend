package com.group3.backend.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group3.backend.dto.request.Base64ImageRequest;
import com.group3.backend.dto.response.ImageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImageService {

    @Autowired
    private Cloudinary cloudinary;

    @SuppressWarnings("rawtypes")
    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploadResult.get("url").toString();
    }

    public List<ImageResponse> uploadFromBase64Images(List<Base64ImageRequest> base64Images) {
        try{
            List<ImageResponse> imageResponses = new ArrayList<>();
            Map uploadOptions = ObjectUtils.asMap();

            for(Base64ImageRequest base64Image : base64Images){
                uploadOptions.put("public_id", base64Image.getFileName());
                Map uploadResult = cloudinary.uploader().upload(base64Image.getBase64Image(), uploadOptions);
                String url = uploadResult.get("secure_url").toString();
                imageResponses.add(ImageResponse.builder()
                    .url(url)
                    .fileName(base64Image.getFileName())
                    .build());
            }
            return imageResponses;
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException("Failed to upload images", e);
        }
    }

    public void deleteImage(List<String> fileNames){
        try{
            cloudinary.api().deleteResources(fileNames, ObjectUtils.asMap());
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            throw new RuntimeException("Failed to delete images", e);
        }
    }
}
