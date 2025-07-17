package com.group3.backend.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.group3.backend.constants.Roles;
import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.DrugResponse;
import com.group3.backend.dto.response.PatientEventResponse;
import com.group3.backend.dto.response.UserBusyResponse;
import com.group3.backend.dto.response.UserPatientResponse;
import com.group3.backend.dto.response.Schedule.DoctorScheduleReponse;
import com.group3.backend.dto.response.Treatment.TreatmentPatientDrugResponse;
import com.group3.backend.mapper.AppointmentRequestMapper;
import com.group3.backend.mapper.ScheduleMapper;
import com.group3.backend.mapper.TreatmentMapper;
import com.group3.backend.mapper.UserMapper;
import com.group3.backend.model.PatientDrug;
import com.group3.backend.model.RequestAppointment;
import com.group3.backend.model.Schedule;
import com.group3.backend.model.Treatment;
import com.group3.backend.model.User;
import com.group3.backend.repository.RequestAppointmentRepository;
import com.group3.backend.repository.TreatmentRepository;
import com.group3.backend.repository.UserRepository;
import com.group3.backend.service.PatientDrugService;
import com.group3.backend.service.ScheduleService;
import com.group3.backend.utils.Constants;
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
    ScheduleMapper scheduleMapper;

    @Autowired
    CurrentUserUtils currentUserUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    PatientDrugService patientDrugService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ROLE_MANAGER', 'ROLE_DOCTOR')")
    public ResponseEntity<Response<Page<UserPatientResponse>>> getAllPatients(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "") String email
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> patients = userRepository.findAllByRoleNameAndEmailIgnoreCaseContainingAndIsActive(Roles.ROLE_PATIENT, email, true, pageable);
        return ResponseEntity.ok(new Response<>(patients.map(userMapper::toUserPatientResponse), "Fetching patients successfully"));
    }

    @GetMapping("/patient/events")
    @PreAuthorize("hasAuthority('ROLE_PATIENT')")
    public ResponseEntity<Response<PatientEventResponse>> getPatientScheduleAndDrugSchedule(
        @RequestParam(value="from", required = true) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDate from,
        @RequestParam(value="to", required = true) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDate to,
        @RequestParam(defaultValue = "PENDING") List<Schedule.Status> scheduleStatus) {
        UUID patientId = currentUserUtils.getCurrentUserId();
        List<PatientDrug> patientDrugs = patientDrugService.getPatientDrugsByPatientIdAndDateBetween(patientId, from, to);
        List<Schedule> schedules = scheduleService.getSchedulesByPatientId(currentUserUtils.getCurrentUser().getId(),scheduleStatus,from,to);

        List<TreatmentPatientDrugResponse> treatmentPatientDrugResponses = patientDrugs.stream()
                .map(treatmentMapper::map)
                .collect(Collectors.toList());
        
        List<DoctorScheduleReponse> doctorScheduleResponses = schedules.stream()
                .map(scheduleMapper::toDoctorScheduleRespone)
                .collect(Collectors.toList());
        
        PatientEventResponse patientEventResponse = new PatientEventResponse();
        patientEventResponse.setTreatmentPatientDrugResponse(treatmentPatientDrugResponses);
        patientEventResponse.setScheduleResponse(doctorScheduleResponses);

        return ResponseEntity.ok(new Response<>(patientEventResponse, "Fetching patient events successfully"));
    }

    @GetMapping("/manager/events")
    @PreAuthorize("hasAuthority('ROLE_MANAGER')")
    public ResponseEntity<Response<PatientEventResponse>> getPatientScheduleAndDrugScheduleByManager(
        @RequestParam() UUID patientId,
        @RequestParam(value="from", required = true) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDate from,
        @RequestParam(value="to", required = true) @DateTimeFormat(pattern = Constants.DATE_FORMAT) LocalDate to,
        @RequestParam(defaultValue = "PENDING") List<Schedule.Status> scheduleStatus) {
        List<PatientDrug> patientDrugs = patientDrugService.getPatientDrugsByPatientIdAndDateBetween(patientId, from, to);
        List<Schedule> schedules = scheduleService.getSchedulesByPatientId(patientId,scheduleStatus,from,to);

        List<TreatmentPatientDrugResponse> treatmentPatientDrugResponses = patientDrugs.stream()
                .map(treatmentMapper::map)
                .collect(Collectors.toList());
        
        List<DoctorScheduleReponse> doctorScheduleResponses = schedules.stream()
                .map(scheduleMapper::toDoctorScheduleRespone)
                .collect(Collectors.toList());
        
        PatientEventResponse patientEventResponse = new PatientEventResponse();
        patientEventResponse.setTreatmentPatientDrugResponse(treatmentPatientDrugResponses);
        patientEventResponse.setScheduleResponse(doctorScheduleResponses);

        return ResponseEntity.ok(new Response<>(patientEventResponse, "Fetching patient events successfully"));
    }
    
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
