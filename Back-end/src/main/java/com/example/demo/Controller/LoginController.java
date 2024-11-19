package com.example.demo.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            if (!loginService.isPasswordValid(password, user.getPassword())) {
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
        try {
            User user = loginService.getUserByEmail(email);
            if (user != null) {
                logger.warn("Email already exists: {}", email);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Email already exists.");
            } else {
                loginService.saveUser(nickname, password, email);
                logger.info("User registered successfully: {}", email);
                return ResponseEntity.status(HttpStatus.OK).body("Register success.");
            }
        } catch (Exception e) {
            logger.error("Error during registration for email: {}, Error: {}", email, e.getMessage(), e);
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    @PostMapping("/forgetPassword")
    public ResponseEntity<String> forgetPassword(@RequestParam String email) {
        logger.info("Forget password request for email: {}", email);

        try {
            // Check if user exists
            User user = loginService.getUserByEmail(email);
            if (user == null) {
                logger.warn("User not found for email: {}", email);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Email not found.");
            }

            // Simulate sending a reset password email
            loginService.sendResetPasswordEmail(email);

            logger.info("Reset password email sent to: {}", email);
            return ResponseEntity.ok("Reset password email sent successfully.");
        } catch (Exception e) {
            logger.error("Error during forgetPassword process for email: {}, Error: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error.");
        }
    }


}
