package com.taskflowapi.service;

import com.taskflowapi.dto.TaskRequest;
import com.taskflowapi.dto.TaskUpdateRequest;
import com.taskflowapi.dto.TaskResponse;
import com.taskflowapi.entity.Project;
import com.taskflowapi.entity.Task;
import com.taskflowapi.exception.ResourceNotFoundException;
import com.taskflowapi.repository.ProjectRepository;
import com.taskflowapi.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    @Transactional
    public TaskResponse createTask(Long projectId, TaskRequest request, Long userId) {
        projectService.validateUserOwnsProject(projectId, userId);

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());
        task.setIsCompleted(false);
        task.setProject(project);

        Task savedTask = taskRepository.save(task);
        return mapToResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getProjectTasks(Long projectId, Long userId) {
        projectService.validateUserOwnsProject(projectId, userId);

        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse toggleTaskCompletion(Long taskId, Long userId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setIsCompleted(!task.getIsCompleted());
        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId, Long userId) {
        // Make DELETE idempotent - if task doesn't exist, treat as already deleted
        taskRepository.findByIdAndUserId(taskId, userId)
                .ifPresent(taskRepository::delete);
    }

    @Transactional
    public TaskResponse updateTask(Long taskId, TaskUpdateRequest request, Long userId) {
        Task task = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (request.getTitle() != null) {
            String trimmed = request.getTitle().trim();
            if (trimmed.isEmpty()) {
                throw new IllegalArgumentException("Title, if provided, must not be blank");
            }
            task.setTitle(trimmed);
        }

        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }

        Task updatedTask = taskRepository.save(task);
        return mapToResponse(updatedTask);
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getIsCompleted(),
                task.getProject().getId(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
