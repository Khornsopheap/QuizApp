package com.example.QuizApp;

import com.example.QuizApp.model.LoginResponse;
import com.example.QuizApp.model.QuizSubmission;
import com.example.QuizApp.model.ResultResponse;
import com.example.QuizApp.model.User;
import com.example.QuizApp.service.QuizService;
import com.example.QuizApp.service.UserService;
import com.example.QuizApp.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody User user) {
        User dbUser = userService.findByUsername(user.getUsername());
        log.info("username: "+user.getUsername());
        log.info("password: "+user.getPassword());
        if (dbUser != null && dbUser.getPassword().equals(user.getPassword())) {
            String token = JwtUtil.generateToken(user.getUsername());
            log.info("token: "+token);

            return ResponseEntity.ok(new LoginResponse(token, "Login successful"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(null, "Invalid credentials"));
    }

}
