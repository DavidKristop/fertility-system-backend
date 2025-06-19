package com.group3.backend.service;

import com.group3.backend.constants.TreatmentStatus;
import com.group3.backend.dto.request.Treatment.POST.DrugRequest;
import com.group3.backend.dto.request.Treatment.POST.ScheduleRequest;
import com.group3.backend.dto.request.Treatment.POST.ServiceRequest;
import com.group3.backend.dto.request.Treatment.POST.TreatmentCreateRequest;
import com.group3.backend.dto.request.Treatment.POST.TreatmentPhaseRequest;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.model.TreatmentProtocol;
import com.group3.backend.model.ScheduleService;
import com.group3.backend.model.PatientDrug;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.Service;
import com.group3.backend.model.Drug;
import com.group3.backend.model.User;
import com.group3.backend.repository.TreatmentRepository;
import com.group3.backend.repository.TreatmentPhaseRepository;
import com.group3.backend.repository.TreatmentProtocolRepository;
import com.group3.backend.repository.ScheduleServiceRepository;
import com.group3.backend.repository.DrugPatientRepository;
import com.group3.backend.repository.ServiceRepository;
import com.group3.backend.repository.DrugRepository;
import com.group3.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@org.springframework.stereotype.Service
public class TreatmentService {
    
    private final TreatmentRepository treatmentRepository;
    private final ServiceRepository serviceRepository;
    private final DrugRepository drugRepository;
    private final UserRepository userRepository;
    private final TreatmentProtocolRepository treatmentProtocolRepository;

    @Autowired
    public TreatmentService(TreatmentRepository treatmentRepository,
                           TreatmentPhaseRepository treatmentPhaseRepository,
                           ScheduleServiceRepository scheduleServiceRepository,
                           DrugPatientRepository drugPatientRepository,
                           ServiceRepository serviceRepository,
                           DrugRepository drugRepository,
                           UserRepository userRepository,
                           TreatmentProtocolRepository treatmentProtocolRepository) {
        this.treatmentRepository = treatmentRepository;
        this.serviceRepository = serviceRepository;
        this.drugRepository = drugRepository;
        this.userRepository = userRepository;
        this.treatmentProtocolRepository = treatmentProtocolRepository;
    }


    @Transactional
    public Treatment createTreatment(TreatmentCreateRequest request) {
        // Create treatment
        Treatment treatment = new Treatment();
        TreatmentProtocol protocol = treatmentProtocolRepository.findById(request.getProtocolId())
            .orElseThrow(() -> new RuntimeException("Treatment protocol not found"));

        treatment.setStartDate(Date.valueOf(request.getStartDate()));
        treatment.setEndDate(Date.valueOf(request.getEndDate()));
        treatment.setDiagnosis(request.getDiagnosis());
        treatment.setTreatmentProtocol(protocol);

        // Fetch user objects from repository
        User patient = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        
        treatment.setPatient(patient);
        treatment.setDoctor(doctor);
        treatment.setTreatmentStatus(TreatmentStatus.IN_PROGRESS);
        List<TreatmentPhase> phases = new ArrayList<>();

        // Create treatment phases
        for (TreatmentPhaseRequest phaseRequest : request.getPhases()) {
            TreatmentPhase phase = new TreatmentPhase();
            phase.setTitle(phaseRequest.getTitle());
            phase.setDescription(phaseRequest.getDescription());
            phase.setTreatment(treatment);
            
            // Calculate total amount for phase
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            //Create schedule
            if(phaseRequest.getSchedules() != null){
                for (ScheduleRequest scheduleRequest : phaseRequest.getSchedules()){
                    Schedule schedule = new Schedule();
                    schedule.setDoctor(doctor);
                    schedule.setPatient(patient);
                    schedule.setAppointmentDateTime(scheduleRequest.getAppointmentDateTime());
                    schedule.setEstimatedTime(scheduleRequest.getEstimatedTime());
                    schedule.setStatus(Schedule.Status.PENDING);

                    for (ServiceRequest serviceRequest : scheduleRequest.getServices()){
                        ScheduleService scheduleService = new ScheduleService();
                        Service service = serviceRepository.findById(serviceRequest.getId())
                            .orElseThrow(() -> new RuntimeException("Service not found"));
                        scheduleService.setService(service);
                        scheduleService.setAmount(serviceRequest.getAmount());
                        scheduleService.setNotes(serviceRequest.getNotes());
                        schedule.getScheduleServices().add(scheduleService);
                        totalAmount = totalAmount.add(service.getPrice().multiply(BigDecimal.valueOf(serviceRequest.getAmount())));
                    }

                    phase.getSchedules().add(schedule);
                }
            }

            // Create drug patients
            if (phaseRequest.getDrugs() != null) {
                for (DrugRequest drugRequest : phaseRequest.getDrugs()) {
                    PatientDrug patientDrug = new PatientDrug();
                    Drug drug = drugRepository.findById(drugRequest.getId())
                        .orElseThrow(() -> new RuntimeException("Drug not found"));
                    patientDrug.setDrug(drug);
                    patientDrug.setDosage(drugRequest.getDosage());
                    patientDrug.setUsageInstructions(drugRequest.getUsageInstructions());
                    patientDrug.setStartDate(Date.valueOf(request.getStartDate()));
                    patientDrug.setEndDate(Date.valueOf(request.getEndDate()));
                    patientDrug.setTreatmentPhase(phase);
                    phase.getPatientDrugs().add(patientDrug);
                    totalAmount = totalAmount.add(drug.getPrice().multiply(BigDecimal.valueOf(drugRequest.getAmount())));
                }
            }

            phase.setTotalAmount(totalAmount);
            phases.add(phase);
        }

        BigDecimal totalTreatmentAmount = phases.stream()
            .map(TreatmentPhase::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        treatment.setPhases(phases);
        treatment.setTotalAmount(totalTreatmentAmount);
        treatmentRepository.save(treatment);

        return treatment;
    }
}
