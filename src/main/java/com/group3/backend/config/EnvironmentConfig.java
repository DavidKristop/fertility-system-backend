package com.group3.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "seed.data")
public class EnvironmentConfig {
    private String consultationServiceId;
    private String ultrasoundServiceId;
    private String consultationProtocolId;
    private String consultationPhaseId;
    private String consultationProtocolServiceId;
    private String ultrasoundProtocolServiceId;
    @Value("${uuid.new}")
    private String newUUID;

    public String getNewUUID() {
        return newUUID;
    }

    public void setNewUUID(String newUUID) {
        this.newUUID = newUUID;
    }

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

    public String getConsultationProtocolId() {
        return consultationProtocolId;
    }

    public void setConsultationProtocolId(String consultationProtocolId) {
        this.consultationProtocolId = consultationProtocolId;
    }

    public String getConsultationPhaseId() {
        return consultationPhaseId;
    }

    public void setConsultationPhaseId(String consultationPhaseId) {
        this.consultationPhaseId = consultationPhaseId;
    }

    public String getConsultationProtocolServiceId() {
        return consultationProtocolServiceId;
    }

    public void setConsultationProtocolServiceId(String consultationProtocolServiceId) {
        this.consultationProtocolServiceId = consultationProtocolServiceId;
    }

    public String getUltrasoundProtocolServiceId() {
        return ultrasoundProtocolServiceId;
    }

    public void setUltrasoundProtocolServiceId(String ultrasoundProtocolServiceId) {
        this.ultrasoundProtocolServiceId = ultrasoundProtocolServiceId;
    }
}
