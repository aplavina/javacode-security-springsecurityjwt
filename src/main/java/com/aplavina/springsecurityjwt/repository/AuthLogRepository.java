package com.aplavina.springsecurityjwt.repository;

import com.aplavina.springsecurityjwt.entity.AuthLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthLogRepository extends JpaRepository<AuthLog, Long> {
}