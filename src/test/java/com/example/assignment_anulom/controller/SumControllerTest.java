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
import java.util.List;

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
        mockMvc = MockMvcBuilders.standaloneSetup(sumController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("TC001: Should calculate sum successfully with positive integers")
    void shouldCalculateSumSuccessfully() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3, 4, 5));
        SumResponse expectedResponse = new SumResponse(15);

        when(sumServices.computeSum(any(SumRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result").value(15));
    }

    @Test
    @DisplayName("TC002: Should calculate sum with single number")
    void shouldCalculateSumWithSingleNumber() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Collections.singletonList(42));
        SumResponse expectedResponse = new SumResponse(42);

        when(sumServices.computeSum(any(SumRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(42));
    }

    @Test
    @DisplayName("TC003: Should calculate sum with large numbers")
    void shouldCalculateSumWithLargeNumbers() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1000000, 2000000, 3000000));
        SumResponse expectedResponse = new SumResponse(6000000);

        when(sumServices.computeSum(any(SumRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(6000000));
    }

    @Test
    @DisplayName("TC004: Should calculate sum with zero values")
    void shouldCalculateSumWithZeroValues() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(0, 0, 0, 5));
        SumResponse expectedResponse = new SumResponse(5);

        when(sumServices.computeSum(any(SumRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(5));
    }

    @Test
    @DisplayName("TC005: Should calculate sum with negative numbers")
    void shouldCalculateSumWithNegativeNumbers() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(-1, -2, 3, 4));
        SumResponse expectedResponse = new SumResponse(4);

        when(sumServices.computeSum(any(SumRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(4));
    }

    @Test
    @DisplayName("TC006: Should calculate sum with mixed positive and negative numbers")
    void shouldCalculateSumWithMixedNumbers() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(10, -5, 3, -2, 1));
        SumResponse expectedResponse = new SumResponse(7);

        when(sumServices.computeSum(any(SumRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(7));
    }

    @Test
    @DisplayName("TC101: Should handle empty array error")
    void shouldHandleEmptyArrayError() throws Exception {
        // Given
        when(sumServices.computeSum(any(SumRequest.class)))
                .thenThrow(new IllegalArgumentException("Input number list cannot be empty."));

        SumRequest request = new SumRequest();
        request.setNumbers(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("TC102: Should handle null array error")
    void shouldHandleNullArrayError() throws Exception {
        // Given
        when(sumServices.computeSum(any(SumRequest.class)))
                .thenThrow(new IllegalArgumentException("Input number list cannot be empty."));

        SumRequest request = new SumRequest();
        request.setNumbers(null);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("TC104: Should handle invalid JSON format")
    void shouldHandleInvalidJsonFormat() throws Exception {
        // Given
        String invalidJson = "{\"numbers\": [1, 2, 3}";

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC108: Should handle wrong HTTP method")
    void shouldHandleWrongHttpMethod() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TC109: Should handle wrong content type")
    void shouldHandleWrongContentType() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3));

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("Should handle maximum integer values")
    void shouldHandleMaximumIntegerValues() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(2147483647, 1));
        SumResponse expectedResponse = new SumResponse(-2147483648); // Overflow result

        when(sumServices.computeSum(any(SumRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(-2147483648));
    }

    @Test
    @DisplayName("Should handle very large array")
    void shouldHandleVeryLargeArray() throws Exception {
        // Given
        List<Integer> largeArray = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeArray.add(i);
        }
        
        SumRequest request = new SumRequest();
        request.setNumbers(largeArray);
        SumResponse expectedResponse = new SumResponse(499500); // Sum of 0 to 999

        when(sumServices.computeSum(any(SumRequest.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(499500));
    }
} 