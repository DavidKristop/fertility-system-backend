package com.group3.backend.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group3.backend.dto.request.Base64ImageRequest;
import com.group3.backend.dto.response.ImageResponse;

//Use jsoup to get all the images in html
//If the image does not have an id tag than generate a random id for them and upload them to cloudinary, then replace the url with cloudinary's
//Also make a method to compare between two htmls
//If there are some images that are not in the second html, delete them from cloudinary

@Service
public class HtmlStringImageUploaderService {
    @Autowired
    private StaticFileService imageService;
    
    public String uploadImagesFromHtmlString(String htmlString){
        Document doc = Jsoup.parse(htmlString);
        List<Base64ImageRequest> base64Images = new ArrayList<>();
        doc.select("img").forEach(img -> {
            if(img.attr("id").isEmpty()){
                String newUUID = UUID.randomUUID().toString();
                img.attr("id",newUUID);
                base64Images.add(Base64ImageRequest.builder()
                    .base64Image(img.attr("src"))
                    .fileName(newUUID)
                    .build());
            }
        });
        List<ImageResponse> imageResponses = imageService.uploadFromBase64Images(base64Images);
        for(ImageResponse imageResponse : imageResponses){
            doc.select("img#" + imageResponse.getFileName()).attr("src", imageResponse.getUrl());
        }


        return doc.html();
    }

    public String updateImagesFromHtmlString(String updateHtmlString, String oldHtmlString){
        Document updateDoc = Jsoup.parse(updateHtmlString);
        Document oldDoc = Jsoup.parse(oldHtmlString);
        List<Base64ImageRequest> updateBase64Images = new ArrayList<>();
        List<Base64ImageRequest> oldBase64Images = new ArrayList<>();
        List<Base64ImageRequest> uploadBase64Images = new ArrayList<>();
        List<String> deleteBase64Images = new ArrayList<>();

        oldDoc.select("img").forEach(img -> {
            oldBase64Images.add(Base64ImageRequest.builder()
                .base64Image(img.attr("src"))
                .fileName(img.attr("id"))
                .build());
        });

        updateDoc.select("img").forEach(img -> {
            if(img.attr("id").isEmpty()){
                String newUUID = UUID.randomUUID().toString();
                img.attr("id",newUUID);
                uploadBase64Images.add(Base64ImageRequest.builder()
                    .base64Image(img.attr("src"))
                    .fileName(newUUID)
                    .build());
            }
            updateBase64Images.add(Base64ImageRequest.builder()
                .base64Image(img.attr("src"))
                .fileName(img.attr("id"))
                .build());
        });

        deleteBase64Images = oldBase64Images.stream().filter(oldBase64Image -> !updateBase64Images.contains(oldBase64Image))
            .map(Base64ImageRequest::getFileName)
            .collect(Collectors.toList());

        imageService.deleteImage(deleteBase64Images);
        List<ImageResponse> uploadImageResponses = imageService.uploadFromBase64Images(uploadBase64Images);
        for(ImageResponse imageResponse : uploadImageResponses){
            updateDoc.select("img#" + imageResponse.getFileName()).attr("src", imageResponse.getUrl());
        }
        return updateDoc.html();
    }
}
