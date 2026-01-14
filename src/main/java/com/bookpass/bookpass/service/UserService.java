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
                user.getIban(),
                user.getCreatedAt()
        );
    }
    public UserProfileResponse updateProfile(String email, com.bookpass.bookpass.dto.request.UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());

        if (request.getStoreName() != null) user.setStoreName(request.getStoreName());
        if (request.getStoreAddress() != null) user.setStoreAddress(request.getStoreAddress());
        if (request.getIban() != null) user.setIban(request.getIban());

        userRepository.save(user);

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
                user.getIban(),
                user.getCreatedAt()
        );
    }
}