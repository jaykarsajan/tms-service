package com.unziproute.tms_service.controller;

import com.unziproute.tms_service.dto.ApiResponse;
import com.unziproute.tms_service.entity.TaskMasterEo;
import com.unziproute.tms_service.service.TaskService;
import com.unziproute.tms_service.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    @GetMapping("/fetch-task-count")
    public ResponseEntity fetchTaskCount() {
        logger.info("Inside TaskController fetchTaskCount");
        Map<String, Object> count = taskService.fetchTaskCount();
        ApiResponse<Map<String, Object>> response = new ApiResponse<>(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                "Count of tasks",
                HttpStatus.OK.value(),
                Map.of(
                        "pending", count.get("pending"),
                        "finished", count.get("finished"),
                        "removed", count.get("removed")
                )
        );
        logger.info("Exiting TaskController fetchTaskCount");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fetch-all-task")
    public ResponseEntity fetchAllTask() {
        logger.info("Inside TaskController fetchAllTask");
        List<TaskMasterEo> tasks = taskService.fetchAllTask();
        String message = tasks.isEmpty() ? "No task found" : "Task fetch successfully";
        ApiResponse<List<TaskMasterEo>> response = new ApiResponse<>(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                message,
                HttpStatus.OK.value(),
                tasks
        );
        logger.info("Exiting TaskController fetchAllTask");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fetch-assigned-task")
    public ResponseEntity fetchAssignedTask() {
        logger.info("Inside TaskController fetchAssignedTask");
        List<TaskMasterEo> tasks = taskService.fetchAssignedTask();
        String message = tasks.isEmpty() ? "No assigned task found" : "Assigned task fetch successfully";
        ApiResponse<List<TaskMasterEo>> response = new ApiResponse<>(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                message,
                HttpStatus.OK.value(),
                tasks
        );
        logger.info("Exiting TaskController fetchAssignedTask");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fetch-finished-task")
    public ResponseEntity fetchFinishedTask() {
        logger.info("Inside TaskController fetchFinishedTask");
        List<TaskMasterEo> tasks = taskService.fetchFinishedTask();
        String message = tasks.isEmpty() ? "No finished task found" : "Finished task fetch successfully";
        ApiResponse<List<TaskMasterEo>> response = new ApiResponse<>(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                message,
                HttpStatus.OK.value(),
                tasks
        );
        logger.info("Exiting TaskController fetchFinishedTask");
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/fetch-removed-task")
    public ResponseEntity fetchRemovedTask() {
        logger.info("Inside TaskController fetchRemovedTask");
        List<TaskMasterEo> tasks = taskService.fetchRemovedTask();
        String message = tasks.isEmpty() ? "No removed task found" : "Removed task fetch successfully";
        ApiResponse<List<TaskMasterEo>> response = new ApiResponse<>(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                message,
                HttpStatus.OK.value(),
                tasks
        );
        logger.info("Exiting TaskController fetchRemovedTask");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fetch-task-by-id/{taskId}")
    public ResponseEntity fetchTaskById(@PathVariable String taskId) {
        logger.info("Inside TaskController fetchTaskById");
        Optional<TaskMasterEo> task = taskService.fetchTaskById(taskId);
        String message = task.isEmpty() ? "No task found" : "Task fetch successfully";
        ApiResponse<TaskMasterEo> response = new ApiResponse<TaskMasterEo>(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                message,
                task.isPresent() ? HttpStatus.OK.value() : HttpStatus.NO_CONTENT.value(),
                task.isPresent() ? task.get() : null
        );
        logger.info("Exiting TaskController fetchTaskById");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create-new-task")
    public ResponseEntity createNewTask(@RequestBody TaskMasterEo request) {
        logger.info("Inside TaskController createNewTask");
        Map<String, Object> task = taskService.createNewTask(request);
        boolean isTrue = (boolean) task.get("status");
        String message = isTrue ? "Task created successfully" : "Task not created";
        TaskMasterEo taskMasterEo = (TaskMasterEo) task.get("task");
        ApiResponse<TaskMasterEo> response = new ApiResponse<>(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                message,
                HttpStatus.OK.value(),
                taskMasterEo
        );
        logger.info("Exiting TaskController createNewTask");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/update-task/{taskId}")
    public ResponseEntity updateTask(@PathVariable String taskId, @RequestBody TaskMasterEo request) {
        logger.info("Inside TaskController updateTask");
        Optional<TaskMasterEo> existingTask = taskService.findByTaskIdAndStatusAndDeleted(taskId, Constants.pending, 'N');
        if (existingTask.isPresent()) {
            TaskMasterEo updatedTask = existingTask.get();

            updatedTask.setTask(request.getTask());
            updatedTask.setDescription(request.getDescription());
            updatedTask.setUpdatedBy(request.getUpdatedBy());
            updatedTask.setUpdatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
            taskService.updateTask(updatedTask);
            ApiResponse<String> response = new ApiResponse<>(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    "SUCCESS",
                    HttpStatus.OK.value(),
                    "Task updated successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<String> response = new ApiResponse<>(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    "FAILURE",
                    HttpStatus.NOT_FOUND.value(),
                    "No task found"
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/delete-task")
    public ResponseEntity deleteTask(@RequestBody TaskMasterEo request) {
        logger.info("Inside TaskController deleteTask");
        boolean exists = taskService.existsByTaskIdAndStatusAndDeleted(request.getTaskId());
        if (exists) {
            taskService.deleteTask(Constants.deleted, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), request.getDeleteBy(), request.getTaskId());
            ApiResponse<String> response = new ApiResponse<>(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    "SUCCESS",
                    HttpStatus.OK.value(),
                    "Task removed successfully"
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            ApiResponse<String> response = new ApiResponse<>(
                    LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                    "FAILURE",
                    HttpStatus.NOT_FOUND.value(),
                    "No task found"
            );
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

    }


}
