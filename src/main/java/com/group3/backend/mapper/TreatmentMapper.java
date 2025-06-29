package com.group3.backend.mapper;

import com.group3.backend.dto.response.Treatment.*;
import com.group3.backend.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {ProtocolMapper.class, UserMapper.class})
public interface TreatmentMapper {

    @Mapping(source = "phases", target = "phases")
    @Mapping(source = "treatmentProtocol", target = "protocol")
    @Mapping(source = "doctor", target = "doctor", qualifiedByName = "toUserDoctorResponse")
    @Mapping(source = "patient", target = "patient", qualifiedByName = "toUserPatientResponse")
    @Named("toTreatmentReponse")
    TreatmentResponse toResponse(Treatment treatment);

    @Mapping(source = "assignDrugs", target = "assignDrugs")
    @Mapping(source = "schedules", target = "schedules")
    TreatmentPhaseResponse map(TreatmentPhase phase);

    @Mapping(source = "scheduleServices", target = "services")
    TreatmentScheduleResponse map(Schedule schedule);

    @Mapping(source = "service.id", target = "id")
    @Mapping(source = "service.name", target = "name")
    @Mapping(source = "service.description", target = "description")
    @Mapping(source = "service.price", target = "price")
    @Mapping(source = "service.unit", target = "unit")
    @Mapping(source = "service.active", target = "active")
    TreatmentServiceResponse map(ScheduleService scheduleService);
    
    @Mapping(source = "drug", target = "drug")
    TreatmentPatientDrugResponse map(PatientDrug patientDrug);
    

}
