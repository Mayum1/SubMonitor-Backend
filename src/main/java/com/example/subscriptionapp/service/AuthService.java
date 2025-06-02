package com.example.subscriptionapp.service;

import com.example.subscriptionapp.dto.LoginRequest;
import com.example.subscriptionapp.dto.LoginResponse;
import com.example.subscriptionapp.dto.RegisterRequest;
 
public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse register(RegisterRequest registerRequest);
} 