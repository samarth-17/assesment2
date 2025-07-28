package com.example.assignment_anulom.performance;

import com.example.assignment_anulom.model.SumRequest;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Sum API Performance Tests")
class SumApiPerformanceTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("TC201: Should respond within acceptable time limit")
    void shouldRespondWithinAcceptableTimeLimit() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));

        // When & Then
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Assert response time is less than 1000ms
        assertTrue(responseTime < 1000, "Response time should be less than 1000ms, but was: " + responseTime + "ms");
        System.out.println("Response time: " + responseTime + "ms");
    }

    @Test
    @DisplayName("TC202: Should handle concurrent requests efficiently")
    void shouldHandleConcurrentRequestsEfficiently() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3));
        int numberOfConcurrentRequests = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfConcurrentRequests);

        // When
        List<CompletableFuture<Long>> futures = new java.util.ArrayList<>();
        
        for (int i = 0; i < numberOfConcurrentRequests; i++) {
            CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    mockMvc.perform(post("/api/sum")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isOk());
                    long endTime = System.currentTimeMillis();
                    return endTime - startTime;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(30, TimeUnit.SECONDS);
        executor.shutdown();

        // Then
        List<Long> responseTimes = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        // Verify all requests completed successfully
        assertEquals(numberOfConcurrentRequests, responseTimes.size());
        
        // Calculate average response time
        double averageResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        // Verify average response time is reasonable
        assertTrue(averageResponseTime < 2000, "Average response time should be less than 2000ms, but was: " + averageResponseTime + "ms");
        
        System.out.println("Average response time for " + numberOfConcurrentRequests + " concurrent requests: " + averageResponseTime + "ms");
        System.out.println("Min response time: " + responseTimes.stream().mapToLong(Long::longValue).min().orElse(0) + "ms");
        System.out.println("Max response time: " + responseTimes.stream().mapToLong(Long::longValue).max().orElse(0) + "ms");
    }

    @Test
    @DisplayName("Should handle large array efficiently")
    void shouldHandleLargeArrayEfficiently() throws Exception {
        // Given
        List<Integer> largeArray = new java.util.ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            largeArray.add(i);
        }
        
        SumRequest request = new SumRequest();
        request.setNumbers(largeArray);

        // When & Then
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Assert response time is reasonable for large array
        assertTrue(responseTime < 5000, "Response time for large array should be less than 5000ms, but was: " + responseTime + "ms");
        System.out.println("Response time for large array (10000 elements): " + responseTime + "ms");
    }

    @Test
    @DisplayName("Should handle repeated requests with caching efficiently")
    void shouldHandleRepeatedRequestsWithCachingEfficiently() throws Exception {
        // Given
        SumRequest request = new SumRequest();
        request.setNumbers(Arrays.asList(1, 2, 3, 4, 5));

        // First request (should be slower due to computation and database save)
        long firstRequestStart = System.currentTimeMillis();
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        long firstRequestTime = System.currentTimeMillis() - firstRequestStart;

        // Second request (should be faster due to caching)
        long secondRequestStart = System.currentTimeMillis();
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        long secondRequestTime = System.currentTimeMillis() - secondRequestStart;

        // Verify second request is faster (caching works)
        assertTrue(secondRequestTime <= firstRequestTime, 
                "Second request should be faster or equal due to caching. First: " + firstRequestTime + "ms, Second: " + secondRequestTime + "ms");
        
        System.out.println("First request time: " + firstRequestTime + "ms");
        System.out.println("Second request time (cached): " + secondRequestTime + "ms");
        System.out.println("Performance improvement: " + (firstRequestTime - secondRequestTime) + "ms");
    }

    @Test
    @DisplayName("Should handle stress test with multiple different requests")
    void shouldHandleStressTestWithMultipleDifferentRequests() throws Exception {
        // Given
        int numberOfRequests = 50;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // When
        List<CompletableFuture<Long>> futures = new java.util.ArrayList<>();
        
        for (int i = 0; i < numberOfRequests; i++) {
            final int requestNumber = i;
            CompletableFuture<Long> future = CompletableFuture.supplyAsync(() -> {
                try {
                    SumRequest request = new SumRequest();
                    request.setNumbers(Arrays.asList(requestNumber, requestNumber + 1, requestNumber + 2));
                    
                    long startTime = System.currentTimeMillis();
                    mockMvc.perform(post("/api/sum")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                            .andExpect(status().isOk());
                    long endTime = System.currentTimeMillis();
                    return endTime - startTime;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all requests to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
        executor.shutdown();

        // Then
        List<Long> responseTimes = futures.stream()
                .map(CompletableFuture::join)
                .toList();

        // Verify all requests completed successfully
        assertEquals(numberOfRequests, responseTimes.size());
        
        // Calculate statistics
        double averageResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        
        long maxResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0);
        
        long minResponseTime = responseTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0);

        // Verify performance metrics
        assertTrue(averageResponseTime < 3000, "Average response time should be less than 3000ms, but was: " + averageResponseTime + "ms");
        assertTrue(maxResponseTime < 10000, "Max response time should be less than 10000ms, but was: " + maxResponseTime + "ms");
        
        System.out.println("Stress test results for " + numberOfRequests + " requests:");
        System.out.println("Average response time: " + averageResponseTime + "ms");
        System.out.println("Min response time: " + minResponseTime + "ms");
        System.out.println("Max response time: " + maxResponseTime + "ms");
    }

    @Test
    @DisplayName("Should handle memory efficiently with large requests")
    void shouldHandleMemoryEfficientlyWithLargeRequests() throws Exception {
        // Given
        List<Integer> veryLargeArray = new java.util.ArrayList<>();
        for (int i = 0; i < 50000; i++) {
            veryLargeArray.add(i % 1000); // Use smaller numbers to avoid overflow
        }
        
        SumRequest request = new SumRequest();
        request.setNumbers(veryLargeArray);

        // Get initial memory usage
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();

        // When
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(post("/api/sum")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;

        // Get final memory usage
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;

        // Then
        assertTrue(responseTime < 10000, "Response time for very large array should be less than 10000ms, but was: " + responseTime + "ms");
        assertTrue(memoryUsed < 100 * 1024 * 1024, "Memory usage should be less than 100MB, but was: " + (memoryUsed / 1024 / 1024) + "MB");
        
        System.out.println("Response time for very large array (50000 elements): " + responseTime + "ms");
        System.out.println("Memory used: " + (memoryUsed / 1024 / 1024) + "MB");
    }
} 