package com.group3.backend.mapper;

import com.group3.backend.dto.response.Schedule.DoctorScheduleReponse;
import com.group3.backend.dto.response.Schedule.PatientScheduleResponse;
import com.group3.backend.dto.response.Schedule.ScheduleServiceRespone;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.ScheduleService;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ServiceMapper.class})
public interface ScheduleMapper {
    @Mapping(source = "scheduleServices", target = "services")
    @Mapping(source = "scheduleResult", target = "scheduleResult")
    @Mapping(source = "doctor", target = "doctor", qualifiedByName = "toUserDoctorResponse")
    @Mapping(source = "patient", target = "patient", qualifiedByName = "toUserPatientResponse")
    DoctorScheduleReponse toDoctorScheduleRespone(Schedule schedule);

    @Mapping(source = "doctor", target = "doctor", qualifiedByName = "toUserDoctorResponse")
    PatientScheduleResponse toPatientScheduleResponse(Schedule schedule);

    @Mapping(source = "service.name", target = "name")
    @Mapping(source = "service.description", target = "description")
    @Mapping(source = "service.price", target = "price")
    @Mapping(source = "service.active", target = "active")
    @Mapping(source = "service.id", target="id")
    ScheduleServiceRespone toScheduleServiceRespone(ScheduleService scheduleService);
}
