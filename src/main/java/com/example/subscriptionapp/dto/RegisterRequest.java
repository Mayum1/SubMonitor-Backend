package com.example.subscriptionapp.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RegisterRequest {
    private String email;
    private String password;
} 