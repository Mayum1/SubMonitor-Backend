package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.repository.UserRepository;
import com.example.subscriptionapp.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        user.setEmail(userDetails.getEmail());
        // При обновлении можно условно обновлять пароль (если он изменился)
        if (!userDetails.getPasswordHash().equals(user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
        }
        user.setDefaultCurrency(userDetails.getDefaultCurrency());
        user.setDefaultTimezone(userDetails.getDefaultTimezone());
        user.setIsTelegramLinked(userDetails.getIsTelegramLinked());
        user.setDefaultRemindersEnabled(userDetails.getDefaultRemindersEnabled());
        user.setRole(userDetails.getRole());
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }
}
