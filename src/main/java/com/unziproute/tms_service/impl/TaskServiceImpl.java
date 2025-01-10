package com.unziproute.tms_service.impl;

import com.unziproute.tms_service.entity.TaskMasterEo;
import com.unziproute.tms_service.repo.TaskRepo;
import com.unziproute.tms_service.service.TaskService;
import com.unziproute.tms_service.utils.Constants;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {


    private final TaskRepo taskRepo;

    public static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    protected TaskServiceImpl(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    public Map<String, Object> createNewTask(TaskMasterEo request) {
        logger.info("Inside TaskServiceImpl createNewTask");
        Map<String, Object> response = new HashMap<>();
        try {
            String lastTaskId = taskRepo.findLastTaskId();

            int nextIdNumber = 1;
            if (lastTaskId != null || lastTaskId.startsWith("Task-")) {
                String[] parts = lastTaskId.split("-");
                nextIdNumber = Integer.parseInt(parts[1]) + 1;
            }
            String newTaskId = "Task-" + nextIdNumber;

            TaskMasterEo newTask = new TaskMasterEo();
            newTask.setTaskId(newTaskId);
            newTask.setTask(request.getTask());
            newTask.setDescription(request.getDescription());
            newTask.setCreatedBy(request.getCreatedBy());
            newTask.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            newTask.setStatus("PENDING");
            newTask.setDeleted('N');
            taskRepo.save(newTask);

            response.put("task", newTask);
            response.put("status", true);
        } catch (Exception e) {
            response.put("task", null);
            response.put("status", false);
            logger.error("Inside TaskServiceImpl createNewTask catch :: " + e.getMessage());
        }
        logger.info("Exit from TaskServiceImpl createNewTask");
        return response;
    }

    public List<TaskMasterEo> fetchAllTask() {
        logger.info("Inside TaskServiceImpl fetchAllTask");
        return taskRepo.findAll();
    }

    public List<TaskMasterEo> fetchAssignedTask() {
        logger.info("Inside TaskServiceImpl fetchAssignedTask");
        return taskRepo.findByStatus(Constants.pending);
    }

    public List<TaskMasterEo> fetchFinishedTask() {
        logger.info("Inside TaskServiceImpl fetchFinishedTask");
        return taskRepo.findByStatus(Constants.finished);
    }

    public List<TaskMasterEo> fetchRemovedTask() {
        logger.info("Inside TaskServiceImpl fetchRemovedTask");
        return taskRepo.findByStatusAndDeleted(Constants.deleted, 'Y');
    }

    public Map<String, Object> fetchTaskCount() {
        logger.info("Inside TaskServiceImpl fetchTaskCount");
        Map<String, Object> response = new HashMap<>();
        try {
            long pendingTaskCount = taskRepo.countByStatus(Constants.pending);
            long finishedTaskCount = taskRepo.countByStatus(Constants.finished);
            long removedTaskCount = taskRepo.countByStatus(Constants.deleted);

            response.put("pending", pendingTaskCount);
            response.put("finished", finishedTaskCount);
            response.put("removed", removedTaskCount);
        } catch (Exception e) {
            logger.error("Inside TaskServiceImpl fetchTaskCount catch --> " + e.getMessage());
        }
        return response;
    }

    public Optional<TaskMasterEo> fetchTaskById(String taskId) {
        logger.info("Inside TaskServiceImpl fetchTaskById");
        return Optional.ofNullable(taskRepo.findByTaskId(taskId));
    }


    public void updateTask(TaskMasterEo request) {
        logger.info("Inside TaskServiceImpl updateTask");
        taskRepo.save(request);
    }

    public Optional<TaskMasterEo> findByTaskIdAndStatusAndDeleted(String taskId, String status, char deleted) {
        logger.info("Inside TaskServiceImpl findByTaskIdAndStatusAndDeleted");
        return Optional.ofNullable(taskRepo.findByTaskIdAndStatusAndDeleted(taskId, status, deleted));
    }


    public boolean existsByTaskIdAndStatusAndDeleted(String taskId) {
        logger.info("Inside TaskServiceImpl existsByTaskIdAndStatusAndDeleted");
        return taskRepo.existsByTaskIdAndStatusAndDeleted(taskId, Constants.pending, 'N');
    }


    public void deleteTask(String status, LocalDateTime deletedAt, String deletedBy, String taskId) {
        logger.info("Inside TaskServiceImpl deleteTask");
        try {
            taskRepo.deleteTask(status,deletedAt,deletedBy,taskId);
        } catch (Exception e) {
            logger.error("Inside TaskServiceImpl deleteTask catch --> " + e.getMessage());
        }
    }

}
