package com.bookpass.bookpass.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddBookRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;

    private String author;
    private String isbn;
    private String university;
    private String condition; // Book condition: جديد, ممتاز, جيد جداً, جيد, مقبول
    private String bookImages; // JSON array of URLs
}