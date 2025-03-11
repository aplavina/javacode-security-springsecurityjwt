package com.aplavina.springsecurityjwt.service;

import com.aplavina.springsecurityjwt.entity.AuthLog;
import com.aplavina.springsecurityjwt.repository.AuthLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthLoggingService {
    private final AuthLogRepository authLogRepository;

    public void logEvent(String username, String description) {
        AuthLog log = new AuthLog();
        log.setUsername(username);
        log.setDescription(description);
        log.setTime(LocalDateTime.now());
        authLogRepository.save(log);
    }
}