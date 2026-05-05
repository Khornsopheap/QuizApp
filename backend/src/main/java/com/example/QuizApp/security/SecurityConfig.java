package com.example.QuizApp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // ── Public endpoints first ──────────────────────────────
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/api/register").permitAll()
                        .requestMatchers("/ws/**").permitAll()

                        // ── ADMIN-only write operations ─────────────────────────
                        .requestMatchers(HttpMethod.POST,   "/api/quizzes").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/quizzes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/quizzes/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST,   "/api/questions").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST,   "/api/questions/quiz/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/questions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/questions/**").hasRole("ADMIN")

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/session/create/**").hasRole("ADMIN")

                        // ── USER + ADMIN read & interact ────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/quizzes").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/quizzes/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/questions").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/questions/**").hasAnyRole("USER", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/quizzes/*/submit").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/session/join/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/session/submit/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,  "/api/session/leaderboard/**").hasAnyRole("USER", "ADMIN")

                        // ── Everything else requires authentication ──────────────
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}