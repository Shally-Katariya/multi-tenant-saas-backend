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

    // 🔒 ONLY ADMIN can create users
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // 🔓 ADMIN 
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public User getMyProfile() {

    String username = SecurityContextHolder.getContext()
                        .getAuthentication().getName();

    String tenantId = TenantContext.getTenant();  // 🔥 IMPORTANT

    return userRepository
            .findByUsernameAndTenantId(username, tenantId)
            .orElseThrow();
}
}