package net.simnacher.hometodo.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.simnacher.hometodo.config.DataLoader;
import net.simnacher.hometodo.dto.CreateTaskRequest;
import net.simnacher.hometodo.dto.TaskDTO;
import net.simnacher.hometodo.dto.UpdateTaskRequest;
import net.simnacher.hometodo.model.TaskStatus;
import net.simnacher.hometodo.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long assignedUserId) {

        logger.info("getAllTasks {} {}", assignedUserId, status);
        
        if (status != null && assignedUserId != null) {
            return ResponseEntity.ok(taskService.getTasksByStatus(status).stream()
                    .filter(t -> assignedUserId.equals(t.getAssignedUserId()))
                    .toList());
        }
        
        if (status != null) {
            return ResponseEntity.ok(taskService.getTasksByStatus(status));
        }
        
        if (assignedUserId != null) {
            return ResponseEntity.ok(taskService.getTasksAssignedToUser(assignedUserId));
        }
        
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/open")
    public ResponseEntity<List<TaskDTO>> getOpenTasks() {
        logger.info("getOpenTasks");
        return ResponseEntity.ok(taskService.getOpenTasks());
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TaskDTO>> getCompletedTasks() {
        return ResponseEntity.ok(taskService.getCompletedTasks());
    }

    @GetMapping("/my-tasks/{userId}")
    public ResponseEntity<List<TaskDTO>> getMyTasks(@PathVariable Long userId) {
        return ResponseEntity.ok(taskService.getTasksAssignedToUser(userId));
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @RequestBody CreateTaskRequest request,
            @RequestParam Long createdByUserId) {
        return ResponseEntity.ok(taskService.createTask(request, createdByUserId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request,
            @RequestParam Long userId) {
        return ResponseEntity.ok(taskService.updateTask(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskDTO> completeTask(
            @PathVariable Long id,
            @RequestParam Long userId) {
        return ResponseEntity.ok(taskService.completeTask(id, userId));
    }

    @PostMapping("/{id}/uncomplete")
    public ResponseEntity<TaskDTO> uncompleteTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.uncompleteTask(id));
    }
}
