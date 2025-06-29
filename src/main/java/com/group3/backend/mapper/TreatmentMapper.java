package com.group3.backend.mapper;

import com.group3.backend.dto.response.Treatment.*;
import com.group3.backend.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProtocolMapper.class, UserMapper.class})
public interface TreatmentMapper {

    @Mapping(source = "phases", target = "phases")
    @Mapping(source = "treatmentProtocol", target = "protocol")
    @Mapping(source = "doctor", target = "doctor", qualifiedByName = "toUserDoctorResponse")
    @Mapping(source = "patient", target = "patient", qualifiedByName = "toUserPatientResponse")
    @Named("toTreatmentReponse")
    TreatmentResponse toResponse(Treatment treatment);

    @Mapping(source = "assignDrugs", target = "assignDrugs")
    @Mapping(target = "unsetServices", expression = "java(getUnsetServices(phase))")
    @Mapping(target = "schedules", expression = "java(getScheduledServices(phase))")
    TreatmentPhaseResponse map(TreatmentPhase phase);

    @Mapping(target = "service", expression = "java(schedule.getScheduleServices().stream().findFirst().map(this::map).orElse(null))")
    TreatmentScheduleResponse map(Schedule schedule);

    @Mapping(source = "service.id", target = "id")
    @Mapping(source = "service.name", target = "name")
    @Mapping(source = "service.description", target = "description")
    @Mapping(source = "service.price", target = "price")
    @Mapping(source = "service.unit", target = "unit")
    @Mapping(source = "service.active", target = "active")
    TreatmentServiceResponse map(ScheduleService scheduleService);

    default List<TreatmentServiceResponse> getUnsetServices(TreatmentPhase phase) {
        List<TreatmentServiceResponse> unsetServices = new ArrayList<>();
        for (ScheduleService scheduleService : phase.getScheduleServices()) {
            if (scheduleService.getSchedule() == null) {
                unsetServices.add(map(scheduleService));
            }
        }
        return unsetServices;
    }

    default List<TreatmentScheduleResponse> getScheduledServices(TreatmentPhase phase) {
        List<TreatmentScheduleResponse> schedules = new ArrayList<>();
        for (ScheduleService scheduleService : phase.getScheduleServices()) {
            if (scheduleService.getSchedule() != null) {
                Schedule schedule = scheduleService.getSchedule();
                if(schedules.stream().anyMatch(s -> s.getId().equals(schedule.getId()))) continue;
                TreatmentScheduleResponse scheduleResponse = map(schedule);
                TreatmentServiceResponse serviceResponse = map(scheduleService);
                scheduleResponse.setService(serviceResponse);
                schedules.add(scheduleResponse);
            }
        }
        return schedules;
    }
    
    @Mapping(source = "drug", target = "drug")
    TreatmentPatientDrugResponse map(PatientDrug patientDrug);
    

}
