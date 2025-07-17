package com.group3.backend.mapper;

import com.group3.backend.dto.response.ContractPreviewResponse;
import com.group3.backend.dto.response.PaymentPreviewResponse;
import com.group3.backend.dto.response.TreatmentPhasePreviewResponse;
import com.group3.backend.dto.response.TreatmentPreviewResponse;
import com.group3.backend.dto.response.Schedule.DoctorScheduleReponse;
import com.group3.backend.dto.response.Schedule.PatientScheduleResponse;
import com.group3.backend.dto.response.Schedule.ScheduleResponse;
import com.group3.backend.dto.response.Schedule.ScheduleResultResponse;
import com.group3.backend.dto.response.Schedule.ScheduleServiceRespone;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.ScheduleResult;
import com.group3.backend.model.ScheduleService;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ServiceMapper.class})
public interface ScheduleMapper {
    @Mapping(source = "scheduleServices", target = "services")
    @Mapping(source = "scheduleResult", target = "scheduleResult")
    @Mapping(source = "doctor", target = "doctor", qualifiedByName = "toUserDoctorResponse")
    @Mapping(source = "patient", target = "patient", qualifiedByName = "toUserPatientResponse")
    @Mapping(target = "payment", expression = "java(getPaymentOfSchdule(schedule))")
    @Mapping(target = "treatment", expression = "java(getTreatmentPreview(schedule))")
    @Mapping(target = "treatmentPhase", expression = "java(getTreatmentPhasePreview(schedule))")
    @Mapping(target = "canMoveToNextPhase", expression = "java(getCanMoveToNextPhase(schedule))")
    @Mapping(target = "contract", expression = "java(getContractPreview(schedule))")
    DoctorScheduleReponse toDoctorScheduleRespone(Schedule schedule);

    @Mapping(source = "doctor", target = "doctor", qualifiedByName = "toUserDoctorResponse")
    PatientScheduleResponse toPatientScheduleResponse(Schedule schedule);

    ScheduleResultResponse toScheduleResultResponse(ScheduleResult result);

    @Mapping(source = "service.name", target = "name")
    @Mapping(source = "service.description", target = "description")
    @Mapping(source = "service.price", target = "price")
    @Mapping(source = "service.active", target = "active")
    @Mapping(source = "service.id", target="id")
    ScheduleServiceRespone toScheduleServiceRespone(ScheduleService scheduleService);

    default ContractPreviewResponse getContractPreview(Schedule schedule) {
        if(schedule.getScheduleServices().size() > 0){
            ScheduleService scheduleService = schedule.getScheduleServices().get(0);
            if(scheduleService.getTreatmentPhase() != null){
                return ContractPreviewResponse.builder()
                    .id(scheduleService.getTreatmentPhase().getTreatment().getContract().getId())
                    .isSigned(scheduleService.getTreatmentPhase().getTreatment().getContract().getIsSigned())
                    .build();
            }
        }
        return null;
    }

    //Need to check the if the schedule has a treatment phase,
    //then check all of the schedule in treatment phase, if all of them are DONE then set the canMoveToNextPhase to true
    default boolean getCanMoveToNextPhase(Schedule scheduleSource){
        if(scheduleSource.getScheduleServices().size() > 0){
            ScheduleService scheduleServiceBase = scheduleSource.getScheduleServices().get(0);
            if(scheduleServiceBase.getTreatmentPhase() != null){
                List<ScheduleResponse> schedules = new ArrayList<>();
                for (ScheduleService scheduleService : scheduleServiceBase.getTreatmentPhase().getScheduleServices()) {
                    if (scheduleService.getSchedule() != null) {
                        Schedule schedule = scheduleService.getSchedule();
                        if(schedules.stream().anyMatch(s -> s.getId().equals(schedule.getId()))) continue;
                        
                        ScheduleResponse newScheduleResponse = new ScheduleResponse();
                        newScheduleResponse.setId(schedule.getId());
                        newScheduleResponse.setAppointmentDateTime(schedule.getAppointmentDateTime());
                        newScheduleResponse.setEstimatedTime(schedule.getEstimatedTime());
                        newScheduleResponse.setStatus(schedule.getStatus());
                        schedules.add(newScheduleResponse);
                    }
                    //Has schedule service that does not have a schedule
                    else return false;
                }
                return schedules.stream().allMatch(s -> s.getStatus() == Schedule.Status.DONE);
            }
        }
        return false;
    }

    default TreatmentPreviewResponse getTreatmentPreview(Schedule schedule) {
        if(schedule.getScheduleServices().size() > 0){
            ScheduleService scheduleService = schedule.getScheduleServices().get(0);
            if(scheduleService.getTreatmentPhase() != null){
                return TreatmentPreviewResponse.builder()
                    .id(scheduleService.getTreatmentPhase().getTreatment().getId())
                    .status(scheduleService.getTreatmentPhase().getTreatment().getStatus())
                    .contractId(scheduleService.getTreatmentPhase().getTreatment().getContract().getId())
                    .build();
            }
        }
        return null;
    }

    default TreatmentPhasePreviewResponse getTreatmentPhasePreview(Schedule schedule) {
        if(schedule.getScheduleServices().size() > 0){
            ScheduleService scheduleService = schedule.getScheduleServices().get(0);
            if(scheduleService.getTreatmentPhase() != null){
                return TreatmentPhasePreviewResponse.builder()
                    .id(scheduleService.getTreatmentPhase().getId())
                    .title(scheduleService.getTreatmentPhase().getTitle())
                    .build();
            }
        }
        return null;
    }

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
}
