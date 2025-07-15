package com.group3.backend.controller;

import com.group3.backend.dto.Response;
import com.group3.backend.dto.response.AssignDrugResponse;
import com.group3.backend.model.AssignDrug.Status;
import com.group3.backend.service.AssignDrugService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/assign-drugs")
@RequiredArgsConstructor
public class AssignDrugController {

    private final AssignDrugService assignDrugService;

    // [GET] /api/assign-drugs/my-assign-drugs
    @GetMapping("/my-assign-drugs")
    public ResponseEntity<Response<Page<AssignDrugResponse>>> getMyAssignedDrugs(
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AssignDrugResponse> data = assignDrugService.getMyAssignedDrugs(status, page, size);
        return ResponseEntity.ok(new Response<>(data, "Lấy danh sách thuốc được chỉ định thành công"));
    }

    // [GET] /api/assign-drugs/manager
    @GetMapping("/manager")
    public ResponseEntity<Response<Page<AssignDrugResponse>>> getAllAssignedDrugs(
            @RequestParam(required = false) Status status,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AssignDrugResponse> data = assignDrugService.getAllAssignedDrugs(status, keyword, page, size);
        return ResponseEntity.ok(new Response<>(data, "Lấy danh sách thuốc được chỉ định thành công"));
    }

    // [POST] /taken/{id}
    @PostMapping("/taken/{id}")
    public ResponseEntity<Response<String>> markAsTaken(@PathVariable UUID id) {
        assignDrugService.markAsTaken(id);
        return ResponseEntity.ok(new Response<>(null, "Cập nhật trạng thái thành TAKEN thành công"));
    }

    // [POST] /cancel/{id}
    @PostMapping("/cancel/{id}")
    public ResponseEntity<Response<String>> cancelAssignDrug(@PathVariable UUID id) {
        assignDrugService.cancelAssignDrug(id);
        return ResponseEntity.ok(new Response<>(null, "Thuốc đã được hủy thành công"));
    }
}
