package com.group3.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group3.backend.model.PatientDrug;

public interface PatientDrugRepository extends JpaRepository<PatientDrug, UUID> {
    List<PatientDrug> findByIdIn(List<UUID> ids);

    @Query("""
        SELECT pd FROM PatientDrug pd 
        WHERE pd.assignDrug.treatmentPhase.treatment.patient.id = :patientId 
        AND (
            (pd.startDate <= :start 
            AND pd.endDate >= :end)
            OR (pd.startDate >= :start
            AND pd.startDate <= :end)
            OR (pd.endDate >= :start
            AND pd.endDate <= :end)
            OR (pd.startDate >= :start
            AND pd.endDate <= :end)
        )
    """)
    List<PatientDrug> findOverlappingPatientDrugs(
        @Param("patientId") UUID patientId,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}