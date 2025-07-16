package com.group3.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group3.backend.dto.request.PaymentRequest;
import com.group3.backend.dto.request.ScheduleCreateRequest;
import com.group3.backend.dto.request.Schedule.ScheduleChangeRequest;
import com.group3.backend.dto.request.Treatment.TreatmentPhaseSetRequest;
import com.group3.backend.dto.request.Treatment.TreatmentScheduleSetRequest;
import com.group3.backend.dto.request.Treatment.TreatmentScheduleServiceSetRequest;
import com.group3.backend.dto.request.Treatment.TreatmentAssignDrugSetRequest;
import com.group3.backend.dto.request.Treatment.TreatmentPatientDrugSetRequest;
import com.group3.backend.model.Drug;
import com.group3.backend.model.AssignDrug;
import com.group3.backend.model.PatientDrug;
import com.group3.backend.model.Payment;
import com.group3.backend.model.Refund;
import com.group3.backend.repository.DrugRepository;
import com.group3.backend.repository.PatientDrugRepository;
import com.group3.backend.repository.PaymentRepository;
import com.group3.backend.repository.RefundRepository;
import com.group3.backend.repository.AssignDrugRepository;
import com.group3.backend.exception.ResourceConflictException;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.ScheduleService;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.ScheduleServiceRepository;
import com.group3.backend.repository.ServiceRepository;
import com.group3.backend.repository.TreatmentPhaseRepository;

@Service
public class TreatmentPhaseService {
    @Autowired
    private TreatmentPhaseRepository treatmentPhaseRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleServiceRepository scheduleServiceRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private DrugRepository drugRepository;
    
    @Autowired
    private AssignDrugRepository assignDrugRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private com.group3.backend.service.ScheduleService scheduleService;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private PatientDrugRepository patientDrugRepository;

    //Set the scheduleServices and drug in treatment phase
    //If the scheduleService or patientDrug is new( has id with only 0) then create the scheduleService or patientDrug
    //Same goes for schedule and patientdrug
    public TreatmentPhase setTreatmentPhase(TreatmentPhaseSetRequest request){
        TreatmentPhase treatmentPhase = treatmentPhaseRepository.findById(request.getPhaseId())
        .orElseThrow(() -> new ResourceNotFoundException("Treatment phase not found"));
        
        if(!(treatmentPhase.getTreatment().getStatus().equals(Treatment.Status.IN_PROGRESS)
        || treatmentPhase.getTreatment().getStatus().equals(Treatment.Status.AWAITING_CONTRACT_SIGNED))){
            throw new ResourceConflictException("Treatment is not in progress or awaiting contract signed");
        }

        if(!treatmentPhase.getId().equals(treatmentPhase.getTreatment().getCurrentPhase().getId())){
            throw new ResourceConflictException("Treatment phase is not the current phase");
        }

        // Get existing schedule services for this treatment phase
        List<ScheduleService> existingScheduleServices = scheduleServiceRepository.findByIdIn(
            treatmentPhase.getScheduleServices().stream().map(ScheduleService::getId).toList());

        // Get existing assign drugs
        List<AssignDrug> existingAssignDrugs = assignDrugRepository.findByIdIn(
            treatmentPhase.getAssignDrugs().stream().map(AssignDrug::getId).toList());

        // Initialize lists for IDs
        List<UUID> scheduleServiceIds = new ArrayList<>();
        List<UUID> assignDrugIds = new ArrayList<>();

        // Initialize payment requests
        PaymentRequest servicePaymentRequest = PaymentRequest.builder()
            .userId(treatmentPhase.getTreatment().getPatient().getId())
            .amount(new BigDecimal(0))
            .description("Payment for new services")
            .paymentDeadline(LocalDateTime.now().plusHours(48))
            .build();

        PaymentRequest drugPaymentRequest = PaymentRequest.builder()
            .userId(treatmentPhase.getTreatment().getPatient().getId())
            .amount(new BigDecimal(0))
            .description("Payment for new drugs")
            .paymentDeadline(LocalDateTime.now().plusHours(48))
            .build();

        for(TreatmentScheduleSetRequest scheduleRequest : request.getSchedules()) {
            // Process each schedule service in the schedule
            if(scheduleRequest.getEstimatedTime().isAfter(treatmentPhase.getTreatment().getEndDate().atTime(23, 59, 59))){
                throw new ResourceConflictException("Estimated time must be before treatment end date");
            }

            Schedule schedule;

            //Create schedule if its new
            if(scheduleRequest.getScheduleId().isEmpty()){
                schedule = scheduleService.createSchedule(ScheduleCreateRequest.builder()
                .title(scheduleRequest.getTitle())
                .patientId(treatmentPhase.getTreatment().getPatient().getId())
                .doctorId(treatmentPhase.getTreatment().getDoctor().getId())
                .appointmentDateTime(scheduleRequest.getAppointmentDateTime())
                .estimatedTime(scheduleRequest.getEstimatedTime())
                .build());
            }else{
                schedule = scheduleService.changeScheduleTime(
                    scheduleRequest.getScheduleId().get(), 
                    treatmentPhase.getTreatment().getDoctor().getId(), 
                    ScheduleChangeRequest.builder()
                        .appointmentDateTime(scheduleRequest.getAppointmentDateTime())
                        .estimatedTime(scheduleRequest.getEstimatedTime())
                    .build());
                schedule.setTitle(scheduleRequest.getTitle());
            }
            
            List<ScheduleService> scheduleServices = new ArrayList<>();
            for(TreatmentScheduleServiceSetRequest scheduleServiceRequest : scheduleRequest.getScheduleServices()) {
                if(scheduleServiceRequest.getId().isEmpty()) {
                    // New schedule service - create it
                    ScheduleService newScheduleService = new ScheduleService();
                    newScheduleService.setTreatmentPhase(treatmentPhase);
                    newScheduleService.setSchedule(schedule);

                    // Add service
                    com.group3.backend.model.Service service = serviceRepository.findById(scheduleServiceRequest.getServiceId())
                        .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
                    newScheduleService.setService(service);
                    ScheduleService savedScheduleService = scheduleServiceRepository.save(newScheduleService);

                    // Add to service payment amount
                    servicePaymentRequest.setAmount(servicePaymentRequest.getAmount().add(service.getPrice()));
                    scheduleServiceIds.add(savedScheduleService.getId());
                    scheduleServices.add(savedScheduleService);
                } else {
                    // Existing schedule service - update it
                    ScheduleService existingScheduleService = existingScheduleServices.stream()
                        .filter(s -> s.getId().equals(scheduleServiceRequest.getId().get()))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Schedule service not found"));

                    existingScheduleService.setSchedule(schedule);
                    scheduleServices.add(existingScheduleService);
                }
            }
            schedule.setScheduleServices(scheduleServices);
            scheduleRepository.save(schedule);
        }

        // Process drug assignments
        for(TreatmentAssignDrugSetRequest drugRequest : request.getAssignDrugs()) {
            if(drugRequest.getAssignDrugId().isEmpty()) {
                // New drug assignment - create it
                AssignDrug newAssignDrug = new AssignDrug();
                newAssignDrug.setTreatmentPhase(treatmentPhase);
                newAssignDrug.setStatus(AssignDrug.Status.PENDING);
                newAssignDrug.setPatientDrugs(new ArrayList<>());

                // Process each patient drug
                for(TreatmentPatientDrugSetRequest patientDrugRequest : drugRequest.getPatientDrugs()) {
                    PatientDrug newPatientDrug = new PatientDrug();
                    newPatientDrug.setAssignDrug(newAssignDrug);
                    
                    Drug drug = drugRepository.findById(patientDrugRequest.getDrugId())
                        .orElseThrow(() -> new ResourceNotFoundException("Drug not found"));
                    newPatientDrug.setDrug(drug);
                    
                    newPatientDrug.setUsageInstructions(patientDrugRequest.getUsageInstructions());
                    newPatientDrug.setStartDate(patientDrugRequest.getStartDate());
                    newPatientDrug.setEndDate(patientDrugRequest.getEndDate());
                    newPatientDrug.setDosage(patientDrugRequest.getDosage());
                    newPatientDrug.setAmount(patientDrugRequest.getAmount());
                    newPatientDrug.setAssignDrug(newAssignDrug);

                    
                    newAssignDrug.getPatientDrugs().add(newPatientDrug);
                    
                    // Add to drug payment amount
                    drugPaymentRequest.setAmount(drugPaymentRequest.getAmount().add(drug.getPrice().multiply(BigDecimal.valueOf(patientDrugRequest.getAmount()))));
                }
                
                treatmentPhase.getAssignDrugs().add(newAssignDrug);
                assignDrugIds.add(assignDrugRepository.save(newAssignDrug).getId());
            } else {
                // Existing drug assignment - update it
                AssignDrug existingAssignDrug = existingAssignDrugs.stream()
                    .filter(d -> d.getId().equals(drugRequest.getAssignDrugId().get()))
                    .findFirst()
                    .orElseThrow(() -> new ResourceNotFoundException("Assign drug not found"));

                // Process each patient drug
                for(TreatmentPatientDrugSetRequest patientDrugRequest : drugRequest.getPatientDrugs()) {
                    if(patientDrugRequest.getPatientDrugId().isEmpty()) {
                        // New patient drug - create it
                        PatientDrug newPatientDrug = new PatientDrug();
                        newPatientDrug.setAssignDrug(existingAssignDrug);
                        
                        Drug drug = drugRepository.findById(patientDrugRequest.getDrugId())
                            .orElseThrow(() -> new ResourceNotFoundException("Drug not found"));
                        newPatientDrug.setDrug(drug);
                        
                        newPatientDrug.setUsageInstructions(patientDrugRequest.getUsageInstructions());
                        newPatientDrug.setStartDate(patientDrugRequest.getStartDate());
                        newPatientDrug.setEndDate(patientDrugRequest.getEndDate());
                        newPatientDrug.setDosage(patientDrugRequest.getDosage());
                        newPatientDrug.setAmount(patientDrugRequest.getAmount());
                        
                        existingAssignDrug.getPatientDrugs().add(newPatientDrug);
                        
                        // Add to payment amount
                        drugPaymentRequest.setAmount(drugPaymentRequest.getAmount().add(drug.getPrice().multiply(BigDecimal.valueOf(patientDrugRequest.getAmount()))));
                        assignDrugIds.add(existingAssignDrug.getId());
                    } else {
                        // Existing patient drug - update it
                        PatientDrug existingPatientDrug = existingAssignDrug.getPatientDrugs().stream()
                            .filter(pd -> pd.getId().equals(patientDrugRequest.getPatientDrugId().get()))
                            .findFirst()
                            .orElseThrow(() -> new ResourceNotFoundException("Patient drug not found"));

                        if(!existingAssignDrug.getStatus().equals(AssignDrug.Status.PENDING)){
                            throw new ResourceConflictException("Cannot update assign drug that is pending");
                        }
                            
                        Drug drug = drugRepository.findById(patientDrugRequest.getDrugId())
                            .orElseThrow(() -> new ResourceNotFoundException("Drug not found"));
                        existingPatientDrug.setDrug(drug);
                        
                        existingPatientDrug.setUsageInstructions(patientDrugRequest.getUsageInstructions());
                        existingPatientDrug.setStartDate(patientDrugRequest.getStartDate());
                        existingPatientDrug.setEndDate(patientDrugRequest.getEndDate());
                        existingPatientDrug.setDosage(patientDrugRequest.getDosage());
                    }
                }
            }
        }

        TreatmentPhase treatmentPhaseReturn = treatmentPhaseRepository.save(treatmentPhase);

        // Create service payment if needed
        if(servicePaymentRequest.getAmount().compareTo(new BigDecimal(0)) > 0) {
            servicePaymentRequest.setScheduleServiceIds(scheduleServiceIds);
            paymentService.createPayment(servicePaymentRequest);
        }

        // Create drug payment if needed
        if(drugPaymentRequest.getAmount().compareTo(new BigDecimal(0)) > 0) {
            drugPaymentRequest.setAssignDrugIds(assignDrugIds);
            paymentService.createPayment(drugPaymentRequest);
        }
    
        return treatmentPhaseReturn;
    }


    public boolean deleteScheduleService(UUID scheduleServiceId){
        ScheduleService scheduleService = scheduleServiceRepository.findById(scheduleServiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Schedule service not found"));
        
        if(scheduleService.getSchedule() != null){
            throw new ResourceConflictException("Schedule service is associated with a schedule");
        }

        if(!(scheduleService.getTreatmentPhase().getTreatment().getStatus().equals(Treatment.Status.AWAITING_CONTRACT_SIGNED)
        || scheduleService.getTreatmentPhase().getTreatment().getStatus().equals(Treatment.Status.IN_PROGRESS))){
            throw new ResourceConflictException("Treatment must be in progress or are awaiting contract signed.");
        }
        
        Payment payment = scheduleService.getPayment();

        if(payment != null){
            if(payment.getStatus().equals(Payment.Status.PENDING)){
                payment.setAmount(payment.getAmount().subtract(scheduleService.getService().getPrice()));
                if(payment.getAmount().compareTo(new BigDecimal(0)) == 0){
                    paymentRepository.delete(payment);
                }
                else paymentRepository.save(payment);
            }
            else if(payment.getStatus().equals(Payment.Status.COMPLETED)){
                refundRepository.save(Refund.builder()
                    .payment(payment)
                    .amount(scheduleService.getService().getPrice())
                    .refundDate(LocalDateTime.now())
                    .refundMethod("Refund")
                    .reason("Refund for canceling a schedule service")
                    .user(scheduleService.getTreatmentPhase().getTreatment().getPatient())
                    .build());
            }
        }

        scheduleServiceRepository.delete(scheduleService);
        return true;
    }
}
