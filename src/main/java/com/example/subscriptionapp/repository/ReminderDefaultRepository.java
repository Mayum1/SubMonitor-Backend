package com.example.subscriptionapp.repository;

import com.example.subscriptionapp.model.ReminderDefault;
import com.example.subscriptionapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReminderDefaultRepository extends JpaRepository<ReminderDefault, Long> {
    List<ReminderDefault> findByUser(User user);
}
