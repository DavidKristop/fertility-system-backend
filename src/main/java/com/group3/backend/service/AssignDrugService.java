package com.group3.backend.service;

import com.group3.backend.dto.response.AssignDrugResponse;
import com.group3.backend.mapper.AssignDrugMapper;
import com.group3.backend.model.AssignDrug;
import com.group3.backend.model.AssignDrug.Status;
import com.group3.backend.repository.AssignDrugRepository;
import com.group3.backend.utils.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignDrugService {

    private final AssignDrugRepository assignDrugRepository;
    private final AssignDrugMapper assignDrugMapper;
    private final CurrentUserUtils currentUserUtils;

    public Page<AssignDrugResponse> getMyAssignedDrugs(Status status, int page, int size) {
    UUID patientId = currentUserUtils.getCurrentUserId();
    Pageable pageable = PageRequest.of(page, size);

    Page<AssignDrug> pageResult;
    if (status != null) {
        pageResult = assignDrugRepository
            .findByTreatmentPhase_Treatment_Patient_IdAndStatus(patientId, status, pageable);
    } else {
        pageResult = assignDrugRepository
            .findByTreatmentPhase_Treatment_Patient_Id(patientId, pageable);
    }

    return pageResult.map(assignDrugMapper::toAssignDrugResponse);
}

    public Page<AssignDrugResponse> getAllAssignedDrugs(Status status, String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AssignDrug> pageResult;

        if (status != null && keyword != null && !keyword.isBlank()) {
            pageResult = assignDrugRepository
                .findByStatusAndTreatmentPhase_Treatment_Patient_FullNameContainingIgnoreCase(status, keyword, pageable);
        } else if (status != null) {
            pageResult = assignDrugRepository.findByStatus(status, pageable);
        } else if (keyword != null && !keyword.isBlank()) {
            pageResult = assignDrugRepository
                .findByTreatmentPhase_Treatment_Patient_FullNameContainingIgnoreCase(keyword, pageable);
        } else {
            pageResult = assignDrugRepository.findAll(pageable);
        }

        return pageResult.map(assignDrugMapper::toAssignDrugResponse);
    }

    @Transactional
    public void markAsTaken(UUID assignDrugId) {
        AssignDrug assignDrug = assignDrugRepository.findById(assignDrugId)
                .orElseThrow(() -> new RuntimeException("AssignDrug not found"));

        if (assignDrug.getPayment() == null || assignDrug.getPayment().getStatus() != com.group3.backend.model.Payment.Status.COMPLETED) {
            throw new RuntimeException("Payment not completed");
        }

        assignDrug.setStatus(Status.COMPLETED);
        assignDrugRepository.save(assignDrug);
    }

    @Transactional
    public void cancelAssignDrug(UUID assignDrugId) {
        AssignDrug assignDrug = assignDrugRepository.findById(assignDrugId)
                .orElseThrow(() -> new RuntimeException("AssignDrug not found"));

        assignDrug.setStatus(Status.CANCELLED);
        assignDrugRepository.save(assignDrug);
    }
} 
