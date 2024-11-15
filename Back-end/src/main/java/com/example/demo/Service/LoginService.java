package com.example.demo.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;

@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    private final UserRepository userRepository;

    @Autowired
    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email); // Query on the "_email" field
    }

    public void saveUser(String nickname, String password, String email) {
        logger.info("Saving user: {}, email: {}, password: {}", nickname, email, password);

        User user = new User();
        user.setNickname(nickname);
        user.setPassword(password); // Hash the password in production
        user.setEmail(email);
        userRepository.save(user);

        logger.info("User saved successfully.");
    }
}
