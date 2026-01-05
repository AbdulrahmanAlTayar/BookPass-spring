package com.bookpass.bookpass.service;

import com.bookpass.bookpass.dto.response.UserProfileResponse;
import com.bookpass.bookpass.entity.User;
import com.bookpass.bookpass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserProfileResponse(
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getProfilePicture(),
                user.getRole(),
                user.getSellerRating(),
                user.getStoreName(),
                user.getStoreAddress(),
                user.getCreatedAt()
        );
    }
}