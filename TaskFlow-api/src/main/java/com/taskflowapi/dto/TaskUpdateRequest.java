package com.taskflowapi.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title; // optional; if provided, must not be blank

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description; // optional
}
