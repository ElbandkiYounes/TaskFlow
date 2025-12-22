package com.taskflowapi.service;

import com.taskflowapi.dto.ProjectProgressResponse;
import com.taskflowapi.dto.ProjectRequest;
import com.taskflowapi.dto.ProjectResponse;
import com.taskflowapi.entity.Project;
import com.taskflowapi.entity.User;
import com.taskflowapi.exception.ResourceNotFoundException;
import com.taskflowapi.exception.UnauthorizedException;
import com.taskflowapi.repository.ProjectRepository;
import com.taskflowapi.repository.TaskRepository;
import com.taskflowapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public ProjectResponse createProject(ProjectRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Project project = new Project();
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setUser(user);

        Project savedProject = projectRepository.save(project);
        return mapToResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getUserProjects(Long userId) {
        return projectRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long projectId, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        return mapToResponse(project);
    }

    @Transactional
    public ProjectResponse updateProject(Long projectId, ProjectRequest request, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());

        Project saved = projectRepository.save(project);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteProject(Long projectId, Long userId) {
        // Idempotent: if not found for this user, treat as already deleted
        projectRepository.findByIdAndUserId(projectId, userId)
                .ifPresent(projectRepository::delete);
        // Tasks are deleted automatically due to cascade = ALL and orphanRemoval = true on Project.tasks
    }

    @Transactional(readOnly = true)
    public ProjectProgressResponse getProjectProgress(Long projectId, Long userId) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        long totalTasks = taskRepository.countByProjectId(projectId);
        long completedTasks = taskRepository.countCompletedByProjectId(projectId);
        double progressPercentage = totalTasks > 0 ? (completedTasks * 100.0 / totalTasks) : 0.0;

        return new ProjectProgressResponse(
                project.getId(),
                project.getTitle(),
                totalTasks,
                completedTasks,
                Math.round(progressPercentage * 100.0) / 100.0
        );
    }

    public void validateUserOwnsProject(Long projectId, Long userId) {
        projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new UnauthorizedException("You don't have access to this project"));
    }

    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getDescription(),
                project.getUser().getId(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
