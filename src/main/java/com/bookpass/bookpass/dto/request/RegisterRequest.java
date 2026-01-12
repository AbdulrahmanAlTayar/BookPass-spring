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
    private String role;

    private String firstName;
    private String lastName;
    private String phoneNumber;

    private String profilePicture;

    // موقع المستخدم (للطلاب والمكاتب)
    private Double latitude;
    private Double longitude;
    private String address; // عنوان نصي

    // معلومات المكتبة (مطلوبة إذا role = BOOKSTORE)
    private String storeName;
    private String storeAddress;
}