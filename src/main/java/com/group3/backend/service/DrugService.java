package com.group3.backend.service;

import com.group3.backend.dto.request.DrugCreateRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.model.Drug;
import com.group3.backend.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DrugService {

    @Autowired
    private DrugRepository drugRepository;

    public List<Drug> getAllDrugs(boolean onlyActive){
        List<Drug> drugs = onlyActive
                ? drugRepository.findByIsActiveTrue()
                : drugRepository.findAll();

        return drugs;
    }

    public Drug getDrugById(UUID id){
        return drugRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Drug Not Found!"));
    }

    public Drug createDrug(DrugCreateRequest request) {
        Drug drug = Drug.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .unit(request.getUnit())
                .isActive(true)
                .build();
        
        return drugRepository.save(drug);
    }
}
