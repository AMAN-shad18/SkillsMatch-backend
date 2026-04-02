package com.skillsBridge.skillBridge.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Send email verification link to user
     */
    public void sendVerificationEmail(String toEmail, String token) {
        try {
            String verificationLink = frontendUrl + "/verify-email?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Verify Your SkillBridge Account");
            message.setText(buildVerificationEmailBody(verificationLink));

            mailSender.send(message);
            log.info("Verification email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending verification email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Send password reset link to user
     */
    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            String resetLink = frontendUrl + "/reset-password?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Reset Your SkillBridge Password");
            message.setText(buildPasswordResetEmailBody(resetLink));

            mailSender.send(message);
            log.info("Password reset email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending password reset email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Send password change confirmation email
     */
    public void sendPasswordChangeConfirmationEmail(String toEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Changed Successfully");
            message.setText(buildPasswordChangeConfirmationEmailBody());

            mailSender.send(message);
            log.info("Password change confirmation email sent to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending password change confirmation email to {}: {}", toEmail, e.getMessage());
        }
    }

    /**
     * Build verification email body
     */
    private String buildVerificationEmailBody(String verificationLink) {
        return "Hello,\n\n" +
                "Thank you for registering with SkillBridge! Please verify your email address by clicking the link below:\n\n" +
                verificationLink + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not create this account, please ignore this email.\n\n" +
                "Best regards,\n" +
                "SkillBridge Team";
    }

    /**
     * Build password reset email body
     */
    private String buildPasswordResetEmailBody(String resetLink) {
        return "Hello,\n\n" +
                "We received a request to reset your SkillBridge password. Click the link below to reset it:\n\n" +
                resetLink + "\n\n" +
                "This link will expire in 24 hours.\n\n" +
                "If you did not request this, please ignore this email. Your password will remain unchanged.\n\n" +
                "Best regards,\n" +
                "SkillBridge Team";
    }

    /**
     * Build password change confirmation email body
     */
    private String buildPasswordChangeConfirmationEmailBody() {
        return "Hello,\n\n" +
                "Your SkillBridge password has been successfully changed.\n\n" +
                "If you did not make this change, please contact our support team immediately.\n\n" +
                "Best regards,\n" +
                "SkillBridge Team";
    }
}
