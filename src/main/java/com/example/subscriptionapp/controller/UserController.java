package com.example.subscriptionapp.controller;

import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.service.UserService;
import com.example.subscriptionapp.dto.SettingsDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Operations pertaining to users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Создать нового пользователя")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    @Operation(summary = "Получить пользователя по id")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Получить всех пользователей")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Обновить данные пользователя")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(summary = "Обновить настройки пользователя")
    @PutMapping("/{id}/settings")
    public ResponseEntity<User> updateUserSettings(@PathVariable Long id, @RequestBody SettingsDTO settings) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
        if (settings.getDefaultCurrency() != null) user.setDefaultCurrency(settings.getDefaultCurrency());
        if (settings.getDefaultTimezone() != null) user.setDefaultTimezone(settings.getDefaultTimezone());
        // Add more fields as needed
        User updated = userService.createUser(user); // or userService.updateUser(id, user) if you want to reuse logic
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Удалить пользователя")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
