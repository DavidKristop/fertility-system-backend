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
import com.group3.backend.model.ScheduleService;
import com.group3.backend.model.PatientDrug;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.Service;
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
    public Treatment createTreatment(TreatmentCreateRequest request) {
        // Create treatment
        Treatment treatment = new Treatment();
        TreatmentProtocol protocol = treatmentProtocolRepository.findById(request.getProtocolId())
            .orElseThrow(() -> new ResourceNotFoundException("Treatment protocol not found"));

        treatment.setStartDate(Date.valueOf(request.getStartDate()));
        treatment.setEndDate(Date.valueOf(request.getEndDate()));
        treatment.setDiagnosis(request.getDiagnosis());
        treatment.setTreatmentProtocol(protocol);
        treatment.setPaymentMode(request.getPaymentMode());

        // Fetch user objects from repository
        User patient = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        
        treatment.setPatient(patient);
        treatment.setDoctor(doctor);
        treatment.setStatus(Treatment.Status.IN_PROGRESS);
        List<TreatmentPhase> phases = new ArrayList<>();

        // Create treatment phases
        for (int i=0;i<request.getPhases().size();i++) {
            TreatmentPhaseRequest phaseRequest = request.getPhases().get(i);
            TreatmentPhase phase = new TreatmentPhase();
            phase.setTitle(phaseRequest.getTitle());
            phase.setDescription(phaseRequest.getDescription());
            phase.setTreatment(treatment);
            phase.setPosition(i);
            
            // Calculate total amount for phase
            BigDecimal totalAmount = BigDecimal.ZERO;
            
            //Create schedule
            if(phaseRequest.getSchedules() != null){
                for (TreatmentScheduleRequest scheduleRequest : phaseRequest.getSchedules()){
                    Schedule schedule = new Schedule();
                    schedule.setDoctor(doctor);
                    schedule.setPatient(patient);
                    schedule.setAppointmentDateTime(scheduleRequest.getAppointmentDateTime());
                    schedule.setEstimatedTime(scheduleRequest.getEstimatedTime());
                    schedule.setStatus(Schedule.Status.PENDING);
                    schedule.setTreatmentPhase(phase);

                    if(schedule.getEstimatedTime().getTime() <= schedule.getAppointmentDateTime().getTime()){
                        throw new ResourceConflictException("Estimated time must be greater than appointment time");
                    }

                    if(schedule.getEstimatedTime().getTime() - schedule.getAppointmentDateTime().getTime() > 4 * 60 * 60 * 1000){
                        throw new ResourceConflictException("Estimated time must be at most 4 hours after appointment time");
                    }

                    if(checkOverlappingSchedule(doctor.getId(),scheduleRequest.getAppointmentDateTime(),scheduleRequest.getEstimatedTime())){
                        throw new ResourceConflictException("Doctor is already scheduled for another appointment during this time");
                    }
                    for (TreatmentServiceRequest serviceRequest : scheduleRequest.getServices()){
                        ScheduleService scheduleService = new ScheduleService();
                        Service service = serviceRepository.findById(serviceRequest.getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
                        scheduleService.setService(service);
                        scheduleService.setSchedule(schedule);
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
                for (TreatmentDrugRequest drugRequest : phaseRequest.getDrugs()) {
                    PatientDrug patientDrug = new PatientDrug();
                    Drug drug = drugRepository.findById(drugRequest.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Drug not found"));
                    patientDrug.setDrug(drug);
                    patientDrug.setDosage(drugRequest.getDosage());
                    patientDrug.setUsageInstructions(drugRequest.getUsageInstructions());
                    patientDrug.setStartDate(drugRequest.getStartDate());
                    patientDrug.setEndDate(drugRequest.getEndDate());
                    patientDrug.setTreatmentPhase(phase);
                    phase.getPatientDrugs().add(patientDrug);
                    totalAmount = totalAmount.add(drug.getPrice().multiply(BigDecimal.valueOf(drugRequest.getAmount())));
                }
            }
            double phaseMultiplier = 1;
            if(treatment.getPaymentMode().equals(Treatment.PaymentMode.FULL)){
                phaseMultiplier = 1.2;
            }
            phase.setTotalAmount(totalAmount.multiply(BigDecimal.valueOf(phaseMultiplier)));
            phases.add(phase);
        }

        treatment.setPhases(phases);
        treatmentRepository.save(treatment);

        return treatment;
    }

    private boolean checkOverlappingSchedule(UUID doctorId,Timestamp appointmentDateTime, Timestamp estimatedTime){
        List<Schedule> overlappingSchedules = scheduleRepository.findByDoctorIdAndAppointmentDateTimeBetween(
            doctorId,
            appointmentDateTime,
            estimatedTime
        );
        return !overlappingSchedules.isEmpty();
    }
}
