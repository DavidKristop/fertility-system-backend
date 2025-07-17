package com.group3.backend.dto.response;

import java.util.List;

import com.group3.backend.dto.response.Schedule.DoctorScheduleReponse;
import com.group3.backend.dto.response.Treatment.TreatmentPatientDrugResponse;

import lombok.Data;

@Data
public class PatientEventResponse {
    private List<TreatmentPatientDrugResponse> treatmentPatientDrugResponse;
    private List<DoctorScheduleReponse> scheduleResponse;
}
