package com.fer.student_analytics.controller;

import com.fer.student_analytics.dto.LoginRequestDto;
import com.fer.student_analytics.dto.LoginResponseDto;
import com.fer.student_analytics.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // sve auth rute počinju s /api/auth
@CrossOrigin(origins = "*")

public class AuthController {

    private final AuthService authService; // referenca na service

    public AuthController(AuthService authService) { // konstruktor
        this.authService = authService;
    }

    // POST /api/auth/login, login endpoint
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto response = authService.login(request);
        return ResponseEntity.ok(response); // 200 OK s tokenom
    }
}
