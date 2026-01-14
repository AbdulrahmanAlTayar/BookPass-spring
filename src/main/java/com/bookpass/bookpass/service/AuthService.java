package com.bookpass.bookpass.service;

import com.bookpass.bookpass.dto.request.LoginRequest;
import com.bookpass.bookpass.dto.request.RegisterRequest;
import com.bookpass.bookpass.dto.response.AuthResponse;
import com.bookpass.bookpass.entity.User;
import com.bookpass.bookpass.exception.EmailAlreadyExistsException;
import com.bookpass.bookpass.exception.InvalidCredentialsException;
import com.bookpass.bookpass.repository.UserRepository;
import com.bookpass.bookpass.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    /**
     * Handles user registration
     * @param request RegisterRequest containing user details
     * @return AuthResponse with JWT token and user info
     */
    public AuthResponse register(RegisterRequest request) {
        // Check if email is already registered
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Create new user entity
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encode password for security
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setProfilePicture(request.getProfilePicture());
        user.setRole(request.getRole()); // Role can be CUSTOMER or BOOKSTORE

        // If the user is a bookstore, set store-specific details
        if ("BOOKSTORE".equals(request.getRole())) {
            user.setStoreName(request.getStoreName());
            user.setStoreAddress(request.getStoreAddress());
        }

        // Save user to database
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Return authentication response with token and user info
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getSellerRating() // Seller rating for BOOKSTORE role
        );
    }

    /**
     * Handles user login
     * @param request LoginRequest containing email and password
     * @return AuthResponse with JWT token and user info
     */
    public AuthResponse login(LoginRequest request) {
        // Retrieve user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Return authentication response
        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getSellerRating() // Seller rating for BOOKSTORE role
        );
    }

    /**
     * Handles forgot password request
     */
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        // Generate reset token
        String resetToken = java.util.UUID.randomUUID().toString();
        
        // Save token and expiry (1 hour) to user
        user.setResetToken(resetToken);
        user.setResetTokenExpiry(java.time.LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // Generate reset link (frontend URL)
        // Assuming frontend is running on localhost:5173 or production URL
        // ideally this base URL should be configurable
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;

        // Send email
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);
    }

    /**
     * Handles reset password using token
     */
    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new InvalidCredentialsException("Reset token has expired");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
}
