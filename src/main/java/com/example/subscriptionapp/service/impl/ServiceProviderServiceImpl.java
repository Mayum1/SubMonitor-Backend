package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.model.ServiceProvider;
import com.example.subscriptionapp.repository.ServiceProviderRepository;
import com.example.subscriptionapp.service.ServiceProviderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ServiceProviderServiceImpl implements ServiceProviderService {

    private final ServiceProviderRepository serviceProviderRepository;

    public ServiceProviderServiceImpl(ServiceProviderRepository serviceProviderRepository) {
        this.serviceProviderRepository = serviceProviderRepository;
    }

    @Override
    public ServiceProvider createServiceProvider(ServiceProvider serviceProvider) {
        return serviceProviderRepository.save(serviceProvider);
    }

    @Override
    public Optional<ServiceProvider> getServiceProviderById(Long id) {
        return serviceProviderRepository.findById(id);
    }

    @Override
    public List<ServiceProvider> getAllServiceProviders() {
        return serviceProviderRepository.findAll();
    }

    @Override
    public ServiceProvider updateServiceProvider(Long id, ServiceProvider serviceProvider) {
        ServiceProvider existing = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ServiceProvider not found with id " + id));
        existing.setName(serviceProvider.getName());
        existing.setLogoUrl(serviceProvider.getLogoUrl());
        existing.setWebsiteUrl(serviceProvider.getWebsiteUrl());
        existing.setCategory(serviceProvider.getCategory());
        return serviceProviderRepository.save(existing);
    }

    @Override
    public void deleteServiceProvider(Long id) {
        serviceProviderRepository.deleteById(id);
    }
}
