package com.group3.backend.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.group3.backend.exception.ResourceConflictException;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.model.AssignDrug;
import com.group3.backend.repository.AssignDrugRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignDrugService{
    private final AssignDrugRepository assignDrugRepository;

    public Page<AssignDrug> getAssignDrugByPatientId(UUID patientId, List<AssignDrug.Status> statuses, Pageable pageable){
        return assignDrugRepository.findByTreatmentPhaseTreatmentPatientIdAndStatusIn(patientId, statuses, pageable);
    }

    public Page<AssignDrug> getAssignDrugByStatus(List<AssignDrug.Status> statuses, Pageable pageable){
        return assignDrugRepository.findByStatusIn(statuses, pageable);
    }

    public AssignDrug getAssignDrugByIdAndPatientId(UUID patientId, UUID id){
        AssignDrug assignDrug = assignDrugRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Assign drug not found"));
        if(!assignDrug.getTreatmentPhase().getTreatment().getPatient().getId().equals(patientId)){
            throw new ResourceConflictException("Assign drug is not for this patient");
        }
        return assignDrug;
    }

    public AssignDrug getAssignDrugById(UUID id){
        return assignDrugRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Assign drug not found"));
    }

    public AssignDrug completeAssignDrug(UUID id){
        AssignDrug assignDrug = getAssignDrugById(id);
        assignDrug.setStatus(AssignDrug.Status.COMPLETED);
        return assignDrugRepository.save(assignDrug);
    }

    public AssignDrug cancelAssignDrug(UUID id){
        AssignDrug assignDrug = getAssignDrugById(id);
        assignDrug.setStatus(AssignDrug.Status.CANCELLED);
        return assignDrugRepository.save(assignDrug);
    }
}
