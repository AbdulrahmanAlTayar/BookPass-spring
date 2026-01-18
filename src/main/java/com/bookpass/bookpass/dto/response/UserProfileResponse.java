package com.bookpass.bookpass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private java.util.UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePicture;
    private String role;
    private BigDecimal sellerRating;

    // معلومات المكتبة (إذا role = BOOKSTORE)
    private String storeName;
    private String storeAddress;
    
    // IBAN for payment purposes
    private String iban;

    private LocalDateTime createdAt;
}