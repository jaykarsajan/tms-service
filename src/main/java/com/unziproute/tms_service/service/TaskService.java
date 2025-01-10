package com.unziproute.tms_service.service;

import com.unziproute.tms_service.entity.TaskMasterEo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TaskService {

    Map<String, Object> createNewTask(TaskMasterEo request);

    List<TaskMasterEo> fetchAllTask();

    List<TaskMasterEo> fetchAssignedTask();

    List<TaskMasterEo> fetchFinishedTask();

    List<TaskMasterEo> fetchRemovedTask();

    Map<String, Object> fetchTaskCount();

    Optional<TaskMasterEo> fetchTaskById(String taskId);


    void updateTask(TaskMasterEo request);

    Optional<TaskMasterEo> findByTaskIdAndStatusAndDeleted(String taskId, String status, char deleted);

    boolean existsByTaskIdAndStatusAndDeleted(String taskId);

    void deleteTask(String status, LocalDateTime deletedAt, String deletedBy, String taskId);


}