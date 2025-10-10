package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.model.dto.*;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepo;
    private final JwtService jwtService;

    public AuthController(AuthService authService, UserRepository userRepo, JwtService jwtService) {
        this.authService = authService;
        this.userRepo = userRepo;
        this.jwtService = jwtService;
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        return authService.signup(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String refreshToken) {
        authService.logout(refreshToken);
        return "Logged out!";
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestParam String refreshToken) {
        String newAccess = authService.refresh(refreshToken);
        return new TokenResponse(newAccess, refreshToken);
    }

    @GetMapping("/me")
    public UserInfoResponse me(Authentication auth) {
        String email = auth.getName();
        User user = userRepo.findByEmail(email);
        return new UserInfoResponse(user.getUsername(), user.getEmail(), user.getRole());
    }
}
