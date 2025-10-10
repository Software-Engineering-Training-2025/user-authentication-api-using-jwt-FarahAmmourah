package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.model.RefreshToken;
import com.example.demo.model.dto.LoginRequest;
import com.example.demo.model.dto.SignupRequest;
import com.example.demo.model.dto.TokenResponse;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.RefreshTokenRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final RefreshTokenRepository refreshRepo;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder encoder;

    public AuthService(UserRepository userRepo,
                       RefreshTokenRepository refreshRepo,
                       JwtService jwtService,
                       BCryptPasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.refreshRepo = refreshRepo;
        this.jwtService = jwtService;
        this.encoder = encoder;
    }

    public String signup(SignupRequest request) {
        if (userRepo.findByEmail(request.getEmail()) != null) {
            return "Email already taken!";
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole("USER");
        user.setCreatedAt(LocalDateTime.now());
        userRepo.save(user);
        return "User registered successfully!";
    }

    public TokenResponse login(LoginRequest request) {
        User user = userRepo.findByEmail(request.getEmail());
        if (user == null || !encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        RefreshToken rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setToken(refreshToken);
        rt.setExpiresAt(LocalDateTime.now().plusDays(7));
        rt.setRevoked(false);
        rt.setCreatedAt(LocalDateTime.now());
        refreshRepo.save(rt);

        return new TokenResponse(accessToken, refreshToken);
    }

    public void logout(String refreshToken) {
        RefreshToken rt = refreshRepo.findByToken(refreshToken);
        if (rt != null) {
            rt.setRevoked(true);
            refreshRepo.save(rt);
        }
    }

    public String refresh(String refreshToken) {
        RefreshToken rt = refreshRepo.findByToken(refreshToken);
        if (rt == null || rt.isRevoked() || rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid refresh token");
        }
        return jwtService.generateAccessToken(jwtService.extractEmail(refreshToken));
    }
}
