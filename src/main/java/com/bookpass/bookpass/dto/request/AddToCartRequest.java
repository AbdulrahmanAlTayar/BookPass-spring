package com.bookpass.bookpass.dto.request;

import lombok.Data;
import java.util.UUID;

@Data
public class AddToCartRequest {
    private UUID bookId;
}
