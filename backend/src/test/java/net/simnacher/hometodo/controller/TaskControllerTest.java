package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.dto.CreateTaskRequest;
import net.simnacher.hometodo.dto.TaskDTO;
import net.simnacher.hometodo.dto.UpdateTaskRequest;
import net.simnacher.hometodo.model.RecurrenceType;
import net.simnacher.hometodo.model.TaskStatus;
import net.simnacher.hometodo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskDTO testTask;

    @BeforeEach
    void setUp() {
        testTask = new TaskDTO();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setStatus(TaskStatus.OPEN);
    }

    @Test
    void getAllTasks_WithoutFilters_ReturnsList() {
        when(taskService.getAllTasks()).thenReturn(List.of(testTask));

        ResponseEntity<List<TaskDTO>> response = taskController.getAllTasks(null, null);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Task", response.getBody().get(0).getTitle());
        verify(taskService).getAllTasks();
    }

    @Test
    void getAllTasks_WithStatusAndUser_ReturnsFilteredList() {
        when(taskService.getTasksByStatusAndAssignedUser(TaskStatus.OPEN, 1L)).thenReturn(List.of(testTask));

        ResponseEntity<List<TaskDTO>> response = taskController.getAllTasks(TaskStatus.OPEN, 1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(taskService).getTasksByStatusAndAssignedUser(TaskStatus.OPEN, 1L);
    }

    @Test
    void getAllTasks_WithStatusOnly_ReturnsFilteredList() {
        when(taskService.getTasksByStatus(TaskStatus.OPEN)).thenReturn(List.of(testTask));

        ResponseEntity<List<TaskDTO>> response = taskController.getAllTasks(TaskStatus.OPEN, null);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(taskService).getTasksByStatus(TaskStatus.OPEN);
    }

    @Test
    void getAllTasks_WithAssignedUserOnly_ReturnsFilteredList() {
        when(taskService.getTasksAssignedToUser(1L)).thenReturn(List.of(testTask));

        ResponseEntity<List<TaskDTO>> response = taskController.getAllTasks(null, 1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(taskService).getTasksAssignedToUser(1L);
    }

    @Test
    void getOpenTasks_ReturnsOpenTasks() {
        when(taskService.getOpenTasks()).thenReturn(List.of(testTask));

        ResponseEntity<List<TaskDTO>> response = taskController.getOpenTasks();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(taskService).getOpenTasks();
    }

    @Test
    void getCompletedTasks_ReturnsCompletedTasks() {
        testTask.setStatus(TaskStatus.COMPLETED);
        when(taskService.getCompletedTasks()).thenReturn(List.of(testTask));

        ResponseEntity<List<TaskDTO>> response = taskController.getCompletedTasks();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(TaskStatus.COMPLETED, response.getBody().get(0).getStatus());
        verify(taskService).getCompletedTasks();
    }

    @Test
    void getMyTasks_ReturnsTasksForUser() {
        when(taskService.getTasksAssignedToUser(1L)).thenReturn(List.of(testTask));

        ResponseEntity<List<TaskDTO>> response = taskController.getMyTasks(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(taskService).getTasksAssignedToUser(1L);
    }

    @Test
    void createTask_ReturnsCreatedTask() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");
        request.setDescription("Description");
        request.setDueDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        request.setAssignedUserId(1L);
        request.setRecurrence(RecurrenceType.WEEKLY);

        when(taskService.createTask(request, 1L)).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.createTask(request, 1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(testTask.getId(), response.getBody().getId());
        verify(taskService).createTask(request, 1L);
    }

    @Test
    void updateTask_ReturnsUpdatedTask() {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Task");
        request.setStatus(TaskStatus.COMPLETED);

        when(taskService.updateTask(1L, request, 2L)).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.updateTask(1L, request, 2L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(testTask.getId(), response.getBody().getId());
        verify(taskService).updateTask(1L, request, 2L);
    }

    @Test
    void deleteTask_ReturnsNoContent() {
        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(taskService).deleteTask(1L);
    }

    @Test
    void completeTask_ReturnsCompletedTask() {
        testTask.setStatus(TaskStatus.COMPLETED);
        when(taskService.completeTask(1L, 2L)).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.completeTask(1L, 2L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(TaskStatus.COMPLETED, response.getBody().getStatus());
        verify(taskService).completeTask(1L, 2L);
    }

    @Test
    void uncompleteTask_ReturnsOpenTask() {
        when(taskService.uncompleteTask(1L)).thenReturn(testTask);

        ResponseEntity<TaskDTO> response = taskController.uncompleteTask(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(testTask.getId(), response.getBody().getId());
        verify(taskService).uncompleteTask(1L);
    }
}
