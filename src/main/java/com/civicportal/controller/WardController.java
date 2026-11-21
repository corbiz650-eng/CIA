package com.civicportal.controller;

import com.civicportal.dto.ward.WardRequest;
import com.civicportal.dto.ward.WardResponse;
import com.civicportal.service.WardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class WardController {
    private final WardService wardService;

    public WardController(WardService wardService) {
        this.wardService = wardService;
    }

    @GetMapping("/wards")
    public ResponseEntity<List<WardResponse>> getAllWards() {
        return ResponseEntity.ok(wardService.getAllWards());
    }

    @PostMapping("/wards")
    public ResponseEntity<WardResponse> createWard(@Valid @RequestBody WardRequest request) {
        WardResponse response = wardService.createWard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/wards/{id}")
    public ResponseEntity<WardResponse> updateWard(@PathVariable("id") Long wardId, @Valid @RequestBody WardRequest request) {
        return ResponseEntity.ok(wardService.updateWard(wardId, request));
    }

}