package com.group3.backend.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.UserBusyResponse;
import com.group3.backend.mapper.AppointmentRequestMapper;
import com.group3.backend.mapper.TreatmentMapper;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.Treatment;
import com.group3.backend.repository.RequestAppointmentRepository;
import com.group3.backend.repository.TreatmentRepository;
import com.group3.backend.service.RequestAppointmentService;
import com.group3.backend.service.TreatmentService;
import com.group3.backend.utils.CurrentUserUtils;

@RestController
@RequestMapping("/api/patient-management")
public class PatientManagementController {

    @Autowired
    TreatmentRepository treatmentRepository;

    @Autowired
    RequestAppointmentRepository requestAppointmentRepository;

    @Autowired
    AppointmentRequestMapper appointmentRequestMapper;
    
    @Autowired
    TreatmentMapper treatmentMapper;

    @Autowired
    CurrentUserUtils currentUserUtils;
    
    //Endpoint to check if the patient already in a treatment that is IN_PROGRESS
    // or if the patient has a PENDING request appointment or a PEDING scheduled attached to that request
    // or if the patient has a PENDING consultation schedule
    @GetMapping("/am-i-busy")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<UserBusyResponse>> amIBusy() {

        UUID patientId = currentUserUtils.getCurrentUserId();
        UserBusyResponse userBusyResponse = new UserBusyResponse();

        Optional<Treatment> treatment = treatmentRepository.findByPatientIdAndStatusIn(patientId, List.of(Treatment.Status.IN_PROGRESS, Treatment.Status.AWAITING_CONTRACT_SIGNED)); 
        treatment.ifPresent(value -> userBusyResponse.setTreatment(treatmentMapper.toResponse(value)));

        Optional<RequestAppointment> pendingRequestAppointment = requestAppointmentRepository.findByPatientIdAndStatus(patientId, RequestAppointment.Status.PENDING);
        pendingRequestAppointment.ifPresent(value -> userBusyResponse.setRequestAppointment(appointmentRequestMapper.toResponse(value)));

        if(!pendingRequestAppointment.isPresent()){
            Optional<RequestAppointment> acceptedRequestAppointmentWithPendingSchedule = requestAppointmentRepository.findByPatientIdAndStatusAndScheduleStatus(patientId, RequestAppointment.Status.ACCEPTED, Schedule.Status.PENDING);
            acceptedRequestAppointmentWithPendingSchedule.ifPresent(value -> userBusyResponse.setRequestAppointment(appointmentRequestMapper.toResponse(value)));
        }

        return ResponseEntity.ok(new Response<>(userBusyResponse,"Fetching patient business successfully"));
    }
}
