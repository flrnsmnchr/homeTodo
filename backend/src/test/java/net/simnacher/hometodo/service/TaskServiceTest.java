package net.simnacher.hometodo.service;

import net.simnacher.hometodo.dto.CreateTaskRequest;
import net.simnacher.hometodo.dto.TaskDTO;
import net.simnacher.hometodo.model.*;
import net.simnacher.hometodo.repository.TaskRepository;
import net.simnacher.hometodo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskActivityService activityService;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe");
        testUser.setId(1L);

        testTask = new Task("Test Task", "Description", LocalDateTime.now().plusDays(1), testUser, testUser);
        testTask.setId(100L);
    }

    @Test
    void createTask_Success() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");
        request.setAssignedUserId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        TaskDTO result = taskService.createTask(request, 1L);

        assertNotNull(result);
        assertEquals(testTask.getId(), result.getId());
        verify(taskRepository).save(any(Task.class));
        verify(activityService).logActivity(eq(testTask), eq("CREATED"), eq(testUser), anyString());
    }

    @Test
    void completeTask_Success() {
        testTask.setStatus(TaskStatus.OPEN);
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDTO result = taskService.completeTask(100L, 1L);

        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        verify(activityService).logActivity(eq(testTask), eq("COMPLETED"), eq(testUser), anyString());
    }

    @Test
    void completeTask_WithRecurrence_CreatesNextTask() {
        testTask.setStatus(TaskStatus.OPEN);
        testTask.setRecurrence(RecurrenceType.DAILY);
        testTask.setDueDate(LocalDateTime.of(2025, 1, 1, 10, 0));

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        taskService.completeTask(100L, 1L);

        // Verify save was called twice: once for completing the task, once for the new recurring task
        verify(taskRepository, times(2)).save(any(Task.class));
        
        // Capture the new task and check its due date
        // (Alternatively, we could use ArgumentCaptor)
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(100L);

        verify(taskRepository).delete(testTask);
    }

    @Test
    void getTask_ThrowsException_WhenNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(999L));
    }
}
