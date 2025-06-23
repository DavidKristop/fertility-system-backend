package com.group3.backend.mapper;

import com.group3.backend.dto.response.RequestAppointment.RequestAppointmentResponse;
import com.group3.backend.dto.response.UserDoctorResponse;
import com.group3.backend.dto.response.UserPatientResponse;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AppointmentRequestMapper {

    @Mapping(source = "doctor", target = "doctor", qualifiedByName = "toUserDoctorResponse")
    @Mapping(source = "patient", target = "patient", qualifiedByName = "toUserPatientResponse")
    RequestAppointmentResponse toResponse(RequestAppointment requestAppointment);

    @Named("toUserDoctorResponse")
    @Mapping(source = "doctorProfile.specialty", target = "specialty")
    @Mapping(source = "doctorProfile.degree", target = "degree")
    @Mapping(source = "doctorProfile.yearsOfExperience", target = "yearsOfExperience")
    @Mapping(source = "doctorProfile.licenseNumber", target = "licenseNumber")
    @Mapping(source = "role.name", target = "role")
    UserDoctorResponse toUserDoctorResponse(User user);

    @Named("toUserPatientResponse")
    @Mapping(source = "patientProfile.medicalHistory", target = "medicalHistory")
    @Mapping(source = "role.name", target = "role")
    UserPatientResponse toUserPatientResponse(User user);
}
