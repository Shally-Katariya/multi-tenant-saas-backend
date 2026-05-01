package com.saas.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.saas.backend.repository.UserRepository;
import com.saas.backend.entity.User;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@EnableCaching
public class SaasBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaasBackendApplication.class, args);
    }

    @Bean
    CommandLineRunner init(UserRepository repo) {
        return args -> {

            // 🔥 Admin
            if (repo.findByUsernameAndTenantId("admin", "company1").isEmpty()) {

                repo.save(
                    User.builder()
                        .username("admin")
                        .password("1234")
                        .tenantId("company1")
                        .role("ADMIN")
                        .build()
                );

                System.out.println("🔥 Admin user inserted");
            }

            // 🔥 Normal user
            if (repo.findByUsernameAndTenantId("user1", "company1").isEmpty()) {

                repo.save(
                    User.builder()
                        .username("user1")
                        .password("1234")
                        .tenantId("company1")
                        .role("USER")
                        .build()
                );

                System.out.println("🔥 User1 inserted");
            }
        };
    }
}