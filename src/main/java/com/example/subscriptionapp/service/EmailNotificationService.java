package com.example.subscriptionapp.service;
 
public interface EmailNotificationService {
    void sendEmail(String to, String subject, String body);
} 