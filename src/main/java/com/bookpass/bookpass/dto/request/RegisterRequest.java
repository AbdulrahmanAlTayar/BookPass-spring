package com.bookpass.bookpass.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "CUSTOMER|BOOKSTORE", message = "Role must be CUSTOMER or BOOKSTORE")
    private String role; // CUSTOMER أو BOOKSTORE

    private String firstName;
    private String lastName;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    private String profilePicture;

    // معلومات المكتبة (مطلوبة فقط إذا role = BOOKSTORE)
    private String storeName;
    private String storeAddress;
}