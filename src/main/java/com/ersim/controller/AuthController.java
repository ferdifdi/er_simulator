package com.ersim.controller;

import com.ersim.repository.UserRepository;
import com.ersim.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handles authentication. Returns a signed JWT on successful login.
 *
 * Default credentials seeded at startup:
 *   admin / admin123  (role: ADMIN)
 *   nurse / nurse123  (role: NURSE)
 *   doctor / doc123   (role: DOCTOR)
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return userRepository.findByUsername(req.username())
                .filter(u -> passwordEncoder.matches(req.password(), u.getPassword()))
                .map(u -> ResponseEntity.ok(Map.of(
                        "token", jwtUtil.generateToken(u.getUsername(), u.getRole().name()),
                        "role",  u.getRole().name(),
                        "username", u.getUsername())))
                .orElse(ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }

    record LoginRequest(String username, String password) {}
}
