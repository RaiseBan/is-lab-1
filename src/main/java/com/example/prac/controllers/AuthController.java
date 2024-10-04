package com.example.prac.controllers;

import com.example.prac.DTO.auth.AuthenticationRequest;
import com.example.prac.DTO.auth.AuthenticationResponse;
import com.example.prac.service.auth.AuthenticationService;
import com.example.prac.DTO.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    // Регистрация пользователя
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
        AuthenticationResponse authenticationResponse = authenticationService.register(request);
        return ResponseEntity.ok(authenticationResponse);
    }

    // Аутентификация (логин)
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateUser(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    // Проверка валидности токена
    @GetMapping("/verify-token")
    public ResponseEntity<?> checkToken() {
        return ResponseEntity.ok("Token is valid");
    }

}
