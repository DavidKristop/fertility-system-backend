package com.group3.backend.service;

import com.group3.backend.config.TimeZoneConfig;
import com.group3.backend.dto.request.DrugCreateRequest;
import com.group3.backend.dto.request.DrugUpdateRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.ValidationException;
import com.group3.backend.model.Drug;
import com.group3.backend.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class DrugService {

    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private TimeZoneConfig timeZoneConfig;

    public Page<Drug> searchDrugs(String name, boolean isActive, Pageable pageable) {
        return drugRepository.findByNameIgnoreCaseContainingAndIsActive(name, isActive, pageable);
    }

    public Drug getDrugById(UUID id) {
        return drugRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drug Not Found!"));
    }

    public Drug createDrug(DrugCreateRequest request) {
        // Check for duplicate drug name
        if (drugRepository.existsByName(request.getName())) {
            throw new ValidationException("Drug name already exists");
        }

        Drug drug = Drug.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .unit(request.getUnit())
                .isActive(true)
                .build();
        
        return drugRepository.save(drug);
    }

    public Drug updateDrug(UUID id, DrugUpdateRequest request) {
        Drug existingDrug = getDrugById(id);

        // Check if drug name is being changed and if new name exists
        if (!existingDrug.getName().equals(request.getName()) && drugRepository.existsByName(request.getName())) {
            throw new ValidationException("Drug name already exists");
        }

        Drug updatedDrug = Drug.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .unit(request.getUnit())
                .isActive(true)  // Always set to true after update
                .build();

        return drugRepository.save(updatedDrug);
    }

    public void deactivateDrug(UUID id) {
        Drug drug = getDrugById(id);

        // Check if drug is already deactivated
        if (!drug.isActive()) {
            throw new ValidationException("Drug is already deactivated");
        }
        
        drug.setActive(false);
        drugRepository.save(drug);
    }

    public void reactivateDrug(UUID id) {
        Drug drug = getDrugById(id);

        // Check if drug is already activated
        if (drug.isActive()) {
            throw new ValidationException("Drug is already activated");
        }
        
        drug.setActive(true);
        drugRepository.save(drug);
    }
}
