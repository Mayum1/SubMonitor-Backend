package com.example.subscriptionapp.service.impl;

import com.example.subscriptionapp.model.ReminderDefault;
import com.example.subscriptionapp.model.User;
import com.example.subscriptionapp.repository.ReminderDefaultRepository;
import com.example.subscriptionapp.service.ReminderDefaultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReminderDefaultServiceImpl implements ReminderDefaultService {

    private final ReminderDefaultRepository reminderDefaultRepository;

    public ReminderDefaultServiceImpl(ReminderDefaultRepository reminderDefaultRepository) {
        this.reminderDefaultRepository = reminderDefaultRepository;
    }

    @Override
    public ReminderDefault createReminderDefault(ReminderDefault reminderDefault) {
        return reminderDefaultRepository.save(reminderDefault);
    }

    @Override
    public Optional<ReminderDefault> getReminderDefaultById(Long id) {
        return reminderDefaultRepository.findById(id);
    }

    @Override
    public List<ReminderDefault> getReminderDefaultsByUser(User user) {
        return reminderDefaultRepository.findByUser(user);
    }

    @Override
    public ReminderDefault updateReminderDefault(Long id, ReminderDefault updatedReminder) {
        ReminderDefault existing = reminderDefaultRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder default not found with id " + id));
        existing.setDaysBefore(updatedReminder.getDaysBefore());
        existing.setTimeOfDay(updatedReminder.getTimeOfDay());
        existing.setIsEnabled(updatedReminder.getIsEnabled());
        return reminderDefaultRepository.save(existing);
    }

    @Override
    public void deleteReminderDefault(Long id) {
        reminderDefaultRepository.deleteById(id);
    }
}
