package com.group3.backend.dto.response.Schedule;

import java.util.List;

import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.dto.response.UserPatientResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DoctorScheduleReponse extends ScheduleResponse {
    private UserPatientResponse patient;
    private UserDoctorResponse doctor;
    private ScheduleResultResponse scheduleResult;
    private List<ScheduleServiceRespone> services;
}
