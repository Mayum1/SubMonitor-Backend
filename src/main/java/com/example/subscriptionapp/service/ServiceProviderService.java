package com.example.subscriptionapp.service;

import com.example.subscriptionapp.model.ServiceProvider;

import java.util.List;
import java.util.Optional;

public interface ServiceProviderService {
    ServiceProvider createServiceProvider(ServiceProvider serviceProvider);
    Optional<ServiceProvider> getServiceProviderById(Long id);
    List<ServiceProvider> getAllServiceProviders();
    ServiceProvider updateServiceProvider(Long id, ServiceProvider serviceProvider);
    void deleteServiceProvider(Long id);
}
