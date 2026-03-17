package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.dto.CreateTaskRequest;
import net.simnacher.hometodo.dto.TaskDTO;
import net.simnacher.hometodo.dto.UpdateTaskRequest;
import net.simnacher.hometodo.model.TaskStatus;
import net.simnacher.hometodo.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long assignedUserId) {
        logger.debug("Entering getAllTasks(status={}, assignedUserId={})", status, assignedUserId);
        
        List<TaskDTO> result;
        if (status != null && assignedUserId != null) {
            result = taskService.getTasksByStatus(status).stream()
                    .filter(t -> assignedUserId.equals(t.getAssignedUserId()))
                    .toList();
        } else if (status != null) {
            result = taskService.getTasksByStatus(status);
        } else if (assignedUserId != null) {
            result = taskService.getTasksAssignedToUser(assignedUserId);
        } else {
            result = taskService.getAllTasks();
        }
        
        logger.debug("Exiting getAllTasks() with {} tasks", result.size());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/open")
    public ResponseEntity<List<TaskDTO>> getOpenTasks() {
        logger.debug("Entering getOpenTasks()");
        List<TaskDTO> tasks = taskService.getOpenTasks();
        logger.debug("Exiting getOpenTasks() with {} tasks", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TaskDTO>> getCompletedTasks() {
        logger.debug("Entering getCompletedTasks()");
        List<TaskDTO> tasks = taskService.getCompletedTasks();
        logger.debug("Exiting getCompletedTasks() with {} tasks", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/my-tasks/{userId}")
    public ResponseEntity<List<TaskDTO>> getMyTasks(@PathVariable Long userId) {
        logger.debug("Entering getMyTasks(userId={})", userId);
        List<TaskDTO> tasks = taskService.getTasksAssignedToUser(userId);
        logger.debug("Exiting getMyTasks(userId={}) with {} tasks", userId, tasks.size());
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskDTO> createTask(
            @RequestBody CreateTaskRequest request,
            @RequestParam Long createdByUserId) {
        logger.debug("Entering createTask(request={}, createdByUserId={})", request, createdByUserId);
        TaskDTO task = taskService.createTask(request, createdByUserId);
        logger.debug("Exiting createTask() with created task: {}", task);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDTO> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest request,
            @RequestParam Long userId) {
        logger.debug("Entering updateTask(id={}, request={}, userId={})", id, request, userId);
        TaskDTO task = taskService.updateTask(id, request, userId);
        logger.debug("Exiting updateTask() with updated task: {}", task);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        logger.debug("Entering deleteTask(id={})", id);
        taskService.deleteTask(id);
        logger.debug("Exiting deleteTask(id={})", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<TaskDTO> completeTask(
            @PathVariable Long id,
            @RequestParam Long userId) {
        logger.debug("Entering completeTask(id={}, userId={})", id, userId);
        TaskDTO task = taskService.completeTask(id, userId);
        logger.debug("Exiting completeTask() with completed task: {}", task);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/{id}/uncomplete")
    public ResponseEntity<TaskDTO> uncompleteTask(@PathVariable Long id) {
        logger.debug("Entering uncompleteTask(id={})", id);
        TaskDTO task = taskService.uncompleteTask(id);
        logger.debug("Exiting uncompleteTask() with uncompleted task: {}", task);
        return ResponseEntity.ok(task);
    }
}
