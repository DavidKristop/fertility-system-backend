

package com.group3.backend.dto.response.Schedule;

import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.model.Schedule;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientScheduleResponse extends ScheduleResponse {
    private UserDoctorResponse doctor;
}