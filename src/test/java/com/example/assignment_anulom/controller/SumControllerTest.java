package com.example.assignment_anulom.controller;

import com.example.assignment_anulom.model.SumRequest;
import com.example.assignment_anulom.model.SumResponse;
import com.example.assignment_anulom.services.SumServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sum Controller Tests")
class SumControllerTest {


    @Mock
    private SumServices sumServices;

    @InjectMocks
    private SumController sumController;


    private MockMvc mockMvc;
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        // Set up the controller for testing
        mockMvc = MockMvcBuilders.standaloneSetup(sumController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldCalculateSumSuccessfully() throws Exception {

        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3));

        SumResponse expectedResponse = new SumResponse(6);


        when(sumServices.computeSum(any(SumRequest.class))).thenReturn(expectedResponse);


        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                // Check that the response status is 200 OK
                .andExpect(status().isOk())
                // Check that the result in the response is 6
                .andExpect(jsonPath("$.result").value(6));
    }

    @Test
    void shouldHandleEmptyArrayError() throws Exception {
        when(sumServices.computeSum(any(SumRequest.class)))
                .thenThrow(new IllegalArgumentException("Input number list cannot be empty."));

        SumRequest request = new SumRequest();
        request.setNumbers(Collections.emptyList());

        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldHandleNullArrayError() throws Exception {
        when(sumServices.computeSum(any(SumRequest.class)))
                .thenThrow(new IllegalArgumentException("Input number list cannot be empty."));


        SumRequest request = new SumRequest();
        request.setNumbers(null);

        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldHandleInvalidJsonFormat() throws Exception {
        String invalidJson = "{\"numbers\": [1, 2, 3}";
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
} 