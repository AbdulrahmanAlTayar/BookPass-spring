package com.bookpass.bookpass.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReviewBookRequest {

    @NotNull(message = "Condition is required")
    @Pattern(regexp = "excellent|very good|good|poor", message = "Condition must be excellent, very good, good, or poor")
    private String condition;

    private String reviewNotes;
}
