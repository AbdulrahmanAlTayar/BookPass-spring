package com.bookpass.bookpass.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentUserDTO {
    private UUID id;
    private String name;
    private String role;
    private String university;
    private LocalDate joinDate;
}
