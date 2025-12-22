package com.taskflowapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectProgressResponse {
    private Long projectId;
    private String projectTitle;
    private long totalTasks;
    private long completedTasks;
    private double progressPercentage;
}
