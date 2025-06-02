package com.example.subscriptionapp.repository;

import com.example.subscriptionapp.model.Subscription;
import com.example.subscriptionapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserIdAndIsArchived(Long userId, boolean isArchived);
}
