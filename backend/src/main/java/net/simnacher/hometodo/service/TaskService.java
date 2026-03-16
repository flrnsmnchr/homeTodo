package net.simnacher.hometodo.service;

import net.simnacher.hometodo.dto.CreateTaskRequest;
import net.simnacher.hometodo.dto.TaskDTO;
import net.simnacher.hometodo.dto.UpdateTaskRequest;
import net.simnacher.hometodo.model.*;
import net.simnacher.hometodo.repository.TaskRepository;
import net.simnacher.hometodo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskActivityService activityService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskActivityService activityService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.activityService = activityService;
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks() {
        return taskRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksAssignedToUser(Long userId) {
        return taskRepository.findByAssignedUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getOpenTasks() {
        return getTasksByStatus(TaskStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getCompletedTasks() {
        return getTasksByStatus(TaskStatus.COMPLETED);
    }

    @Transactional
    public TaskDTO createTask(CreateTaskRequest request, Long createdByUserId) {
        User createdBy = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + createdByUserId));

        User assignedUser = null;
        if (request.getAssignedUserId() != null) {
            assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
        }

        Task task = new Task(
                request.getTitle(),
                request.getDescription(),
                request.getDueDate(),
                assignedUser,
                createdBy
        );
        task.setRecurrence(request.getRecurrence());

        Task saved = taskRepository.save(task);
        activityService.logActivity(saved, "CREATED", createdBy, "Task created");

        return toDTO(saved);
    }

    @Transactional
    public TaskDTO updateTask(Long id, UpdateTaskRequest request, Long userId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getAssignedUserId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedUserId())
                    .orElseThrow(() -> new RuntimeException("Assigned user not found"));
            task.setAssignedUser(assignedUser);
        }
        if (request.getRecurrence() != null) {
            task.setRecurrence(request.getRecurrence());
        }

        Task saved = taskRepository.save(task);
        activityService.logActivity(saved, "UPDATED", user, "Task updated");

        return toDTO(saved);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));
        taskRepository.delete(task);
    }

    @Transactional
    public TaskDTO completeTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setStatus(TaskStatus.COMPLETED);
        Task saved = taskRepository.save(task);

        activityService.logActivity(task, "COMPLETED", user, "Task completed");

        // Handle recurring tasks
        if (task.getRecurrence() != null) {
            createNextRecurrence(task, user);
        }

        return toDTO(saved);
    }

    @Transactional
    public TaskDTO uncompleteTask(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        task.setStatus(TaskStatus.OPEN);
        Task saved = taskRepository.save(task);

        activityService.logActivity(task, "UNCOMPLETED", null, "Task marked as open again");

        return toDTO(saved);
    }

    private void createNextRecurrence(Task completedTask, User user) {
        LocalDateTime nextDueDate = calculateNextDueDate(completedTask.getDueDate(), completedTask.getRecurrence());

        Task newTask = new Task(
                completedTask.getTitle(),
                completedTask.getDescription(),
                nextDueDate,
                null,
                user
        );
        newTask.setRecurrence(completedTask.getRecurrence());

        taskRepository.save(newTask);
        activityService.logActivity(newTask, "CREATED", user, "Recurring task created from completion");
    }

    private LocalDateTime calculateNextDueDate(LocalDateTime currentDueDate, RecurrenceType recurrence) {
        if (currentDueDate == null) {
            return LocalDateTime.now();
        }

        return switch (recurrence) {
            case DAILY -> currentDueDate.plusDays(1);
            case WEEKLY -> currentDueDate.plusWeeks(1);
            case MONTHLY -> currentDueDate.plusMonths(1);
        };
    }

    private TaskDTO toDTO(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setDueDate(task.getDueDate());
        dto.setRecurrence(task.getRecurrence());

        if (task.getAssignedUser() != null) {
            dto.setAssignedUserId(task.getAssignedUser().getId());
            dto.setAssignedUserName(task.getAssignedUser().getName());
        }

        if (task.getCreatedByUser() != null) {
            dto.setCreatedByUserId(task.getCreatedByUser().getId());
            dto.setCreatedByUserName(task.getCreatedByUser().getName());
        }

        return dto;
    }
}
