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
}
