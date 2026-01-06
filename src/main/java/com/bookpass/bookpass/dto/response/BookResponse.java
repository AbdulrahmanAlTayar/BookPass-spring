package com.bookpass.bookpass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponse {
    private UUID bookId;
    private String title;
    private String description;
    private BigDecimal price;
    private String status;
    private String author;
    private String isbn;
    private String university;
    private String bookImages;
    private String bookCondition;
    private String reviewNotes;
    private boolean isSold;

    // Seller info
    private UUID sellerId;
    private String sellerName;
    private String sellerPhone;

    private LocalDateTime createdAt;
}