package com.group3.backend.service;

import com.group3.backend.dto.response.DrugResponse;
import com.group3.backend.model.Drug;
import com.group3.backend.repository.DrugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DrugService {

    @Autowired
    private DrugRepository drugRepository;

    public List<DrugResponse> getAllDrugs(boolean onlyActive){
        List<Drug> drugs = onlyActive
                ? drugRepository.findByIsActiveTrue()
                : drugRepository.findAll();

        return drugs.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public DrugResponse getDrugById(UUID id){
        Drug drug = drugRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Drug not found: " + id));
        return toResponse(drug);
    }

    public DrugResponse toResponse (Drug drug){
        return new DrugResponse(
            drug.getId(),
            drug.getName(),
            drug.getDescription(),
            drug.getPrice(),
            drug.getUnit(),
            drug.isActive()
        );
    }
}
