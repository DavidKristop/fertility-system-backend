package com.group3.backend.service;

import com.group3.backend.dto.request.ServiceCreateRequest;
import com.group3.backend.dto.request.ServiceUpdateRequest;
import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.exception.ValidationException;
import com.group3.backend.model.Service;
import com.group3.backend.repository.ServiceRepository;
import com.group3.backend.config.TimeZoneConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceItemService {
    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private TimeZoneConfig timeZoneConfig;

    public Page<Service> getServices(String name, boolean isActive, Pageable pageable){
        return serviceRepository.findByNameIgnoreCaseContainingAndIsActive(name, isActive, pageable);
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

        if(existingService.isActive()){
            throw new ValidationException("Cannot update active service");
        }

        if(existingService.getLastDeactivated() == null){
            throw new ValidationException("Cannot update service that has not been deactivated");
        }

        // Check if service can be updated (must be deactivated for 120 days)
        if (existingService.getLastDeactivated() != null) {
            long daysSinceDeactivation = Duration.between(existingService.getLastDeactivated(), LocalDateTime.now(timeZoneConfig.defaultZoneId())).toDays();
            if (daysSinceDeactivation < 120) {
                throw new ValidationException("Cannot update service that was deactivated less than 120 days ago");
            }
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
        service.setLastDeactivated(LocalDateTime.now(timeZoneConfig.defaultZoneId()));
        serviceRepository.save(service);
    }

    public void reActivateService(UUID id) {
        Service service = getServiceById(id);

        // Check if service is already activated
        if (service.isActive()) {
            throw new ValidationException("Service is already activated");
        }

        service.setActive(true);
        service.setLastDeactivated(null);
        serviceRepository.save(service);
    }
}
