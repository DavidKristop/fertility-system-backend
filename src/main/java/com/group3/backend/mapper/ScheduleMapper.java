package com.group3.backend.mapper;

import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.dto.response.UserPatientResponse;
import com.group3.backend.dto.response.Schedule.DoctorScheduleReponse;
import com.group3.backend.dto.response.Schedule.PatientScheduleResponse;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(source = "scheduleServices", target = "services")
    @Mapping(source = "scheduleResult", target = "scheduleResult")
    @Mapping(source = "doctor", target = "doctor")
    @Mapping(source = "patient", target = "patient")
    DoctorScheduleReponse toDoctorScheduleRespone(Schedule schedule);

    @Mapping(source = "scheduleServices", target = "services")
    @Mapping(source = "scheduleResult", target = "scheduleResult")
    @Mapping(source = "doctor", target = "doctor")
    PatientScheduleResponse toPatientScheduleResponse(Schedule schedule);

    @Mapping(source = "user.doctorProfile.specialty",target = "specialty")
    @Mapping(source = "user.doctorProfile.degree",target = "degree")
    @Mapping(source = "user.doctorProfile.yearsOfExperience",target = "yearsOfExperience")
    @Mapping(source = "user.doctorProfile.licenseNumber",target = "licenseNumber")
    @Mapping(source = "user.role.name",target = "role")
    UserDoctorResponse toUserDoctorReponse(User user);

    @Mapping(source = "user.patientProfile.medicalHistory",target = "medicalHistory")
    @Mapping(source = "user.role.name",target = "role")
    UserPatientResponse toUserPatientResponse(User user);
}
