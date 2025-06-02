package com.example.subscriptionapp.service;

import com.example.subscriptionapp.model.Subscription;
import com.example.subscriptionapp.model.User;

import java.util.List;
import java.util.Optional;

public interface SubscriptionService {
    Subscription createSubscription(Subscription subscription);
    Optional<Subscription> getSubscriptionById(Long id);
    List<Subscription> getAllActiveUserSubscriptions(Long userId);
    List<Subscription> getAllArchivedUserSubscriptions(Long userId);
    List<Subscription> getAllSubscriptions();
    Subscription archiveSubscription(Long id);
    Subscription restoreSubscription(Long id);
    Subscription updateSubscription(Long id, Subscription subscription);
    void deleteSubscription(Long id);
}
