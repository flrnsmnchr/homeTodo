package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.dto.CreateTaskRequest;
import net.simnacher.hometodo.dto.TaskDTO;
import net.simnacher.hometodo.dto.UpdateTaskRequest;
import net.simnacher.hometodo.model.TaskStatus;
import net.simnacher.hometodo.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Long assignedUserId) {
        
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
