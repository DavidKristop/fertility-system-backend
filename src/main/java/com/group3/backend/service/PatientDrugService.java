package com.group3.backend.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group3.backend.model.PatientDrug;
import com.group3.backend.repository.PatientDrugRepository;

@Service
public class PatientDrugService {
    @Autowired
    PatientDrugRepository patientDrugRepository;

    public List<PatientDrug> getPatientDrugsByPatientIdAndDateBetween(UUID patientId, LocalDate start, LocalDate end){
        return patientDrugRepository.findOverlappingPatientDrugs(patientId, start, end);
    }
        
}
