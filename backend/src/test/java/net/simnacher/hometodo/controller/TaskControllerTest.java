package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.dto.TaskDTO;
import net.simnacher.hometodo.model.TaskStatus;
import net.simnacher.hometodo.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

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
    void getAllTasks_ReturnsList() {
        when(taskService.getAllTasks()).thenReturn(List.of(testTask));

        ResponseEntity<List<TaskDTO>> response = taskController.getAllTasks(null, null);

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Task", response.getBody().get(0).getTitle());
    }

    @Test
    void getOpenTasks_ReturnsOpenTasks() {
        when(taskService.getOpenTasks()).thenReturn(List.of(testTask));

        ResponseEntity<List<TaskDTO>> response = taskController.getOpenTasks();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void deleteTask_ReturnsNoContent() {
        ResponseEntity<Void> response = taskController.deleteTask(1L);

        assertEquals(204, response.getStatusCode().value());
        verify(taskService).deleteTask(1L);
    }
}
