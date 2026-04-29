package com.saas.backend.controller;

import com.saas.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {

        String username = request.get("username");
        String tenantId = request.get("tenantId");
        String role = "USER"; // for now static

        String token = jwtUtil.generateToken(username, tenantId, role);

        return Map.of("token", token);
    }
}