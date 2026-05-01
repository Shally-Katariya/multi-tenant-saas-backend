package com.saas.backend.controller;

import com.saas.backend.entity.User;
import com.saas.backend.repository.UserRepository;
import com.saas.backend.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> request) {

        // 🔹 Step 1: Extract request data safely
        String username = request.get("username");
        String tenantId = request.get("tenantId");
        String password = request.get("password");

        if (username == null || tenantId == null || password == null) {
            throw new RuntimeException("Missing required fields");
        }

        // 🔹 Step 2: Fetch user from DB
        Optional<User> userOpt = userRepository.findByUsernameAndTenantId(username, tenantId);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        // 🔹 Step 3: Validate password
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid password");
        }

        // 🔹 Step 4: Validate role (🔥 IMPORTANT FIX)
        String role = user.getRole();
        if (role == null || role.isEmpty()) {
            throw new RuntimeException("User role not set. Please fix user data.");
        }

        // 🔹 Step 5: Generate JWT
        String token = jwtUtil.generateToken(username, tenantId, role);

        // 🔹 Step 6: Return token
        return Map.of("token", token);
    }
}