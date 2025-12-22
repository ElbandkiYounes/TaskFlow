package com.taskflowapi.repository;

import com.taskflowapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    
    @Query("SELECT t FROM Task t WHERE t.id = :taskId AND t.project.user.id = :userId")
    Optional<Task> findByIdAndUserId(@Param("taskId") Long taskId, @Param("userId") Long userId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId")
    long countByProjectId(@Param("projectId") Long projectId);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.isCompleted = true")
    long countCompletedByProjectId(@Param("projectId") Long projectId);
}
