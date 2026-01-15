package com.bookpass.bookpass.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetricsDTO {
    private long totalBooks;
    private BigDecimal totalRevenue;
    private long availableBooks;
    private long soldBooks;
    private long totalUsers;
    private long activeUsers;
    private long universities;
}
