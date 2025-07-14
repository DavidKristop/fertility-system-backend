package com.group3.backend.mapper;

import com.group3.backend.dto.response.PaymentPreviewResponse;
import com.group3.backend.dto.response.Treatment.*;
import com.group3.backend.model.*;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {ProtocolMapper.class, UserMapper.class})
public interface TreatmentMapper {

    @Mapping(source = "phases", target = "phases")
    @Mapping(source = "treatmentProtocol", target = "protocol")
    @Mapping(source = "doctor", target = "doctor", qualifiedByName = "toUserDoctorResponse")
    @Mapping(source = "patient", target = "patient", qualifiedByName = "toUserPatientResponse")
    @Mapping(source = "currentPhase.id", target = "currentPhaseId")
    @Mapping(source = "contract.id", target = "contractId")
    @Mapping(source = "contract.isSigned", target = "signedContract")
    @Mapping(target = "canMoveToNextPhase", expression = "java(getCanMoveToNextPhase(treatment.getCurrentPhase()))")
    @Named("toTreatmentReponse")
    TreatmentResponse toResponse(Treatment treatment);

    @AfterMapping
    default void sortPhases(@MappingTarget TreatmentResponse response) {
        if (response.getPhases() != null) {
            response.setPhases(response.getPhases().stream()
                .sorted((p1, p2) -> Integer.compare(p1.getPosition(), p2.getPosition()))
                .collect(Collectors.toList()));
        }
    }

    @Mapping(source = "assignDrugs", target = "assignDrugs")
    @Mapping(target = "unsetServices", expression = "java(getUnsetServices(phase))")
    @Mapping(target = "schedules", expression = "java(getScheduledServices(phase))")
    @Mapping(source = "complete", target = "complete")
    TreatmentPhaseResponse map(TreatmentPhase phase);

    @Mapping(target = "services", expression = "java(getServices(schedule.getScheduleServices()))")
    @Mapping(target = "payment", expression = "java(getPaymentOfSchdule(schedule))")
    TreatmentScheduleResponse map(Schedule schedule);

    @Mapping(source = "service.name", target = "name")
    @Mapping(source = "service.description", target = "description")
    @Mapping(source = "service.price", target = "price")
    @Mapping(source = "service.unit", target = "unit")
    @Mapping(source = "service.active", target = "active")
    @Mapping(source = "service.id", target = "serviceId")
    TreatmentServiceResponse map(ScheduleService scheduleService);

    default List<PaymentPreviewResponse> getPaymentOfSchdule(Schedule schedule) {
        List<PaymentPreviewResponse> payments = new ArrayList<>();
        for(ScheduleService scheduleService : schedule.getScheduleServices()){
            if(scheduleService.getPayment() != null){
                if(payments.stream().noneMatch(p -> p.getId().equals(scheduleService.getPayment().getId()))){
                    payments.add(PaymentPreviewResponse.builder()
                        .id(scheduleService.getPayment().getId())
                        .amount(scheduleService.getPayment().getAmount())
                        .paymentDeadline(scheduleService.getPayment().getPaymentDeadline())
                        .status(scheduleService.getPayment().getStatus())
                        .build());
                }
            }
        }   
        return payments;
    }
    
    default List<TreatmentServiceResponse> getUnsetServices(TreatmentPhase phase) {
        List<TreatmentServiceResponse> unsetServices = new ArrayList<>();
        for (ScheduleService scheduleService : phase.getScheduleServices()) {
            if (scheduleService.getSchedule() == null) {
                unsetServices.add(map(scheduleService));
            }
        }
        return unsetServices;
    }

    default List<TreatmentServiceResponse> getServices(List<ScheduleService> scheduleServices){
        return scheduleServices.stream().map(this::map).collect(Collectors.toList());
    }

    default boolean getCanMoveToNextPhase(TreatmentPhase phase) {
        List<TreatmentScheduleResponse> schedules = getScheduledServices(phase);
        List<TreatmentServiceResponse> unsetServices = getUnsetServices(phase);
        return schedules.stream().allMatch(s -> s.getStatus() == Schedule.Status.DONE) && unsetServices.isEmpty();
    }

    default List<TreatmentScheduleResponse> getScheduledServices(TreatmentPhase phase) {
        List<TreatmentScheduleResponse> schedules = new ArrayList<>();
        for (ScheduleService scheduleService : phase.getScheduleServices()) {
            if (scheduleService.getSchedule() != null) {
                Schedule schedule = scheduleService.getSchedule();
                if(schedules.stream().anyMatch(s -> s.getId().equals(schedule.getId()))) continue;
                TreatmentScheduleResponse scheduleResponse = map(schedule);
                List<TreatmentServiceResponse> serviceResponse = getServices(schedule.getScheduleServices());
                scheduleResponse.setServices(serviceResponse);
                schedules.add(scheduleResponse);
            }
        }
        return schedules;
    }

    
    @Mapping(source = "drug", target = "drug")
    TreatmentPatientDrugResponse map(PatientDrug patientDrug);
    

}
