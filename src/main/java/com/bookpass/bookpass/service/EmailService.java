package com.bookpass.bookpass.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendPasswordResetEmail(String toEmail, String resetLink) {
        // Initialize Resend with API Key
        Resend resend = new Resend(resendApiKey);

        String htmlContent = "<p>You requested a password reset for BookPass.</p>" +
                "<p>Click the link below to reset your password:</p>" +
                "<a href=\"" + resetLink + "\">Reset Password</a>" +
                "<p>If you did not request this, please ignore this email.</p>";

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("BookPass <onboarding@resend.dev>") // Using default Resend test domain or verified domain
                .to(toEmail)
                .subject("BookPass Password Reset")
                .html(htmlContent)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            log.info("Password reset email sent to {}", toEmail);
        } catch (ResendException e) {
            log.error("Failed to send email: {}", e.getMessage());
            throw new RuntimeException("Failed to send email");
        }
    }
}
