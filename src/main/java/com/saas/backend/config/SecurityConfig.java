package com.saas.backend.config;

import com.saas.backend.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            // 🔒 Disable default auth
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            // 🔥 Stateless JWT
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // 🔥 H2 console
            .headers(headers ->
                headers.frameOptions(frame -> frame.disable())
            )

            .authorizeHttpRequests(auth -> auth
                // 🔓 Public
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // 🔥 IMPORTANT FIX (THIS LINE SOLVES YOUR 403)
                .requestMatchers("/api/users/**").hasAuthority("ROLE_ADMIN")

                // 🔐 All other secured
                .anyRequest().authenticated()
            )

            // 🔥 JWT filter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}