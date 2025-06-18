package com.group3.backend.controller;

import com.group3.backend.dto.response.ServiceResponse;
import com.group3.backend.service.ServiceItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/services")
public class ServiceController {
    @Autowired
    private ServiceItemService serviceItemService;

    @GetMapping
    public List<ServiceResponse> list(@RequestParam(defaultValue = "true") boolean onlyActive){
        return serviceItemService.getAllServices(onlyActive);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse> get(@PathVariable UUID id){
        ServiceResponse serviceResponse = serviceItemService.getServiceById(id);
        return serviceResponse != null ? ResponseEntity.ok(serviceResponse) : ResponseEntity.notFound().build();
    }
}
