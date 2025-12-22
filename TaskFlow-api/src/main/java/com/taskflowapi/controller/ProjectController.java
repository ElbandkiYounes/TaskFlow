package com.taskflowapi.controller;

import com.taskflowapi.dto.ProjectProgressResponse;
import com.taskflowapi.dto.ProjectRequest;
import com.taskflowapi.dto.ProjectResponse;
import com.taskflowapi.security.JwtUtil;
import com.taskflowapi.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(
            @Valid @RequestBody ProjectRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        ProjectResponse response = projectService.createProject(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getUserProjects(
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        List<ProjectResponse> projects = projectService.getUserProjects(userId);
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        ProjectResponse project = projectService.getProjectById(id, userId);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(
        @PathVariable Long id,
        @Valid @RequestBody ProjectRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
    Long userId = extractUserId(authHeader);
    ProjectResponse updated = projectService.updateProject(id, request, userId);
    return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(
        @PathVariable Long id,
        @RequestHeader("Authorization") String authHeader
    ) {
    Long userId = extractUserId(authHeader);
    projectService.deleteProject(id, userId);
    return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/progress")
    public ResponseEntity<ProjectProgressResponse> getProjectProgress(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader
    ) {
        Long userId = extractUserId(authHeader);
        ProjectProgressResponse progress = projectService.getProjectProgress(id, userId);
        return ResponseEntity.ok(progress);
    }

    private Long extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        return jwtUtil.extractUserId(token);
    }
}
