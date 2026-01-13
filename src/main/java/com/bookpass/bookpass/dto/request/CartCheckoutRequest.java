package com.bookpass.bookpass.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CartCheckoutRequest {
    @NotEmpty(message = "At least one book ID is required")
    private List<UUID> bookIds;

    @NotBlank(message = "Payment ID is required")
    private String paymentId;
}
