package com.unziproute.tms_service.repo;

import com.unziproute.tms_service.entity.TaskMasterEo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskRepo extends JpaRepository<TaskMasterEo, String> {

    @Query(value = "SELECT t.task_id FROM tms.task_master_eo t ORDER BY t.task_id DESC LIMIT 1", nativeQuery = true)
    String findLastTaskId();

    List<TaskMasterEo> findByStatus(String status);

    List<TaskMasterEo> findByStatusAndDeleted(String status, char delete);

    long countByStatus(String status);

    TaskMasterEo findByTaskId(String taskId);

    TaskMasterEo findByTaskIdAndStatusAndDeleted(String taskId, String status, char deleted);

    boolean existsByTaskIdAndStatusAndDeleted(String taskId, String status, char deleted);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tms.task_master_eo t SET t.status = :status, t.delete_at = :deletedAt, t.delete_by = :deletedBy, t.deleted = 'Y' WHERE t.task_id = :taskId", nativeQuery = true)
    void deleteTask(@Param("status") String status,
                    @Param("deletedAt") LocalDateTime deletedAt,
                    @Param("deletedBy") String deletedBy,
                    @Param("taskId") String taskId);


}
