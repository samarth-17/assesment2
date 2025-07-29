package com.example.assignment_anulom.services;

import com.example.assignment_anulom.entity.SumEntity;
import com.example.assignment_anulom.model.SumRequest;
import com.example.assignment_anulom.model.SumResponse;
import com.example.assignment_anulom.repository.SumRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sum Services Minimal Tests")
class SumServicesTest {

    @Mock
    private SumRequestRepository sumRequestRepository;

    @InjectMocks
    private SumServices sumServices;

    private SumRequest sumRequest;

    @BeforeEach
    void setUp() {
        sumRequest = new SumRequest();
    }

    @Test
    void shouldCalculateSumSuccessfully() {
        sumRequest.setNumbers(Arrays.asList(1, 2, 3));
        when(sumRequestRepository.findByInputNumbers("1,2,3")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        SumResponse response = sumServices.computeSum(sumRequest);

        assertNotNull(response);
        assertEquals(6, response.getResult());
    }

    @Test
    void shouldThrowExceptionForEmptyArray() {
        sumRequest.setNumbers(Collections.emptyList());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sumServices.computeSum(sumRequest);
        });
        assertEquals("Input number list cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForNullArray() {
        sumRequest.setNumbers(null);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sumServices.computeSum(sumRequest);
        });
        assertEquals("Input number list cannot be empty.", exception.getMessage());
    }

    @Test
    void shouldReturnCachedResult() {
        sumRequest.setNumbers(Arrays.asList(1, 2, 3));
        SumEntity cachedEntity = new SumEntity();
        cachedEntity.setResult(6);
        when(sumRequestRepository.findByInputNumbers("1,2,3")).thenReturn(Optional.of(cachedEntity));

        SumResponse response = sumServices.computeSum(sumRequest);

        assertNotNull(response);
        assertEquals(6, response.getResult());
        verify(sumRequestRepository, never()).save(any(SumEntity.class));
    }
} 