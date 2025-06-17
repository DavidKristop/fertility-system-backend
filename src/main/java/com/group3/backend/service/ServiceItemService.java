package com.group3.backend.service;

import com.group3.backend.dto.response.ServiceResponse;
import com.group3.backend.model.Service;
import com.group3.backend.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceItemService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<ServiceResponse> getAllServices(boolean onlyActive){
        List<Service> services = onlyActive
                ? serviceRepository.findByIsActiveTrue()
                : serviceRepository.findAll();

        return services.stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());
    }

    public ServiceResponse getServiceById(UUID id){
        Service svc = serviceRepository.findById(id)
                                        .orElseThrow(() -> new IllegalArgumentException("Service not found: " + id));
        return toResponse(svc);
    }

    public ServiceResponse toResponse(Service svc){
        return new ServiceResponse(
          svc.getId(),
          svc.getName(),
          svc.getDescription(),
          svc.getPrice(),
          svc.getUnit(),
          svc.isActive()
        );
    }
}
