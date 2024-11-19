import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtil {

    // Function to send email
    public static String sendEmail(String userEmail) {
        // Your email and password
        private final String senderEmail = "multimediafinalproject03@gmail.com"; // Replace with your email
        private final String senderPassword = "!!abcd1234"; // Replace with your email password
        
        // Frontend reset password page link
        private String resetPasswordLink = "http://localhost:8080/resetPassword";

        // Email content
        private String subject = "Reset Your Password";
        private String messageContent = "Click the link below to reset your password:\n" + resetPasswordLink;

        // Setting up properties for the mail session
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Use your email's SMTP server
        properties.put("mail.smtp.port", "587"); // SMTP port
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session with an authenticator
        private Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            // Create a MIME-style message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));
            message.setSubject(subject);
            message.setText(messageContent);

            // Send the email
            Transport.send(message);
            return "Email sent successfully to " + userEmail;

        } catch (MessagingException e) {
            e.printStackTrace();
            return "Failed to send email: " + e.getMessage();
        }
    }
}
