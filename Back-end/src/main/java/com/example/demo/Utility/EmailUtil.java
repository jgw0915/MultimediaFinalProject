package com.example.demo.Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Component
public class EmailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    public String sendResetPasswordEmail(String email) throws MessagingException {
        try {
            // Simulate sending a reset password email
            // Replace this with actual email sending logic
            logger.info("Sending reset password email to: {}", email);
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("reset Password");
            mimeMessageHelper.setText("""
                <div>
                    <a href="http://localhost:8080/resetPassword?email=%s" target="_blank">click link to reset password</a>
                </div>
                """.formatted(email), true);

            javaMailSender.send(mimeMessage);
            // Example email sending logic
            // emailService.send(email, "Reset Password", "Please reset your password using the following link...");
            return "Email sent successfully";
        } catch (Exception e) {
            logger.error("Error sending reset password email to: {}, Error: {}", email, e.getMessage(), e);
            return "Error sending reset password email";
        }
    }
}
