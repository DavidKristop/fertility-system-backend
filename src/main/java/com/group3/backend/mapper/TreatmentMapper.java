package com.group3.backend.mapper;

import com.group3.backend.dto.response.Treatment.*;
import com.group3.backend.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TreatmentMapper {

    @Mapping(source = "phases", target = "phases")
    TreatmentResponse toResponse(Treatment treatment);

    @Mapping(source = "patientDrugs", target = "patientDrugs")
    @Mapping(source = "schedules", target = "schedules")
    TreatmentPhaseResponse map(TreatmentPhase phase);

    @Mapping(source = "scheduleServices", target = "services")
    TreatmentScheduleResponse map(Schedule schedule);
    
    @Mapping(source = "drug", target = "drug")
    TreatmentPatientDrugResponse map(PatientDrug patientDrug);
    

}
