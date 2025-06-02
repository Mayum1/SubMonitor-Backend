package com.example.subscriptionapp.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ServiceProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    // URL логотипа сервиса
    @Column(name = "logo_url")
    private String logoUrl;

    // URL сайта провайдера подписки
    @Column(name = "website_url")
    private String websiteUrl;

    // Категория подписки
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private SubscriptionCategory category;
}
