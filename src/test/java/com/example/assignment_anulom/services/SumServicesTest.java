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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Sum Services Tests")
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
    @DisplayName("TC001: Should calculate sum successfully with positive integers")
    void shouldCalculateSumSuccessfully() {
        // Given
        sumRequest.setNumbers(Arrays.asList(1, 2, 3, 4, 5));
        when(sumRequestRepository.findByInputNumbers("1,2,3,4,5")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(15, response.getResult());
        verify(sumRequestRepository).findByInputNumbers("1,2,3,4,5");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("TC002: Should calculate sum with single number")
    void shouldCalculateSumWithSingleNumber() {
        // Given
        sumRequest.setNumbers(Collections.singletonList(42));
        when(sumRequestRepository.findByInputNumbers("42")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(42, response.getResult());
        verify(sumRequestRepository).findByInputNumbers("42");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("TC003: Should calculate sum with large numbers")
    void shouldCalculateSumWithLargeNumbers() {
        // Given
        sumRequest.setNumbers(Arrays.asList(1000000, 2000000, 3000000));
        when(sumRequestRepository.findByInputNumbers("1000000,2000000,3000000")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(6000000, response.getResult());
        verify(sumRequestRepository).findByInputNumbers("1000000,2000000,3000000");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("TC004: Should calculate sum with zero values")
    void shouldCalculateSumWithZeroValues() {
        // Given
        sumRequest.setNumbers(Arrays.asList(0, 0, 0, 5));
        when(sumRequestRepository.findByInputNumbers("0,0,0,5")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(5, response.getResult());
        verify(sumRequestRepository).findByInputNumbers("0,0,0,5");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("TC005: Should calculate sum with negative numbers")
    void shouldCalculateSumWithNegativeNumbers() {
        // Given
        sumRequest.setNumbers(Arrays.asList(-1, -2, 3, 4));
        when(sumRequestRepository.findByInputNumbers("-1,-2,3,4")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(4, response.getResult());
        verify(sumRequestRepository).findByInputNumbers("-1,-2,3,4");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("TC006: Should calculate sum with mixed positive and negative numbers")
    void shouldCalculateSumWithMixedNumbers() {
        // Given
        sumRequest.setNumbers(Arrays.asList(10, -5, 3, -2, 1));
        when(sumRequestRepository.findByInputNumbers("10,-5,3,-2,1")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(7, response.getResult());
        verify(sumRequestRepository).findByInputNumbers("10,-5,3,-2,1");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("TC007: Should return cached result when same input is provided")
    void shouldReturnCachedResult() {
        // Given
        sumRequest.setNumbers(Arrays.asList(1, 2, 3));
        SumEntity cachedEntity = new SumEntity();
        cachedEntity.setResult(6);
        when(sumRequestRepository.findByInputNumbers("1,2,3")).thenReturn(Optional.of(cachedEntity));

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(6, response.getResult());
        verify(sumRequestRepository).findByInputNumbers("1,2,3");
        verify(sumRequestRepository, never()).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("TC008: Should handle maximum integer values")
    void shouldHandleMaximumIntegerValues() {
        // Given
        sumRequest.setNumbers(Arrays.asList(2147483647, 1));
        when(sumRequestRepository.findByInputNumbers("2147483647,1")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(-2147483648, response.getResult()); // Overflow result
        verify(sumRequestRepository).findByInputNumbers("2147483647,1");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("TC101: Should throw exception for empty array")
    void shouldThrowExceptionForEmptyArray() {
        // Given
        sumRequest.setNumbers(Collections.emptyList());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sumServices.computeSum(sumRequest);
        });
        assertEquals("Input number list cannot be empty.", exception.getMessage());
        verify(sumRequestRepository, never()).findByInputNumbers(any());
        verify(sumRequestRepository, never()).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("TC102: Should throw exception for null array")
    void shouldThrowExceptionForNullArray() {
        // Given
        sumRequest.setNumbers(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            sumServices.computeSum(sumRequest);
        });
        assertEquals("Input number list cannot be empty.", exception.getMessage());
        verify(sumRequestRepository, never()).findByInputNumbers(any());
        verify(sumRequestRepository, never()).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("Should handle very large array")
    void shouldHandleVeryLargeArray() {
        // Given
        List<Integer> largeArray = new java.util.ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeArray.add(i);
        }
        sumRequest.setNumbers(largeArray);
        
        StringBuilder expectedInput = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            if (i > 0) expectedInput.append(",");
            expectedInput.append(i);
        }
        
        when(sumRequestRepository.findByInputNumbers(expectedInput.toString())).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(499500, response.getResult()); // Sum of 0 to 999
        verify(sumRequestRepository).findByInputNumbers(expectedInput.toString());
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("Should handle integer overflow correctly")
    void shouldHandleIntegerOverflow() {
        // Given
        sumRequest.setNumbers(Arrays.asList(2147483647, 2147483647, 2147483647));
        when(sumRequestRepository.findByInputNumbers("2147483647,2147483647,2147483647")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(-2147483647, response.getResult()); // Overflow result
        verify(sumRequestRepository).findByInputNumbers("2147483647,2147483647,2147483647");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("Should verify correct entity creation and saving")
    void shouldVerifyCorrectEntityCreationAndSaving() {
        // Given
        sumRequest.setNumbers(Arrays.asList(1, 2, 3));
        when(sumRequestRepository.findByInputNumbers("1,2,3")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenAnswer(invocation -> {
            SumEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(6, response.getResult());
        
        verify(sumRequestRepository).save(argThat(entity -> 
            entity.getInputNumbers().equals("1,2,3") && 
            entity.getResult() == 6
        ));
    }

    @Test
    @DisplayName("Should handle edge case with single zero")
    void shouldHandleSingleZero() {
        // Given
        sumRequest.setNumbers(Collections.singletonList(0));
        when(sumRequestRepository.findByInputNumbers("0")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(0, response.getResult());
        verify(sumRequestRepository).findByInputNumbers("0");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }

    @Test
    @DisplayName("Should handle edge case with all negative numbers")
    void shouldHandleAllNegativeNumbers() {
        // Given
        sumRequest.setNumbers(Arrays.asList(-1, -2, -3, -4, -5));
        when(sumRequestRepository.findByInputNumbers("-1,-2,-3,-4,-5")).thenReturn(Optional.empty());
        when(sumRequestRepository.save(any(SumEntity.class))).thenReturn(new SumEntity());

        // When
        SumResponse response = sumServices.computeSum(sumRequest);

        // Then
        assertNotNull(response);
        assertEquals(-15, response.getResult());
        verify(sumRequestRepository).findByInputNumbers("-1,-2,-3,-4,-5");
        verify(sumRequestRepository).save(any(SumEntity.class));
    }
} 