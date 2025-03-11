package com.aplavina.springsecurityjwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "authlogs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String description;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime time;
}
