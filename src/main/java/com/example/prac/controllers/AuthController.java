package com.example.prac.controllers;

import com.example.prac.model.authEntity.AuthenticationRequest;
import com.example.prac.model.authEntity.AuthenticationResponse;
import com.example.prac.model.authEntity.AuthenticationService;
import com.example.prac.model.authEntity.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authenticationService.register(request));
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
    @GetMapping("/verify-token")
    public ResponseEntity<?> checkToken() {
        return ResponseEntity.ok("Token is valid");
    }

}
