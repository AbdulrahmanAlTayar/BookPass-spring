package com.bookpass.bookpass.dto.request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profilePicture;
    
    // For Bookstore users
    private String storeName;
    private String storeAddress;
}
