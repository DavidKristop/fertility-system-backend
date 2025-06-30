package com.group3.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.dto.response.UserPatientResponse;
import com.group3.backend.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(source = "doctorProfile.specialty",target = "specialty")
    @Mapping(source = "doctorProfile.degree",target = "degree")
    @Mapping(source = "doctorProfile.yearsOfExperience",target = "yearsOfExperience")
    @Mapping(source = "doctorProfile.licenseNumber",target = "licenseNumber")
    @Mapping(source = "role.name",target = "role")
    @Named("toUserDoctorResponse")
    UserDoctorResponse toUserDoctorResponse(User user);

    @Mapping(source = "patientProfile.medicalHistory",target = "medicalHistory")
    @Mapping(source = "role.name",target = "role")
    @Named("toUserPatientResponse")
    UserPatientResponse toUserPatientResponse(User user);
}
