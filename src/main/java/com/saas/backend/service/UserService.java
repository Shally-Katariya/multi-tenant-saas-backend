package com.saas.backend.service;

import com.saas.backend.entity.User;
import com.saas.backend.repository.UserRepository;
import com.saas.backend.tenant.TenantContext;
import com.saas.backend.webhook.WebhookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebhookService webhookService;

    public User createUser(User user) {

        // 🔥 Step 1: Set tenant automatically
        user.setTenantId(TenantContext.getTenant());

        // 🔥 Step 2: Ensure role is never null (SECURITY FIX)
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // default role
        }

        // 🔥 Step 3: Save user FIRST
        User savedUser = userRepository.save(user);

        // 🔥 Step 4: Trigger webhook AFTER successful save
        webhookService.triggerWebhook(
                "https://webhook.site/76c8d117-c072-426e-9b06-8b5dce6d8bf7",
                "User created: " + savedUser.getUsername()
        );

        return savedUser;
    }
}