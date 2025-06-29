package com.group3.backend.service;

import com.group3.backend.dto.request.Treatment.TreatmentDrugRequest;
import com.group3.backend.dto.request.Treatment.TreatmentServiceRequest;
import com.group3.backend.exception.ResourceConflictException;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.dto.request.Treatment.TreatmentCreateRequest;
import com.group3.backend.dto.request.Treatment.TreatmentPhaseRequest;
import com.group3.backend.dto.request.Treatment.TreatmentScheduleRequest;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.TreatmentPhase;
import com.group3.backend.model.TreatmentProtocol;
import com.group3.backend.model.TreatmentProtocolPhase;
import com.group3.backend.model.ScheduleService;
import com.group3.backend.model.PatientDrug;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.Service;
import com.group3.backend.model.AssignDrug;
import com.group3.backend.model.Drug;
import com.group3.backend.model.User;
import com.group3.backend.repository.TreatmentRepository;
import com.group3.backend.repository.TreatmentProtocolRepository;
import com.group3.backend.repository.ServiceRepository;
import com.group3.backend.repository.DrugRepository;
import com.group3.backend.repository.ScheduleRepository;
import com.group3.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class TreatmentService {
    @Autowired
    private TreatmentRepository treatmentRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private DrugRepository drugRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TreatmentProtocolRepository treatmentProtocolRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Treatment> getAllTreatmentsByPatientId(UUID patientId){
        return treatmentRepository.findByPatientId(patientId);
    }

    public List<Treatment> getAllTreatmentsByDoctorId(UUID doctorId){
        return treatmentRepository.findByDoctorId(doctorId);
    }

    public Treatment getTreatmentByIdAndPatientId(UUID id, UUID patientId){
        return treatmentRepository.findByIdAndPatientId(id, patientId)
            .orElseThrow(() -> new ResourceNotFoundException("Treatment not found"));
    }

    @Transactional
    public Treatment moveToNextPhase(UUID treatmentId) {
        Treatment treatment = treatmentRepository.findById(treatmentId)
            .orElseThrow(() -> new ResourceNotFoundException("Treatment not found"));

        if(treatment.getStatus() != Treatment.Status.IN_PROGRESS){
            throw new ResourceConflictException("Treatment is not in progress");
        }

        // Get current phase
        TreatmentPhase currentPhase = treatment.getCurrentPhase();
        if (currentPhase == null) {
            throw new ResourceConflictException("No current phase set");
        }

        // Check if all schedules in current phase are complete
        List<Schedule> schedules = scheduleRepository.findByTreatmentPhaseId(currentPhase.getId());
        boolean allSchedulesComplete = schedules.stream()
            .allMatch(s -> s.getStatus() == Schedule.Status.DONE);

        if (!allSchedulesComplete) {
            throw new ResourceConflictException("Not all schedules in current phase are complete");
        }

        // Mark current phase as complete
        currentPhase.setComplete(true);

        // Find next phase with position + 1
        List<TreatmentPhase> sortedPhases = treatment.getPhases().stream()
            .sorted(Comparator.comparingInt(TreatmentPhase::getPosition))
            .collect(Collectors.toList());

        int currentIndex = sortedPhases.indexOf(currentPhase);
        if (currentIndex == sortedPhases.size() - 1) {
            throw new ResourceConflictException("Already at last phase");
        }

        TreatmentPhase nextPhase = sortedPhases.get(currentIndex + 1);
        treatment.setCurrentPhase(nextPhase);

        // Update treatment status if all phases are complete
        boolean allPhasesComplete = sortedPhases.stream()
            .allMatch(phase -> phase.isComplete());

        if (allPhasesComplete) {
            treatment.setStatus(Treatment.Status.COMPLETED);
        }

        return treatmentRepository.save(treatment);
    }

    public Treatment getTreatmentByIdAndDoctorId(UUID id, UUID doctorId){
        return treatmentRepository.findByIdAndDoctorId(id, doctorId)
            .orElseThrow(() -> new ResourceNotFoundException("Treatment not found"));
    }

    @Transactional
    public Treatment createTreatment(TreatmentCreateRequest request, Treatment.Status treatmentStatus) {
        // Create treatment
        Treatment treatment = new Treatment();
        TreatmentProtocol protocol = treatmentProtocolRepository.findById(request.getProtocolId())
            .orElseThrow(() -> new ResourceNotFoundException("Treatment protocol not found"));

        treatment.setDescription(request.getDescription());
        treatment.setTreatmentProtocol(protocol);
        treatment.setPaymentMode(request.getPaymentMode());

        // Fetch user objects from repository
        User patient = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        treatment.setPatient(patient);
        treatment.setDoctor(doctor);
        treatment.setStatus(treatmentStatus);
        List<TreatmentPhase> phases = new ArrayList<>();

        // Create treatment phases based on protocol phases
        List<TreatmentProtocolPhase> protocolPhases = protocol.getPhases();
        protocolPhases.sort(Comparator.comparingInt(TreatmentProtocolPhase::getPosition));

        for (int i = 0; i < protocolPhases.size(); i++) {
            TreatmentProtocolPhase protocolPhase = protocolPhases.get(i);
            TreatmentPhase phase = new TreatmentPhase();
            phase.setTitle(protocolPhase.getTitle());
            phase.setDescription(protocolPhase.getDescription());
            phase.setTreatment(treatment);
            phase.setPosition(i);
            phase.setPhaseModifierPercentage(protocolPhase.getPhaseModifierPercentage());
            phase.setRefundPercentage(protocolPhase.getRefundPercentage());
            phase.setComplete(false);

            // Add services from protocol phase
            List<ScheduleService> scheduleServices = new ArrayList<>();
            for (Service service : protocolPhase.getServices().stream()
            .map(protocolService->protocolService.getService()).toList()) {
                ScheduleService scheduleService = new ScheduleService();
                scheduleService.setService(service);
                scheduleService.setTreatmentPhase(phase);
                scheduleServices.add(scheduleService);
            }

            // Add drugs from protocol phase
            List<PatientDrug> patientDrugs = new ArrayList<>();
            for (Drug drug : protocolPhase.getDrugs().stream()
            .map(protocolDrug->protocolDrug.getDrug()).toList()) {
                PatientDrug patientDrug = new PatientDrug();
                patientDrug.setDrug(drug);
                patientDrugs.add(patientDrug);
            }
            AssignDrug assignDrug = new AssignDrug();
            assignDrug.setPatientDrugs(patientDrugs);
            assignDrug.setTreatmentPhase(phase);
            assignDrug.setStatus(AssignDrug.Status.PENDING);
            
            phase.setAssignDrugs(List.of(assignDrug));
            phase.setScheduleServices(scheduleServices);
            phases.add(phase);
        }

        // Set first phase as current phase
        if (!phases.isEmpty()) {
            treatment.setCurrentPhase(phases.get(0));
        }

        treatment.setPhases(phases);
        treatmentRepository.save(treatment);

        return treatment;
    }

    public static BigDecimal calculatePhaseEstimatePrice(TreatmentPhase phase){
        BigDecimal phasePrice = BigDecimal.ZERO;

        phasePrice.add(phase.getScheduleServices().stream()
                .map(scheduleService -> scheduleService.getService().getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        phasePrice.add(phase.getAssignDrugs().stream()
                .map(assignDrug->{
                    return assignDrug.getPatientDrugs().stream()
                    .map(patientDrug->patientDrug.getDrug().getPrice()
                    .multiply(BigDecimal.valueOf(patientDrug.getAmount())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        // Apply phase modifier percentage
        if (phase.getPhaseModifierPercentage() != null) {
            phasePrice = phasePrice.multiply(phase.getPhaseModifierPercentage());
        }

        return phasePrice;
    }

    public static BigDecimal calculateEstimatedPrice(Treatment treatment) {
        if (treatment.getPhases() == null || treatment.getPhases().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (TreatmentPhase phase : treatment.getPhases()) {
            total = total.add(calculatePhaseEstimatePrice(phase));
        }

        return total;
    }

}
