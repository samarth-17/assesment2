package com.example.assignment_anulom.services;

import com.example.assignment_anulom.entity.SumEntity;
import com.example.assignment_anulom.model.SumRequest;
import com.example.assignment_anulom.model.SumResponse;
import com.example.assignment_anulom.repository.SumRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SumServices {

    @Autowired
    private SumRequestRepository sumRequestRepository;

    public SumResponse computeSum(SumRequest request) {
        List<Integer> numbers = request.getNumbers();

        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("Input number list cannot be empty.");
        }

        String inputAsString = numbers.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Check if result already exists
        Optional<SumEntity> existing = sumRequestRepository.findByInputNumbers(inputAsString);
        if (existing.isPresent()) {
            return new SumResponse(existing.get().getResult());
        }

        // Compute and save
        int sum = numbers.stream().mapToInt(Integer::intValue).sum();

        SumEntity entity = new SumEntity();
        entity.setInputNumbers(inputAsString);
        entity.setResult(sum);

        sumRequestRepository.save(entity);

        return new SumResponse(sum);
    }
}
