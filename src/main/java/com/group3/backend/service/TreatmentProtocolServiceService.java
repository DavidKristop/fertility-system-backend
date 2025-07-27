package com.group3.backend.service;

import com.group3.backend.dto.request.TreatmentProtocolCreateRequest;
import com.group3.backend.dto.request.TreatmentProtocolPhaseRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.ValidationException;
import com.group3.backend.model.Drug;
import com.group3.backend.model.Service;
import com.group3.backend.model.TreatmentProtocol;
import com.group3.backend.model.TreatmentProtocolPhase;
import com.group3.backend.model.TreatmentProtocolService;
import com.group3.backend.model.TreatmentProtocolDrug;
import com.group3.backend.repository.DrugRepository;
import com.group3.backend.repository.ServiceRepository;
import com.group3.backend.repository.TreatmentProtocolRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@org.springframework.stereotype.Service
public class TreatmentProtocolServiceService {

    @Autowired
    private TreatmentProtocolRepository protocolRepository;
    
    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Transactional
    public TreatmentProtocol createProtocol(TreatmentProtocolCreateRequest request) {
        // Validate phase count
        if (request.getPhases().size() < 1 || request.getPhases().size() > 20) {
            throw new ValidationException("Protocol must have between 1 and 20 phases");
        }

        if (protocolRepository.existsByTitle(request.getTitle())) {
            throw new ValidationException("Protocol title already exists");
        }

        TreatmentProtocol protocol = TreatmentProtocol.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        List<TreatmentProtocolPhase> phases = new ArrayList<>();
        for (int i = 0; i < request.getPhases().size(); i++) {
            TreatmentProtocolPhaseRequest phaseRequest = request.getPhases().get(i);
            if(phaseRequest.getServiceIds().isEmpty() && phaseRequest.getDrugIds().isEmpty()) {
                throw new ValidationException("Phase must have at least one service or drug");
            }

            // Validate services count
            if (phaseRequest.getServiceIds().size() > 40) {
                throw new ValidationException("Phase can have at most 40 services");
            }

            // Validate drugs count
            if (phaseRequest.getDrugIds().size() > 99) {
                throw new ValidationException("Phase can have at most 99 drugs");
            }


            TreatmentProtocolPhase phase = TreatmentProtocolPhase.builder()
                    .title(phaseRequest.getTitle())
                    .description(phaseRequest.getDescription())
                    .position(i)
                    .phaseModifierPercentage(phaseRequest.getPhaseModifierPercentage())
                    .treatmentProtocol(protocol)
                    .refundPercentage(request.getRefundPercentage())
                    .build();

            // Create protocol services
            List<TreatmentProtocolService> protocolServices = phaseRequest.getServiceIds().stream()
                    .map(serviceId -> {
                        Service service = serviceRepository.findById(serviceId)
                                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
                        if(!service.isActive()){
                            throw new ValidationException("Service is not active");
                        }
                        return TreatmentProtocolService.builder()
                                .service(service)
                                .treatmentProtocolPhase(phase)
                                .build();
                    })
                    .collect(Collectors.toList());

            // Create protocol drugs
            List<TreatmentProtocolDrug> protocolDrugs = phaseRequest.getDrugIds().stream()
                    .map(drugId -> {
                        Drug drug = drugRepository.findById(drugId)
                                .orElseThrow(() -> new ResourceNotFoundException("Drug not found"));
                        if(!drug.isActive()){
                            throw new ValidationException("Drug is not active");
                        }
                        return TreatmentProtocolDrug.builder()
                                .drug(drug)
                                .treatmentProtocolPhase(phase)
                                .build();
                    })
                    .collect(Collectors.toList());

            phase.setServices(protocolServices);
            phase.setDrugs(protocolDrugs);
            phases.add(phase);
        }

        protocol.setPhases(phases);
        return protocolRepository.save(protocol);                        
    }

    public Page<TreatmentProtocol> getProtocols(String title, boolean isActive, Pageable pageable) {
        Page<TreatmentProtocol> protocols = protocolRepository.findByTitleIgnoreCaseContainingAndIsActive(title, isActive, pageable);
        protocols.forEach(protocol -> protocol.setPhases(sortProtocolPhase(protocol.getPhases())));
        return protocols;
    }

    public TreatmentProtocol getProtocolById(UUID id) {
        TreatmentProtocol protocol = protocolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Protocol not found"));
        protocol.setPhases(sortProtocolPhase(protocol.getPhases()));
        return protocol;
    }

    @Transactional
    public TreatmentProtocol deactivateProtocol(UUID id) {
        TreatmentProtocol protocol = getProtocolById(id);
        if(!protocol.isActive()){
            throw new ValidationException("Protocol is not active");
        }
        protocol.setActive(false);
        return protocolRepository.save(protocol);
    }

    @Transactional
    public TreatmentProtocol activateProtocol(UUID id) {
        TreatmentProtocol protocol = getProtocolById(id);
        if (protocol.isActive()) {
            throw new ValidationException("Protocol is already active");
        }
        protocol.setActive(true);
        return protocolRepository.save(protocol);
    }

    public static BigDecimal calculateEstimatedPriceByPhase(TreatmentProtocolPhase phase, boolean isByPhase) {
        BigDecimal phasePrice = BigDecimal.ZERO;

        // Calculate services price
        if (phase.getServices() != null) {
            phasePrice = phase.getServices().stream()
                    .map(service -> service.getService().getPrice())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Calculate drugs price
        if (phase.getDrugs() != null) {
            phasePrice = phasePrice.add(phase.getDrugs().stream()
                    .map(drug -> drug.getDrug().getPrice().multiply(BigDecimal.valueOf(drug.getAmount())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        // Apply phase modifier percentage
        if (phase.getPhaseModifierPercentage() != null && isByPhase) {
            phasePrice = phasePrice.multiply(phase.getPhaseModifierPercentage());
        }

        return phasePrice;
    }

    public static BigDecimal calculateEstimatedPrice(TreatmentProtocol protocol, boolean isByPhase) {
        if (protocol.getPhases() == null || protocol.getPhases().isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (TreatmentProtocolPhase phase : protocol.getPhases()) {
            BigDecimal phasePrice = calculateEstimatedPriceByPhase(phase, isByPhase);
            total = total.add(phasePrice);
        }

        return total;
    }

    private List<TreatmentProtocolPhase> sortProtocolPhase(List<TreatmentProtocolPhase> phases) {
        return phases.stream()
                .sorted(Comparator.comparing(TreatmentProtocolPhase::getPosition))
                .collect(Collectors.toList());
    }
}
