package com.example.demo.repository;

import com.example.demo.entity.BreachAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<BreachAlert, Long> {
    Optional<BreachAlert> findByTokenNumber(String tokenNumber);
}