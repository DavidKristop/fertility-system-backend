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
        return drugRepository.searchDrugs(name, isActive, pageable);
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

        if(existingDrug.isActive()){
            throw new ValidationException("Cannot update active drug");
        }

        if (existingDrug.getLastDeactivated() == null) {
            throw new ValidationException("Cannot update drug that has not been deactivated");
        }

        // Check if drug can be updated (must be deactivated for 120 days)
        if ( existingDrug.getLastDeactivated() != null) {
            long daysSinceDeactivation = Duration.between(existingDrug.getLastDeactivated(), LocalDateTime.now(timeZoneConfig.defaultZoneId())).toDays();
            if (daysSinceDeactivation < 120) {
                throw new ValidationException("Cannot update drug that was deactivated less than 120 days ago");
            }
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
        drug.setLastDeactivated(LocalDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh")));
        drugRepository.save(drug);
    }

    public void reactivateDrug(UUID id) {
        Drug drug = getDrugById(id);

        // Check if drug is already activated
        if (drug.isActive()) {
            throw new ValidationException("Drug is already activated");
        }
        
        drug.setActive(true);
        drug.setLastDeactivated(null);
        drugRepository.save(drug);
    }
}
