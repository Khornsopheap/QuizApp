package com.example.QuizApp.controller;

import com.example.QuizApp.model.LoginResponse;
import com.example.QuizApp.model.User;
import com.example.QuizApp.service.UserService;
import com.example.QuizApp.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody User user) {
        User dbUser = userService.findByUsername(user.getUsername());
        log.info("username: " + user.getUsername());

        // ✅ Use BCrypt check instead of plain string compare
        if (dbUser != null && userService.checkPassword(user.getPassword(), dbUser.getPassword())) {
            String token = JwtUtil.generateToken(dbUser.getUsername(), dbUser.getRole());
            log.info("token: " + token);
            return ResponseEntity.ok(new LoginResponse(token, "Login successful"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(null, "Invalid credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        // Check username already exists
        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Username already exists");
        }

        // Default role to USER if not provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }

        // saveUser hashes the password inside UserService
        userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User registered successfully");
    }
}