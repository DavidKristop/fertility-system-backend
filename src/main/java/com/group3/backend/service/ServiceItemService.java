package com.group3.backend.service;

import com.group3.backend.config.EnvironmentConfig;
import com.group3.backend.dto.request.ServiceCreateRequest;
import com.group3.backend.dto.request.ServiceUpdateRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.ValidationException;
import com.group3.backend.model.Service;
import com.group3.backend.repository.ServiceRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceItemService {
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EnvironmentConfig environmentConfig;

    public Page<Service> getServices(String name, boolean isActive, Pageable pageable){
        // Filter out the consulting service
        return serviceRepository.findByNameIgnoreCaseContainingAndIsActiveAndIdNot(
            name, 
            isActive, 
            UUID.fromString(environmentConfig.getConsultationServiceId()),
            pageable
        );
    }

    public Service getServiceById(UUID id){
        return serviceRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Not Found!"));
    }

    public Service createService(ServiceCreateRequest request) {
        // Check for duplicate service name
        if (serviceRepository.existsByName(request.getName())) {
            throw new ValidationException("Service name already exists");
        }

        Service service = Service.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .unit(request.getUnit())
                .build();
        
        return serviceRepository.save(service);
    }

    public Service updateService(UUID id, ServiceUpdateRequest request) {
        Service existingService = getServiceById(id);

        // Check if service name is being changed and if new name exists
        if (!existingService.getName().equals(request.getName()) && serviceRepository.existsByName(request.getName())) {
            throw new ValidationException("Service name already exists");
        }


        Service updatedService = Service.builder()
                .id(id)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .unit(request.getUnit())
                .build();

        return serviceRepository.save(updatedService);
    }

    public void deactivateService(UUID id) {
        Service service = getServiceById(id);

        // Check if service is already deactivated
        if (!service.isActive()) {
            throw new ValidationException("Service is already deactivated");
        }

        service.setActive(false);
        serviceRepository.save(service);
    }

    public void reActivateService(UUID id) {
        Service service = getServiceById(id);

        // Check if service is already activated
        if (service.isActive()) {
            throw new ValidationException("Service is already activated");
        }

        service.setActive(true);
        serviceRepository.save(service);
    }
}
