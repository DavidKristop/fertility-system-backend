package com.group3.backend.service;

import com.group3.backend.dto.request.Protocol.ProtocolCreateRequest;
import com.group3.backend.dto.request.Protocol.ProtocolDrugCreateRequest;
import com.group3.backend.dto.request.Protocol.ProtocolPhaseCreateRequest;
import com.group3.backend.dto.request.Protocol.ProtocolServiceCreateRequest;
import com.group3.backend.exception.ResourceNotFoundException;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class TreatmentProtocolServiceService {

    @Autowired
    private TreatmentProtocolRepository protocolRepository;
    
    @Autowired
    private DrugRepository drugRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Transactional
    public TreatmentProtocol createProtocol(ProtocolCreateRequest request) {
        TreatmentProtocol protocol = TreatmentProtocol.builder()
                .title(request.getName())
                .description(request.getDescription())
                .isActive(true)
                .build();

        if (request.getPhases() != null) {
            List<TreatmentProtocolPhase> phases = new ArrayList<>();
            for (ProtocolPhaseCreateRequest phaseRequest : request.getPhases()) {
                TreatmentProtocolPhase phase = TreatmentProtocolPhase.builder()
                        .title(phaseRequest.getTitle())
                        .description(phaseRequest.getDescription())
                        .totalAmount(phaseRequest.getTotalAmount())
                        .refundCondition(phaseRequest.getRefundCondition())
                        .refundAmount(phaseRequest.getRefundAmount())
                        .position(phaseRequest.getPosition())
                        .treatmentProtocol(protocol)
                        .build();

                if (phaseRequest.getServices() != null) {
                    List<TreatmentProtocolService> services = new ArrayList<>();
                    for (ProtocolServiceCreateRequest serviceRequest : phaseRequest.getServices()) {
                        Service service = serviceRepository.findById(serviceRequest.getServiceId())
                            .orElseThrow(() -> new ResourceNotFoundException("Service not found"));
                        
                        TreatmentProtocolService protocolService = TreatmentProtocolService.builder()
                                .service(service)
                                .amount(serviceRequest.getAmount())
                                .treatmentProtocolPhase(phase)
                                .build();
                        services.add(protocolService);
                    }
                    phase.setServices(services);
                }

                if (phaseRequest.getDrugs() != null) {
                    List<TreatmentProtocolDrug> drugs = new ArrayList<>();
                    for (ProtocolDrugCreateRequest drugRequest : phaseRequest.getDrugs()) {
                        Drug drug = drugRepository.findById(drugRequest.getDrugId())
                            .orElseThrow(() -> new ResourceNotFoundException("Drug not found"));
                        
                        TreatmentProtocolDrug protocolDrug = TreatmentProtocolDrug.builder()
                                .drug(drug)
                                .amount(drugRequest.getAmount())
                                .treatmentProtocolPhase(phase)
                                .build();
                        drugs.add(protocolDrug);
                    }
                    phase.setDrugs(drugs);
                }

                phases.add(phase);
            }
            protocol.setPhases(phases);
        }

        return protocolRepository.save(protocol);
    }

    public List<TreatmentProtocol> getAllProtocol() {
        List<TreatmentProtocol> protocols = protocolRepository.findAll();
        for (TreatmentProtocol protocol : protocols) {
            protocol.setPhases(sortProtocolPhase(protocol.getPhases()));
        }
        return protocols;
    }

    public TreatmentProtocol getProtocolById(UUID id) {
        TreatmentProtocol protocol = protocolRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Protocol not found"));
        protocol.setPhases(sortProtocolPhase(protocol.getPhases()));
        return protocol;
    }

    private List<TreatmentProtocolPhase> sortProtocolPhase(List<TreatmentProtocolPhase> phases){
        phases.sort(Comparator.comparingInt(TreatmentProtocolPhase::getPosition));
        return phases;
    }
}
