package com.group3.backend.dto.response;

import com.group3.backend.dto.response.RequestAppointment.RequestAppointmentResponse;
import com.group3.backend.dto.response.Treatment.TreatmentResponse;

import lombok.Data;

@Data
public class UserBusyResponse {
    private RequestAppointmentResponse requestAppointment;
    private TreatmentResponse treatment;
}
