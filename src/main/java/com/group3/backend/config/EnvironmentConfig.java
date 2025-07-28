package com.group3.backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "seed.data")
public class EnvironmentConfig {
    private String consultationServiceId;
    private String ultrasoundServiceId;

    // Getters and Setters
    public String getConsultationServiceId() {
        return consultationServiceId;
    }

    public void setConsultationServiceId(String consultationServiceId) {
        this.consultationServiceId = consultationServiceId;
    }

    public String getUltrasoundServiceId() {
        return ultrasoundServiceId;
    }

    public void setUltrasoundServiceId(String ultrasoundServiceId) {
        this.ultrasoundServiceId = ultrasoundServiceId;
    }

}
