package com.bookpass.bookpass.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentBookDTO {
    private UUID id;
    private String title;
    private BigDecimal price;
    private String status;
}
