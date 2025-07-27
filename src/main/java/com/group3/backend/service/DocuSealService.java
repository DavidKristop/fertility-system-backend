package com.group3.backend.service;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.group3.backend.model.DocuSealContract;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@Service
public class DocuSealService {

    @Value("${docuseal.api_key}")
    private String apiKey;
    
    public String generateSubmissionBasedOnHtml(DocuSealContract docuSealContract) throws UnirestException{
        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("name", docuSealContract.getSubmissionName());
        bodyMap.put("send_email", false);

        Map<String,String> document = new HashMap<>();
        document.put("name", docuSealContract.getDocumentName());
        document.put("html", docuSealContract.getDocumentHtml());
        bodyMap.put("documents", new Map[]{document});

        Map<String, String> submitter = new HashMap<>();
        submitter.put("role", docuSealContract.getSubmitterRole());
        submitter.put("email", docuSealContract.getSubmitterEmail());
        bodyMap.put("submitters", new Map[]{submitter});

        String bodyMapJson = new Gson().toJson(bodyMap);
        
        HttpResponse<String> response = Unirest.post("https://api.docuseal.com/submissions/html")
                .header("X-Auth-Token", apiKey)
                .header("Content-Type", "application/json")
                .body(bodyMapJson)
                .asString();
        return response.getBody();
    }
}
