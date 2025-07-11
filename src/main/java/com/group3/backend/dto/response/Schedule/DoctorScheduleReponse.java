package com.group3.backend.dto.response.Schedule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.group3.backend.dto.response.PaymentPreviewResponse;
import com.group3.backend.dto.response.TreatmentPhasePreviewResponse;
import com.group3.backend.dto.response.TreatmentPreviewResponse;
import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.dto.response.UserPatientResponse;
import com.group3.backend.model.Schedule;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DoctorScheduleReponse extends ScheduleResponse {
    private UserPatientResponse patient;
    private UserDoctorResponse doctor;
    private ScheduleResultResponse scheduleResult;
    private List<ScheduleServiceRespone> services;
    private List<PaymentPreviewResponse> payment;
    private TreatmentPreviewResponse treatment;
    private TreatmentPhasePreviewResponse treatmentPhase;
    private boolean canMoveToNextPhase;

}
