package com.example.assignment_anulom.repository;

import com.example.assignment_anulom.entity.SumEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SumRequestRepository extends JpaRepository<SumEntity, Long> {
    Optional<SumEntity> findByInputNumbers(String inputNumbers);
}