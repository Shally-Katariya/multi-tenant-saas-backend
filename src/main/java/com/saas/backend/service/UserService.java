package com.saas.backend.service;

import com.saas.backend.entity.User;
import com.saas.backend.repository.UserRepository;
import com.saas.backend.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) {

        // 🔥 AUTO SET TENANT
        user.setTenantId(TenantContext.getTenant());

        return userRepository.save(user);
    }
}