package com.example.subscriptionapp.config;

import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDefaultUsers() {
        return args -> {
            if (userRepository.findByEmail("admin@example.com").isEmpty()) {
                User admin = new User("admin@example.com", passwordEncoder.encode("admin"));
                userRepository.save(admin);
            }

            if (userRepository.findByEmail("user@example.com").isEmpty()) {
                User user = new User("user@example.com", passwordEncoder.encode("user"));
                userRepository.save(user);
            }
        };
    }
}
