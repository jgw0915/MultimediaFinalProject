package com.example.demo.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import com.example.demo.Model.User;
import com.example.demo.Service.LoginService;

@RestController
@RequestMapping("/api")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String userEmail, @RequestParam String password) {
        logger.info("Login request for email: {}", userEmail);

        try {
            // Check if user exists
            User user = loginService.getUserByEmail(userEmail);
            if (user == null) {
                logger.warn("User not found for email: {}", userEmail);
                return ResponseEntity.status(404).body("User not found");
            }

            // Validate password (assumes password is hashed)
            if (!isPasswordValid(password, user.getPassword())) {
                logger.warn("Invalid password for email: {}", userEmail);
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            logger.info("User authenticated successfully: {}", userEmail);
            return ResponseEntity.ok("Login successful");

        } catch (Exception e) {
            logger.error("Error during login for email: {}, Error: {}", userEmail, e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestParam String nickname, @RequestParam String password, @RequestParam String email) {
        User user = loginService.getUserByEmail(email);
        if (user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email already exists.");
        } else {
            loginService.saveUser(nickname, password, email);
            return ResponseEntity.status(HttpStatus.OK).body("Register success.");
        }
    }

    // Simulate password validation (use hashing like BCrypt in a real-world app)
    private boolean isPasswordValid(String rawPassword, String hashedPassword) {
        // Implement hashing logic or use a library like BCrypt
        return rawPassword.equals(hashedPassword); // Replace with BCrypt validation
    }
}
