package com.taskflowapi.controller;

import com.taskflowapi.dto.TaskUpdateRequest;
import com.taskflowapi.dto.TaskRequest;
import com.taskflowapi.dto.TaskResponse;
import com.taskflowapi.security.JwtUtil;
import com.taskflowapi.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final JwtUtil jwtUtil;

    @PostMapping("/projects/{projectId}/tasks")
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable Long projectId,
            @Valid @RequestBody TaskRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        TaskResponse response = taskService.createTask(projectId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/projects/{projectId}/tasks")
    public ResponseEntity<List<TaskResponse>> getProjectTasks(
            @PathVariable Long projectId,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        List<TaskResponse> tasks = taskService.getProjectTasks(projectId, userId);
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/tasks/{id}/complete")
    public ResponseEntity<TaskResponse> toggleTaskCompletion(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        TaskResponse response = taskService.toggleTaskCompletion(id, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        taskService.deleteTask(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/tasks/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody TaskUpdateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        TaskResponse response = taskService.updateTask(id, request, userId);
        return ResponseEntity.ok(response);
    }

    private Long extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}
