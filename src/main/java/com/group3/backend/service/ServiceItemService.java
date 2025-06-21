package com.group3.backend.service;

import com.group3.backend.exception.ResourceNotFoundException;
import com.group3.backend.model.Service;
import com.group3.backend.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.List;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServiceItemService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<Service> getAllServices(boolean onlyActive){
        List<Service> services = onlyActive
                ? serviceRepository.findByIsActiveTrue()
                : serviceRepository.findAll();

        return services;
    }

    public Service getServiceById(UUID id){
        return serviceRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service Not Found!"));
    }
}
