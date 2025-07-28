package com.example.assignment_anulom.integration;

import com.example.assignment_anulom.model.SumRequest;
import com.example.assignment_anulom.repository.SumRequestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Sum API Integration Tests")
class SumApiIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private SumRequestRepository sumRequestRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        sumRequestRepository.deleteAll(); // Clean up database before each test
    }

    @Test
    @DisplayName("TC001: Should calculate sum successfully with positive integers")
    void shouldCalculateSumSuccessfully() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3, 4, 5));

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

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(7));
    }

    @Test
    @DisplayName("TC007: Should return cached result when same input is provided")
    void shouldReturnCachedResult() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3));

        // When & Then - First request
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(6));

        // Second request with same data should return cached result
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(6));

        // Verify only one record exists in database
        assert sumRequestRepository.count() == 1;
    }

    @Test
    @DisplayName("TC008: Should handle maximum integer values")
    void shouldHandleMaximumIntegerValues() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(2147483647, 1));

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(-2147483648)); // Overflow result
    }

    @Test
    @DisplayName("TC101: Should handle empty array error")
    void shouldHandleEmptyArrayError() throws Exception {
        // Given
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
    @DisplayName("Should handle very large array")
    void shouldHandleVeryLargeArray() throws Exception {
        // Given
        List<Integer> largeArray = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeArray.add(i);
        }
        
        SumRequest request = new SumRequest();
        request.setNumbers(largeArray);

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(499500)); // Sum of 0 to 999
    }

    @Test
    @DisplayName("Should handle integer overflow correctly")
    void shouldHandleIntegerOverflow() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(2147483647, 2147483647, 2147483647));

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(-2147483647)); // Overflow result
    }

    @Test
    @DisplayName("Should handle edge case with single zero")
    void shouldHandleSingleZero() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Collections.singletonList(0));

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(0));
    }

    @Test
    @DisplayName("Should handle edge case with all negative numbers")
    void shouldHandleAllNegativeNumbers() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(-1, -2, -3, -4, -5));

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(-15));
    }

    @Test
    @DisplayName("Should verify database persistence")
    void shouldVerifyDatabasePersistence() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3));

        // When
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(6));

        // Then - Verify data is persisted in database
        assert sumRequestRepository.count() == 1;
        var savedEntity = sumRequestRepository.findByInputNumbers("1,2,3");
        assert savedEntity.isPresent();
        assert savedEntity.get().getResult() == 6;
    }

    @Test
    @DisplayName("Should handle concurrent requests")
    void shouldHandleConcurrentRequests() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3));

        // When & Then - Multiple concurrent requests should all succeed
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/sum")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result").value(6));
        }

        // Verify only one record exists in database (caching works)
        assert sumRequestRepository.count() == 1;
    }

    @Test
    @DisplayName("Should handle missing numbers field")
    void shouldHandleMissingNumbersField() throws Exception {
        // Given
        String requestWithoutNumbers = "{}";

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithoutNumbers))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should handle non-integer values in array")
    void shouldHandleNonIntegerValues() throws Exception {
        // Given
        String requestWithNonIntegers = "{\"numbers\": [1, \"2\", 3.5, 4]}";

        // When & Then
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestWithNonIntegers))
                .andExpect(status().isBadRequest());
    }
} 