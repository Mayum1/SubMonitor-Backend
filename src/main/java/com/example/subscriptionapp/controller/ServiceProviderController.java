package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.model.ServiceProvider;
import com.example.subscriptionapp.service.ServiceProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service-providers")
@Tag(name = "Service Providers", description = "API для управления провайдерами подписок")
public class ServiceProviderController {

    private final ServiceProviderService serviceProviderService;

    public ServiceProviderController(ServiceProviderService serviceProviderService) {
        this.serviceProviderService = serviceProviderService;
    }

    @Operation(summary = "Создать нового провайдера подписок")
    @PostMapping
    public ResponseEntity<ServiceProvider> createServiceProvider(@RequestBody ServiceProvider serviceProvider) {
        ServiceProvider created = serviceProviderService.createServiceProvider(serviceProvider);
        return ResponseEntity.ok(created);
    }

    @Operation(summary = "Получить провайдера подписок по ID")
    @GetMapping("/{id}")
    public ResponseEntity<ServiceProvider> getServiceProviderById(@PathVariable Long id) {
        ServiceProvider provider = serviceProviderService.getServiceProviderById(id)
                .orElseThrow(() -> new RuntimeException("ServiceProvider not found with id " + id));
        return ResponseEntity.ok(provider);
    }

    @Operation(summary = "Получить всех провайдеров подписок")
    @GetMapping
    public ResponseEntity<List<ServiceProvider>> getAllServiceProviders() {
        List<ServiceProvider> providers = serviceProviderService.getAllServiceProviders();
        return ResponseEntity.ok(providers);
    }

    @Operation(summary = "Обновить данные провайдера подписок")
    @PutMapping("/{id}")
    public ResponseEntity<ServiceProvider> updateServiceProvider(@PathVariable Long id,
                                                                 @RequestBody ServiceProvider serviceProvider) {
        ServiceProvider updated = serviceProviderService.updateServiceProvider(id, serviceProvider);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить провайдера подписок")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServiceProvider(@PathVariable Long id) {
        serviceProviderService.deleteServiceProvider(id);
        return ResponseEntity.noContent().build();
    }
}
