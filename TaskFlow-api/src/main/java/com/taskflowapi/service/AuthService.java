package com.taskflowapi.service;

import com.taskflowapi.dto.LoginRequest;
import com.taskflowapi.dto.LoginResponse;
import com.taskflowapi.entity.User;
import com.taskflowapi.exception.UnauthorizedException;
import com.taskflowapi.repository.UserRepository;
import com.taskflowapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return new LoginResponse(token, user.getEmail(), user.getName());
    }
}
