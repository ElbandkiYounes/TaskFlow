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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService Unit Tests")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private ProjectService projectService;

    private User testUser;
    private Project testProject;
    private ProjectRequest projectRequest;

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

        projectRequest = new ProjectRequest();
        projectRequest.setTitle("New Project");
        projectRequest.setDescription("New Description");
    }

    // ========== createProject Tests ==========

    @Test
    @DisplayName("createProject() - Success: Should create and return project")
    void createProject_WithValidData_ShouldReturnProjectResponse() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Act
        ProjectResponse response = projectService.createProject(projectRequest, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(testProject.getTitle());
        assertThat(response.getDescription()).isEqualTo(testProject.getDescription());
        assertThat(response.getUserId()).isEqualTo(testUser.getId());

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("createProject() - Failure: Should throw ResourceNotFoundException when user not found")
    void createProject_WithNonExistentUser_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.createProject(projectRequest, testUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("createProject() - Edge Case: Should handle null description")
    void createProject_WithNullDescription_ShouldCreateProject() {
        // Arrange
        projectRequest.setDescription(null);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Act
        ProjectResponse response = projectService.createProject(projectRequest, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("createProject() - Edge Case: Should handle empty description")
    void createProject_WithEmptyDescription_ShouldCreateProject() {
        // Arrange
        projectRequest.setDescription("");
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // Act
        ProjectResponse response = projectService.createProject(projectRequest, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    // ========== getUserProjects Tests ==========

    @Test
    @DisplayName("getUserProjects() - Success: Should return list of user's projects")
    void getUserProjects_WithExistingProjects_ShouldReturnProjectList() {
        // Arrange
        Project project2 = new Project();
        project2.setId(2L);
        project2.setTitle("Project 2");
        project2.setDescription("Description 2");
        project2.setUser(testUser);
        project2.setCreatedAt(LocalDateTime.now());
        project2.setUpdatedAt(LocalDateTime.now());

        List<Project> projects = Arrays.asList(testProject, project2);
        when(projectRepository.findByUserId(testUser.getId())).thenReturn(projects);

        // Act
        List<ProjectResponse> responses = projectService.getUserProjects(testUser.getId());

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo(testProject.getTitle());
        assertThat(responses.get(1).getTitle()).isEqualTo(project2.getTitle());

        verify(projectRepository, times(1)).findByUserId(testUser.getId());
    }

    @Test
    @DisplayName("getUserProjects() - Edge Case: Should return empty list when no projects exist")
    void getUserProjects_WithNoProjects_ShouldReturnEmptyList() {
        // Arrange
        when(projectRepository.findByUserId(testUser.getId())).thenReturn(Arrays.asList());

        // Act
        List<ProjectResponse> responses = projectService.getUserProjects(testUser.getId());

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();

        verify(projectRepository, times(1)).findByUserId(testUser.getId());
    }

    @Test
    @DisplayName("getUserProjects() - Edge Case: Should handle user with single project")
    void getUserProjects_WithSingleProject_ShouldReturnSingleProjectList() {
        // Arrange
        when(projectRepository.findByUserId(testUser.getId())).thenReturn(Arrays.asList(testProject));

        // Act
        List<ProjectResponse> responses = projectService.getUserProjects(testUser.getId());

        // Assert
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo(testProject.getTitle());

        verify(projectRepository, times(1)).findByUserId(testUser.getId());
    }

    // ========== getProjectById Tests ==========

    @Test
    @DisplayName("getProjectById() - Success: Should return project when it exists and belongs to user")
    void getProjectById_WithValidIdAndUser_ShouldReturnProject() {
        // Arrange
        when(projectRepository.findByIdAndUserId(testProject.getId(), testUser.getId()))
                .thenReturn(Optional.of(testProject));

        // Act
        ProjectResponse response = projectService.getProjectById(testProject.getId(), testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testProject.getId());
        assertThat(response.getTitle()).isEqualTo(testProject.getTitle());

        verify(projectRepository, times(1)).findByIdAndUserId(testProject.getId(), testUser.getId());
    }

    // ========== updateProject Tests ===========

    @Test
    @DisplayName("updateProject() - Success: Should update title and description")
    void updateProject_WithValidData_ShouldUpdateFields() {
        // Arrange
        Project existing = new Project();
        existing.setId(testProject.getId());
        existing.setTitle("Old Title");
        existing.setDescription("Old Description");
        existing.setUser(testUser);
        existing.setCreatedAt(LocalDateTime.now());
        existing.setUpdatedAt(LocalDateTime.now());

        Project saved = new Project();
        saved.setId(testProject.getId());
        saved.setTitle(projectRequest.getTitle());
        saved.setDescription(projectRequest.getDescription());
        saved.setUser(testUser);
        saved.setCreatedAt(existing.getCreatedAt());
        saved.setUpdatedAt(LocalDateTime.now());

        when(projectRepository.findByIdAndUserId(testProject.getId(), testUser.getId()))
                .thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenReturn(saved);

        // Act
        ProjectResponse response = projectService.updateProject(testProject.getId(), projectRequest, testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo(projectRequest.getTitle());
        assertThat(response.getDescription()).isEqualTo(projectRequest.getDescription());
        verify(projectRepository, times(1)).findByIdAndUserId(testProject.getId(), testUser.getId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    @DisplayName("updateProject() - Failure: Should throw when project not found for user")
    void updateProject_WithNonExistentProject_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(projectRepository.findByIdAndUserId(999L, testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.updateProject(999L, projectRequest, testUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Project not found");

        verify(projectRepository, times(1)).findByIdAndUserId(999L, testUser.getId());
        verify(projectRepository, never()).save(any(Project.class));
    }

    // ========== deleteProject Tests ===========

    @Test
    @DisplayName("deleteProject() - Success: Should delete project when it exists for user")
    void deleteProject_WithExistingProject_ShouldDelete() {
        // Arrange
        when(projectRepository.findByIdAndUserId(testProject.getId(), testUser.getId()))
                .thenReturn(Optional.of(testProject));

        // Act
        projectService.deleteProject(testProject.getId(), testUser.getId());

        // Assert
        verify(projectRepository, times(1)).findByIdAndUserId(testProject.getId(), testUser.getId());
        verify(projectRepository, times(1)).delete(testProject);
    }

    @Test
    @DisplayName("deleteProject() - Idempotent: Should do nothing when project not found for user")
    void deleteProject_WithNonExistentProject_ShouldBeIdempotent() {
        // Arrange
        when(projectRepository.findByIdAndUserId(999L, testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatCode(() -> projectService.deleteProject(999L, testUser.getId()))
                .doesNotThrowAnyException();

        verify(projectRepository, times(1)).findByIdAndUserId(999L, testUser.getId());
        verify(projectRepository, never()).delete(any(Project.class));
    }
    @Test
    @DisplayName("getProjectById() - Failure: Should throw ResourceNotFoundException when project not found")
    void getProjectById_WithNonExistentProject_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(projectRepository.findByIdAndUserId(999L, testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.getProjectById(999L, testUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Project not found");

        verify(projectRepository, times(1)).findByIdAndUserId(999L, testUser.getId());
    }

    @Test
    @DisplayName("getProjectById() - Failure: Should throw ResourceNotFoundException when project belongs to another user")
    void getProjectById_WithWrongUser_ShouldThrowResourceNotFoundException() {
        // Arrange
        Long wrongUserId = 999L;
        when(projectRepository.findByIdAndUserId(testProject.getId(), wrongUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.getProjectById(testProject.getId(), wrongUserId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Project not found");

        verify(projectRepository, times(1)).findByIdAndUserId(testProject.getId(), wrongUserId);
    }

    // ========== getProjectProgress Tests ==========

    @Test
    @DisplayName("getProjectProgress() - Success: Should calculate progress correctly with tasks")
    void getProjectProgress_WithTasks_ShouldReturnCorrectProgress() {
        // Arrange
        when(projectRepository.findByIdAndUserId(testProject.getId(), testUser.getId()))
                .thenReturn(Optional.of(testProject));
        when(taskRepository.countByProjectId(testProject.getId())).thenReturn(10L);
        when(taskRepository.countCompletedByProjectId(testProject.getId())).thenReturn(7L);

        // Act
        ProjectProgressResponse response = projectService.getProjectProgress(testProject.getId(), testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getProjectId()).isEqualTo(testProject.getId());
        assertThat(response.getProjectTitle()).isEqualTo(testProject.getTitle());
        assertThat(response.getTotalTasks()).isEqualTo(10L);
        assertThat(response.getCompletedTasks()).isEqualTo(7L);
        assertThat(response.getProgressPercentage()).isEqualTo(70.0);

        verify(projectRepository, times(1)).findByIdAndUserId(testProject.getId(), testUser.getId());
        verify(taskRepository, times(1)).countByProjectId(testProject.getId());
        verify(taskRepository, times(1)).countCompletedByProjectId(testProject.getId());
    }

    @Test
    @DisplayName("getProjectProgress() - Edge Case: Should return 0% progress when no tasks exist")
    void getProjectProgress_WithNoTasks_ShouldReturn0Percent() {
        // Arrange
        when(projectRepository.findByIdAndUserId(testProject.getId(), testUser.getId()))
                .thenReturn(Optional.of(testProject));
        when(taskRepository.countByProjectId(testProject.getId())).thenReturn(0L);
        when(taskRepository.countCompletedByProjectId(testProject.getId())).thenReturn(0L);

        // Act
        ProjectProgressResponse response = projectService.getProjectProgress(testProject.getId(), testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTotalTasks()).isEqualTo(0L);
        assertThat(response.getCompletedTasks()).isEqualTo(0L);
        assertThat(response.getProgressPercentage()).isEqualTo(0.0);

        verify(projectRepository, times(1)).findByIdAndUserId(testProject.getId(), testUser.getId());
    }

    @Test
    @DisplayName("getProjectProgress() - Edge Case: Should return 100% when all tasks completed")
    void getProjectProgress_WithAllTasksCompleted_ShouldReturn100Percent() {
        // Arrange
        when(projectRepository.findByIdAndUserId(testProject.getId(), testUser.getId()))
                .thenReturn(Optional.of(testProject));
        when(taskRepository.countByProjectId(testProject.getId())).thenReturn(5L);
        when(taskRepository.countCompletedByProjectId(testProject.getId())).thenReturn(5L);

        // Act
        ProjectProgressResponse response = projectService.getProjectProgress(testProject.getId(), testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getTotalTasks()).isEqualTo(5L);
        assertThat(response.getCompletedTasks()).isEqualTo(5L);
        assertThat(response.getProgressPercentage()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("getProjectProgress() - Edge Case: Should handle partial completion correctly")
    void getProjectProgress_WithPartialCompletion_ShouldCalculateCorrectly() {
        // Arrange
        when(projectRepository.findByIdAndUserId(testProject.getId(), testUser.getId()))
                .thenReturn(Optional.of(testProject));
        when(taskRepository.countByProjectId(testProject.getId())).thenReturn(3L);
        when(taskRepository.countCompletedByProjectId(testProject.getId())).thenReturn(1L);

        // Act
        ProjectProgressResponse response = projectService.getProjectProgress(testProject.getId(), testUser.getId());

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getProgressPercentage()).isEqualTo(33.33);
    }

    @Test
    @DisplayName("getProjectProgress() - Failure: Should throw ResourceNotFoundException when project not found")
    void getProjectProgress_WithNonExistentProject_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(projectRepository.findByIdAndUserId(999L, testUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.getProjectProgress(999L, testUser.getId()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Project not found");

        verify(projectRepository, times(1)).findByIdAndUserId(999L, testUser.getId());
        verify(taskRepository, never()).countByProjectId(anyLong());
    }

    // ========== validateUserOwnsProject Tests ==========

    @Test
    @DisplayName("validateUserOwnsProject() - Success: Should not throw exception when user owns project")
    void validateUserOwnsProject_WithValidOwner_ShouldNotThrowException() {
        // Arrange
        when(projectRepository.findByIdAndUserId(testProject.getId(), testUser.getId()))
                .thenReturn(Optional.of(testProject));

        // Act & Assert
        assertThatCode(() -> projectService.validateUserOwnsProject(testProject.getId(), testUser.getId()))
                .doesNotThrowAnyException();

        verify(projectRepository, times(1)).findByIdAndUserId(testProject.getId(), testUser.getId());
    }

    @Test
    @DisplayName("validateUserOwnsProject() - Failure: Should throw UnauthorizedException when user doesn't own project")
    void validateUserOwnsProject_WithWrongOwner_ShouldThrowUnauthorizedException() {
        // Arrange
        Long wrongUserId = 999L;
        when(projectRepository.findByIdAndUserId(testProject.getId(), wrongUserId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.validateUserOwnsProject(testProject.getId(), wrongUserId))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("You don't have access to this project");

        verify(projectRepository, times(1)).findByIdAndUserId(testProject.getId(), wrongUserId);
    }

    @Test
    @DisplayName("validateUserOwnsProject() - Failure: Should throw UnauthorizedException when project doesn't exist")
    void validateUserOwnsProject_WithNonExistentProject_ShouldThrowUnauthorizedException() {
        // Arrange
        when(projectRepository.findByIdAndUserId(999L, testUser.getId()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> projectService.validateUserOwnsProject(999L, testUser.getId()))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("You don't have access to this project");

        verify(projectRepository, times(1)).findByIdAndUserId(999L, testUser.getId());
    }
}
