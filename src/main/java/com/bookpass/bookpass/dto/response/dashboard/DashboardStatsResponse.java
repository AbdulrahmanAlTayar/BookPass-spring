package com.bookpass.bookpass.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsResponse {
    private MetricsDTO metrics;
    private List<UniversityStatDTO> universityStats;
    private List<RecentUserDTO> recentUsers;
    private List<RecentBookDTO> recentBooks;
}
