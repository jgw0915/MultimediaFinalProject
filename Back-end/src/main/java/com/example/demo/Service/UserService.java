package com.example.demo.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Model.User;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Utility.EmailUtil;
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public EmailUtil emailUtil;

    @Autowired
    public UserService(UserRepository userRepository) {
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

    public String sendResetPasswordEmail(String email) throws Exception {
        // Simulate sending an email (replace with actual email service logic)
        logger.info("Simulating sending reset password email to: {}", email);
        // You can use a real email service like JavaMailSender or third-party APIs such as SendGrid, Amazon SES, etc.
        String msg = emailUtil.sendResetPasswordEmail(email);
        return msg;
    }

    // Simulate password validation (use hashing like BCrypt in a real-world app)
    public boolean isPasswordValid(String rawPassword, String hashedPassword) {
        // Implement hashing logic or use a library like BCrypt
        return rawPassword.equals(hashedPassword); // Replace with BCrypt validation
    }

    public void resetPassword(String email, String newPassword) {
        try {
            // Fetch user by email
            User user = userRepository.findByEmail(email);
    
            if (user == null) {
                logger.warn("User not found for email: {}", email);
                throw new IllegalArgumentException("User not found for the provided email.");
            }
    
            // Update user password
            logger.info("Resetting password for user: {}", email);
    
            // Hash the new password in production (e.g., using BCrypt or similar library)
            user.setPassword(newPassword);
    
            // Save updated user to the database
            userRepository.save(user);
            logger.info("Password reset successfully for user: {}", email);
    
        } catch (IllegalArgumentException e) {
            logger.error("Validation error during password reset for email: {}, Error: {}", email, e.getMessage());
            throw e; // Re-throw the exception to be handled at the controller level
        } catch (Exception e) {
            logger.error("Unexpected error during password reset for email: {}, Error: {}", email, e.getMessage(), e);
            throw new RuntimeException("An error occurred while resetting the password. Please try again later.");
        }
    }

    public void saveProfileImage(String email, MultipartFile image) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        // Save image to a storage service (e.g., S3, local disk) and update `profileImage` field
        String imageUrl = saveToStorage(image); // Implement saveToStorage method
        user.setProfileImage(imageUrl);
        userRepository.save(user);
    }

    public void saveProfileCover(String email, MultipartFile cover) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        // Save cover to a storage service and update `profileCover` field
        String coverUrl = saveToStorage(cover); // Implement saveToStorage method
        user.setProfileCover(coverUrl);
        userRepository.save(user);
    }

    private String saveToStorage(MultipartFile file) {
        // Logic to save the file and return its URL/path
        // Example: Upload to S3, or save to local filesystem
        return "https://example.com/" + file.getOriginalFilename();
    }
    
}
