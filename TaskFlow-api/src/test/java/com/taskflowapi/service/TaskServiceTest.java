package com.taskflowapi.service;

import com.taskflowapi.dto.TaskRequest;
import com.taskflowapi.dto.TaskUpdateRequest;
import com.taskflowapi.dto.TaskResponse;
import com.taskflowapi.entity.Project;
import com.taskflowapi.entity.Task;
import com.taskflowapi.entity.User;
import com.taskflowapi.exception.ResourceNotFoundException;
import com.taskflowapi.exception.UnauthorizedException;
import com.taskflowapi.repository.ProjectRepository;
import com.taskflowapi.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TaskService Unit Tests")
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Project testProject;
    private Task testTask;
    private TaskRequest taskRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("john@example.com");
        testUser.setName("John Doe");

        testProject = new Project();
        testProject.setId(1L);
        testProject.setTitle("Test Project");
        testProject.setDescription("Test Description");
        testProject.setUser(testUser);
        testProject.setCreatedAt(LocalDateTime.now());
        testProject.setUpdatedAt(LocalDateTime.now());

        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDueDate(LocalDate.now().plusDays(7));
        testTask.setIsCompleted(false);
        testTask.setProject(testProject);
        testTask.setCreatedAt(LocalDateTime.now());
        testTask.setUpdatedAt(LocalDateTime.now());

        taskRequest = new TaskRequest();
        taskRequest.setTitle("New Task");
        taskRequest.setDescription("New Description");
        taskRequest.setDueDate(LocalDate.now().plusDays(5));
    }

    // ========== createTask Tests ==========

    @Test
    @DisplayName("createTask() - Success: Should create and return task")
    void createTask_WithValidData_ShouldReturnTaskResponse() {
        // Arrange
        doNothing().when(projectService).validateUserOwnsProject(testProject.getId(), testUser.getId());
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskResponse response = taskService.createTask(testProject.getId(), taskRequest, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(testTask.getTitle());
        assertThat(response.getDescription()).isEqualTo(testTask.getDescription());
        assertThat(response.getProjectId()).isEqualTo(testProject.getId());
        assertThat(response.getIsCompleted()).isFalse();

        verify(projectService, times(1)).validateUserOwnsProject(testProject.getId(), testUser.getId());
        verify(projectRepository, times(1)).findById(testProject.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask() - Failure: Should throw UnauthorizedException when user doesn't own project")
    void createTask_WithUnauthorizedUser_ShouldThrowUnauthorizedException() {
        // Arrange
        doThrow(new UnauthorizedException("You don't have access to this project"))
                .when(projectService).validateUserOwnsProject(testProject.getId(), 999L);

        // Act & Assert
        assertThatThrownBy(() -> taskService.createTask(testProject.getId(), taskRequest, 999L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("You don't have access to this project");

        verify(projectService, times(1)).validateUserOwnsProject(testProject.getId(), 999L);
        verify(projectRepository, never()).findById(anyLong());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask() - Failure: Should throw ResourceNotFoundException when project not found")
    void createTask_WithNonExistentProject_ShouldThrowResourceNotFoundException() {
        // Arrange
        doNothing().when(projectService).validateUserOwnsProject(testProject.getId(), testUser.getId());
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.createTask(testProject.getId(), taskRequest, testUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Project not found");

        verify(projectService, times(1)).validateUserOwnsProject(testProject.getId(), testUser.getId());
        verify(projectRepository, times(1)).findById(testProject.getId());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask() - Edge Case: Should create task without due date")
    void createTask_WithoutDueDate_ShouldCreateTask() {
        // Arrange
        taskRequest.setDueDate(null);
        doNothing().when(projectService).validateUserOwnsProject(testProject.getId(), testUser.getId());
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskResponse response = taskService.createTask(testProject.getId(), taskRequest, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask() - Edge Case: Should create task with null description")
    void createTask_WithNullDescription_ShouldCreateTask() {
        // Arrange
        taskRequest.setDescription(null);
        doNothing().when(projectService).validateUserOwnsProject(testProject.getId(), testUser.getId());
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskResponse response = taskService.createTask(testProject.getId(), taskRequest, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask() - Edge Case: Should create task with past due date")
    void createTask_WithPastDueDate_ShouldCreateTask() {
        // Arrange
        taskRequest.setDueDate(LocalDate.now().minusDays(5));
        doNothing().when(projectService).validateUserOwnsProject(testProject.getId(), testUser.getId());
        when(projectRepository.findById(testProject.getId())).thenReturn(Optional.of(testProject));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        // Act
        TaskResponse response = taskService.createTask(testProject.getId(), taskRequest, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ========== getProjectTasks Tests ==========

    @Test
    @DisplayName("getProjectTasks() - Success: Should return list of project tasks")
    void getProjectTasks_WithExistingTasks_ShouldReturnTaskList() {
        // Arrange
        Task task2 = new Task();
        task2.setId(2L);
        task2.setTitle("Task 2");
        task2.setDescription("Description 2");
        task2.setIsCompleted(true);
        task2.setProject(testProject);
        task2.setCreatedAt(LocalDateTime.now());
        task2.setUpdatedAt(LocalDateTime.now());

        List<Task> tasks = Arrays.asList(testTask, task2);
        doNothing().when(projectService).validateUserOwnsProject(testProject.getId(), testUser.getId());
        when(taskRepository.findByProjectId(testProject.getId())).thenReturn(tasks);

        // Act
        List<TaskResponse> responses = taskService.getProjectTasks(testProject.getId(), testUser.getId());

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo(testTask.getTitle());
        assertThat(responses.get(1).getTitle()).isEqualTo(task2.getTitle());

        verify(projectService, times(1)).validateUserOwnsProject(testProject.getId(), testUser.getId());
        verify(taskRepository, times(1)).findByProjectId(testProject.getId());
    }

    @Test
    @DisplayName("getProjectTasks() - Edge Case: Should return empty list when no tasks exist")
    void getProjectTasks_WithNoTasks_ShouldReturnEmptyList() {
        // Arrange
        doNothing().when(projectService).validateUserOwnsProject(testProject.getId(), testUser.getId());
        when(taskRepository.findByProjectId(testProject.getId())).thenReturn(Arrays.asList());

        // Act
        List<TaskResponse> responses = taskService.getProjectTasks(testProject.getId(), testUser.getId());

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();

        verify(projectService, times(1)).validateUserOwnsProject(testProject.getId(), testUser.getId());
        verify(taskRepository, times(1)).findByProjectId(testProject.getId());
    }

    @Test
    @DisplayName("getProjectTasks() - Failure: Should throw UnauthorizedException when user doesn't own project")
    void getProjectTasks_WithUnauthorizedUser_ShouldThrowUnauthorizedException() {
        // Arrange
        doThrow(new UnauthorizedException("You don't have access to this project"))
                .when(projectService).validateUserOwnsProject(testProject.getId(), 999L);

        // Act & Assert
        assertThatThrownBy(() -> taskService.getProjectTasks(testProject.getId(), 999L))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("You don't have access to this project");

        verify(projectService, times(1)).validateUserOwnsProject(testProject.getId(), 999L);
        verify(taskRepository, never()).findByProjectId(anyLong());
    }

    // ========== toggleTaskCompletion Tests ==========

    @Test
    @DisplayName("toggleTaskCompletion() - Success: Should toggle task from incomplete to complete")
    void toggleTaskCompletion_FromIncompleteToComplete_ShouldToggleStatus() {
        // Arrange
        testTask.setIsCompleted(false);
        Task toggledTask = new Task();
        toggledTask.setId(testTask.getId());
        toggledTask.setTitle(testTask.getTitle());
        toggledTask.setDescription(testTask.getDescription());
        toggledTask.setIsCompleted(true);
        toggledTask.setProject(testProject);
        toggledTask.setCreatedAt(testTask.getCreatedAt());
        toggledTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findByIdAndUserId(testTask.getId(), testUser.getId()))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(toggledTask);

        // Act
        TaskResponse response = taskService.toggleTaskCompletion(testTask.getId(), testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getIsCompleted()).isTrue();

        verify(taskRepository, times(1)).findByIdAndUserId(testTask.getId(), testUser.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("toggleTaskCompletion() - Success: Should toggle task from complete to incomplete")
    void toggleTaskCompletion_FromCompleteToIncomplete_ShouldToggleStatus() {
        // Arrange
        testTask.setIsCompleted(true);
        Task toggledTask = new Task();
        toggledTask.setId(testTask.getId());
        toggledTask.setTitle(testTask.getTitle());
        toggledTask.setDescription(testTask.getDescription());
        toggledTask.setIsCompleted(false);
        toggledTask.setProject(testProject);
        toggledTask.setCreatedAt(testTask.getCreatedAt());
        toggledTask.setUpdatedAt(LocalDateTime.now());

        when(taskRepository.findByIdAndUserId(testTask.getId(), testUser.getId()))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(toggledTask);

        // Act
        TaskResponse response = taskService.toggleTaskCompletion(testTask.getId(), testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getIsCompleted()).isFalse();

        verify(taskRepository, times(1)).findByIdAndUserId(testTask.getId(), testUser.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("toggleTaskCompletion() - Failure: Should throw ResourceNotFoundException when task not found")
    void toggleTaskCompletion_WithNonExistentTask_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(taskRepository.findByIdAndUserId(999L, testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.toggleTaskCompletion(999L, testUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task not found");

        verify(taskRepository, times(1)).findByIdAndUserId(999L, testUser.getId());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("toggleTaskCompletion() - Failure: Should throw ResourceNotFoundException when task belongs to another user")
    void toggleTaskCompletion_WithWrongUser_ShouldThrowResourceNotFoundException() {
        // Arrange
        Long wrongUserId = 999L;
        when(taskRepository.findByIdAndUserId(testTask.getId(), wrongUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.toggleTaskCompletion(testTask.getId(), wrongUserId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task not found");

        verify(taskRepository, times(1)).findByIdAndUserId(testTask.getId(), wrongUserId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    // ========== deleteTask Tests ==========

    @Test
    @DisplayName("deleteTask() - Success: Should delete task when it exists")
    void deleteTask_WithExistingTask_ShouldDeleteTask() {
        // Arrange
        when(taskRepository.findByIdAndUserId(testTask.getId(), testUser.getId()))
                .thenReturn(Optional.of(testTask));

        // Act
        taskService.deleteTask(testTask.getId(), testUser.getId());

        // Assert
        verify(taskRepository, times(1)).findByIdAndUserId(testTask.getId(), testUser.getId());
        verify(taskRepository, times(1)).delete(testTask);
    }

    @Test
    @DisplayName("deleteTask() - Idempotent: Should succeed when task doesn't exist (already deleted)")
    void deleteTask_WithNonExistentTask_ShouldSucceedIdempotently() {
        // Arrange
        when(taskRepository.findByIdAndUserId(999L, testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatCode(() -> taskService.deleteTask(999L, testUser.getId()))
                .doesNotThrowAnyException();

        verify(taskRepository, times(1)).findByIdAndUserId(999L, testUser.getId());
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    @DisplayName("deleteTask() - Idempotent: Should succeed when task belongs to another user")
    void deleteTask_WithWrongUser_ShouldSucceedIdempotently() {
        // Arrange
        Long wrongUserId = 999L;
        when(taskRepository.findByIdAndUserId(testTask.getId(), wrongUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatCode(() -> taskService.deleteTask(testTask.getId(), wrongUserId))
                .doesNotThrowAnyException();

        verify(taskRepository, times(1)).findByIdAndUserId(testTask.getId(), wrongUserId);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    @DisplayName("deleteTask() - Edge Case: Should be callable multiple times (idempotent)")
    void deleteTask_CalledMultipleTimes_ShouldBeIdempotent() {
        // Arrange - first call finds and deletes
        when(taskRepository.findByIdAndUserId(testTask.getId(), testUser.getId()))
                .thenReturn(Optional.of(testTask))
                .thenReturn(Optional.empty());

        // Act - call twice
        taskService.deleteTask(testTask.getId(), testUser.getId());
        taskService.deleteTask(testTask.getId(), testUser.getId());

        // Assert
        verify(taskRepository, times(2)).findByIdAndUserId(testTask.getId(), testUser.getId());
        verify(taskRepository, times(1)).delete(testTask);
    }

    // ========== updateTask Tests ===========

    @Test
    @DisplayName("updateTask() - Success: Should update title and description when provided")
    void updateTask_WithTitleAndDescription_ShouldUpdateFields() {
        // Arrange
        Task existing = new Task();
        existing.setId(testTask.getId());
        existing.setTitle("Old Title");
        existing.setDescription("Old Description");
        existing.setProject(testProject);
        existing.setIsCompleted(false);
        existing.setCreatedAt(testTask.getCreatedAt());
        existing.setUpdatedAt(testTask.getUpdatedAt());

        Task updated = new Task();
        updated.setId(testTask.getId());
        updated.setTitle("New Title");
        updated.setDescription("New Desc");
        updated.setProject(testProject);
        updated.setIsCompleted(false);
        updated.setCreatedAt(existing.getCreatedAt());
        updated.setUpdatedAt(LocalDateTime.now());

        TaskUpdateRequest request = new TaskUpdateRequest("New Title", "New Desc");

        when(taskRepository.findByIdAndUserId(testTask.getId(), testUser.getId()))
                .thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(updated);

        // Act
        TaskResponse response = taskService.updateTask(testTask.getId(), request, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("New Title");
        assertThat(response.getDescription()).isEqualTo("New Desc");
        verify(taskRepository, times(1)).findByIdAndUserId(testTask.getId(), testUser.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTask() - Success: Should update only description when title is null")
    void updateTask_WithOnlyDescription_ShouldUpdateDescription() {
        // Arrange
        Task existing = new Task();
        existing.setId(testTask.getId());
        existing.setTitle("Keep Title");
        existing.setDescription("Old Description");
        existing.setProject(testProject);
        existing.setIsCompleted(false);

        Task updated = new Task();
        updated.setId(testTask.getId());
        updated.setTitle("Keep Title");
        updated.setDescription("New Desc");
        updated.setProject(testProject);
        updated.setIsCompleted(false);

        TaskUpdateRequest request = new TaskUpdateRequest(null, "New Desc");

        when(taskRepository.findByIdAndUserId(testTask.getId(), testUser.getId()))
                .thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenReturn(updated);

        // Act
        TaskResponse response = taskService.updateTask(testTask.getId(), request, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Keep Title");
        assertThat(response.getDescription()).isEqualTo("New Desc");
        verify(taskRepository, times(1)).findByIdAndUserId(testTask.getId(), testUser.getId());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTask() - Failure: Should throw IllegalArgumentException when title is blank")
    void updateTask_WithBlankTitle_ShouldThrowIllegalArgumentException() {
        // Arrange
        Task existing = testTask;
        TaskUpdateRequest request = new TaskUpdateRequest("   ", "Desc");

        when(taskRepository.findByIdAndUserId(testTask.getId(), testUser.getId()))
                .thenReturn(Optional.of(existing));

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask(testTask.getId(), request, testUser.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title, if provided, must not be blank");

        verify(taskRepository, times(1)).findByIdAndUserId(testTask.getId(), testUser.getId());
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("updateTask() - Failure: Should throw ResourceNotFoundException when task not found")
    void updateTask_WithNonExistentTask_ShouldThrowResourceNotFoundException() {
        // Arrange
        TaskUpdateRequest request = new TaskUpdateRequest("New Title", "New Desc");
        when(taskRepository.findByIdAndUserId(999L, testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> taskService.updateTask(999L, request, testUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Task not found");

        verify(taskRepository, times(1)).findByIdAndUserId(999L, testUser.getId());
        verify(taskRepository, never()).save(any(Task.class));
    }
}
