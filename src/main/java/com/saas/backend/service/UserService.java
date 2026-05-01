package com.saas.backend.service;

import com.saas.backend.entity.User;
import com.saas.backend.repository.UserRepository;
import com.saas.backend.tenant.TenantContext;
import com.saas.backend.webhook.WebhookService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebhookService webhookService;

    // 🔥 Fetch users (cache removed until Redis is wired)
    @Cacheable(value = "users")
    public List<User> getAllUsers() {
        System.out.println("🔥 Fetching from DB...");
        return userRepository.findAll();
    }

    // 🔥 Create user
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {

        // 🔹 Set tenant from JWT context
        user.setTenantId(TenantContext.getTenant());

        // 🔹 Default role if missing
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        // 🔹 Save user
        User savedUser = userRepository.save(user);

        // 🔹 Trigger webhook async
        webhookService.triggerWebhook(
                "https://webhook.site/76c8d117-c072-426e-9b06-8b5dce6d8bf7",
                "User created: " + savedUser.getUsername()
        );

        return savedUser;
    }
}