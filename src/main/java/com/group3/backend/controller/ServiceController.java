package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.ServiceResponse;
import com.group3.backend.mapper.ServiceMapper;
import com.group3.backend.service.ServiceItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/services")
public class ServiceController {
    @Autowired
    private ServiceItemService serviceItemService;

    @Autowired
    private ServiceMapper serviceMapper;

    @GetMapping
    public ResponseEntity<Response<List<ServiceResponse>>> list(@RequestParam(defaultValue = "true") boolean onlyActive){
        return ResponseEntity.ok(new Response<>(serviceItemService.getAllServices(onlyActive).stream()
                .map(serviceMapper::toResponse)
                .collect(Collectors.toList()), 
                "Services retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<ServiceResponse>> get(@PathVariable UUID id){
        ServiceResponse serviceResponse = serviceMapper.toResponse(serviceItemService.getServiceById(id));
        return ResponseEntity.ok(new Response<>(serviceResponse, "Service retrieved successfully"));
    }
}
