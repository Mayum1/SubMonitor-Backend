package com.example.subscriptionapp.repository;

import com.example.subscriptionapp.model.TelegramCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramCodeRepository extends JpaRepository<TelegramCode, Long> {
    Optional<TelegramCode> findByCode(String code);
}
