package com.saas.backend.controller;

import com.saas.backend.entity.User;
import com.saas.backend.service.UserService;
import com.saas.backend.tenant.TenantContext;
import com.saas.backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // 🔒 ADMIN only
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<User> getUsers() {
        System.out.println("✅ INSIDE getUsers()"); // remove after testing
        return userService.getAllUsers();
    }

    // 🔒 ADMIN only
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // 🔓 ADMIN + USER
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    public User getMyProfile() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        String tenantId = TenantContext.getTenant();
        return userRepository
                .findByUsernameAndTenantId(username, tenantId)
                .orElseThrow();
    }

    // 🔍 DEBUG — remove after testing
    @GetMapping("/debug")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String debugAuth() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return "User: " + auth.getName() + " | Authorities: " + auth.getAuthorities();
    }
}