

package com.group3.backend.dto.response.Schedule;

import com.group3.backend.dto.response.UserDoctorResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientScheduleResponse extends ScheduleResponse {
    private UserDoctorResponse doctor;
}