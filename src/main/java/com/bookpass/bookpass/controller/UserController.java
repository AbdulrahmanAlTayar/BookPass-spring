package com.bookpass.bookpass.controller;

import com.bookpass.bookpass.dto.response.UserProfileResponse;
import com.bookpass.bookpass.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Principal principal) {
        UserProfileResponse profile = userService.getProfile(principal.getName());
        return ResponseEntity.ok(profile);
    }

    @org.springframework.web.bind.annotation.PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody com.bookpass.bookpass.dto.request.UpdateProfileRequest request,
            Principal principal) {
        return ResponseEntity.ok(userService.updateProfile(principal.getName(), request));
    }
}