package com.bookpass.bookpass.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PurchaseRequest {
    @NotBlank(message = "Payment ID is required")
    private String paymentId;
}
