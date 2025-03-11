package com.aplavina.springsecurityjwt.service;

import com.aplavina.springsecurityjwt.dto.ReqRes;
import com.aplavina.springsecurityjwt.entity.User;
import com.aplavina.springsecurityjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthLoggingService authLoggingService;
    @Value("${tokens.exp-time-hours}")
    private String expTimeHours;

    public ReqRes signUp(ReqRes registrationRequest) {
        ReqRes resp = new ReqRes();
        try {
            User user = new User();
            user.setEmail(registrationRequest.getEmail());
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            user.setRole(registrationRequest.getRole());
            User newUser = userRepository.save(user);
            if (newUser.getId() > 0) {
                resp.setUser(newUser);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
                authLoggingService.logEvent(user.getEmail(), "registered");
            }
        } catch (Exception e) {
            log.error("Error when sign up", e);
            resp.setStatusCode(500);
            resp.setError("Server error");
        }
        return resp;
    }

    @Transactional
    public ReqRes signIn(ReqRes singInRequest) {
        ReqRes response = new ReqRes();
        User user = userRepository.findByEmail(singInRequest.getEmail()).orElseThrow();
        if (!user.isAccountNonLocked()) {
            response.setStatusCode(400);
            response.setError("Account is locked");
            return response;
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(singInRequest.getEmail(), singInRequest.getPassword())
            );
            String jwt = jwtService.generateToken(user);
            String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshToken);
            response.setExpirationTime(expTimeHours);
            response.setMessage("Successfully Signed In");
            authLoggingService.logEvent(user.getEmail(), "logged in");
        } catch (AuthenticationException e) {
            authLoggingService.logEvent(user.getEmail(), "filed to log in");
            user.setLogInTries(user.getLogInTries() + 1);
            if (user.getLogInTries() > 5) {
                user.setAccountNonLocked(false);
            }
        } catch (Exception e) {
            log.error("Error when sign in", e);
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }

    public ReqRes refreshToken(ReqRes refreshTokenRequest) {
        ReqRes response = new ReqRes();
        String email = jwtService.extractUsername(refreshTokenRequest.getToken());
        User user = userRepository.findByEmail(email).orElseThrow();
        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            String jwt = jwtService.generateToken(user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRefreshToken(refreshTokenRequest.getToken());
            response.setExpirationTime(expTimeHours);
            response.setMessage("Successfully Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }

    @Transactional
    public void unlockAccount(long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setAccountNonLocked(true);
    }
}
