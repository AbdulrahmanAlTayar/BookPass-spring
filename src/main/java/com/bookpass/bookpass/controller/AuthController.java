package com.bookpass.bookpass.controller;

import com.bookpass.bookpass.dto.request.LoginRequest;
import com.bookpass.bookpass.dto.request.RegisterRequest;
import com.bookpass.bookpass.dto.response.AuthResponse;
import com.bookpass.bookpass.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request, jakarta.servlet.http.HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setTokenCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, jakarta.servlet.http.HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setTokenCookie(response, authResponse.getToken());
        return ResponseEntity.ok(authResponse);
    }

    private void setTokenCookie(jakarta.servlet.http.HttpServletResponse response, String token) {
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to false for local development (HTTP), true for production (HTTPS)
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        // cookie.setAttribute("SameSite", "Strict"); // Servlet 6.0+ support this, keeping simple for now
        
        response.addCookie(cookie);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody java.util.Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        authService.forgotPassword(email);
        return ResponseEntity.ok("Password reset email sent");
    }

    // Unified endpoint for reset requests (email) OR execution (token in body)
    @PostMapping("/reset-password")
    public ResponseEntity<String> handleResetPassword(@RequestBody java.util.Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        String email = request.get("email");

        if (token != null && !token.isEmpty() && newPassword != null && !newPassword.isEmpty()) {
            authService.resetPassword(token, newPassword);
            return ResponseEntity.ok("Password reset successfully");
        } else if (email != null && !email.isEmpty()) {
            authService.forgotPassword(email);
            return ResponseEntity.ok("Password reset email sent");
        } else {
            return ResponseEntity.badRequest().body("Email is required to request link, OR Token and New Password are required to set new password.");
        }
    }

    @PostMapping("/reset-password/{token}")
    public ResponseEntity<String> resetPassword(@PathVariable String token, @RequestBody java.util.Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("New password is required");
        }
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Password reset successfully");
    }
}