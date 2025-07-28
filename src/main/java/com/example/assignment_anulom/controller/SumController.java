package com.example.assignment_anulom.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.assignment_anulom.model.SumRequest;
import com.example.assignment_anulom.model.SumResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.assignment_anulom.services.SumServices;

@RestController
@RequestMapping("/api/sum")
@Tag(name = "Sum Controller", description = "Handles sum calculation of number lists")
public class SumController {

    @Autowired
    private SumServices sumServices;

    @PostMapping
    @Operation(summary = "Calculate sum of a list of integers")
    public ResponseEntity<SumResponse> calculate(@RequestBody SumRequest request) {
        return ResponseEntity.ok(sumServices.computeSum(request));
    }
}
