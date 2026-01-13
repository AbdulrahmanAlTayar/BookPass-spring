package com.bookpass.bookpass.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartCheckoutResponse {
    private List<BookResponse> purchasedBooks;
    private BigDecimal totalAmount;
    private int totalBooks;
    private String paymentId;
    private String status; // COMPLETED
}
