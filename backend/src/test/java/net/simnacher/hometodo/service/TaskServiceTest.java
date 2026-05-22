package net.simnacher.hometodo.service;

import net.simnacher.hometodo.dto.CreateTaskRequest;
import net.simnacher.hometodo.dto.TaskDTO;
import net.simnacher.hometodo.dto.UpdateTaskRequest;
import net.simnacher.hometodo.model.RecurrenceType;
import net.simnacher.hometodo.model.Task;
import net.simnacher.hometodo.model.TaskStatus;
import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.TaskRepository;
import net.simnacher.hometodo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private User assignedUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe");
        testUser.setId(1L);

        assignedUser = new User("Jane Doe");
        assignedUser.setId(2L);

        testTask = createTaskEntity(100L, "Test Task", testUser, assignedUser, TaskStatus.OPEN);
    }

    @Test
    void getAllTasks_ReturnsMappedTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(testTask));

        List<TaskDTO> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertTaskDtoMatchesTask(result.get(0), testTask);
        verify(taskRepository).findAll();
    }

    @Test
    void getTasksByStatus_ReturnsMappedTasks() {
        when(taskRepository.findByStatus(TaskStatus.OPEN)).thenReturn(List.of(testTask));

        List<TaskDTO> result = taskService.getTasksByStatus(TaskStatus.OPEN);

        assertEquals(1, result.size());
        assertTaskDtoMatchesTask(result.get(0), testTask);
        verify(taskRepository).findByStatus(TaskStatus.OPEN);
    }

    @Test
    void getTasksAssignedToUser_ReturnsMappedTasks() {
        when(taskRepository.findByAssignedUserId(2L)).thenReturn(List.of(testTask));

        List<TaskDTO> result = taskService.getTasksAssignedToUser(2L);

        assertEquals(1, result.size());
        assertTaskDtoMatchesTask(result.get(0), testTask);
        verify(taskRepository).findByAssignedUserId(2L);
    }

    @Test
    void getTasksByStatusAndAssignedUser_ReturnsMappedTasks() {
        when(taskRepository.findByStatusAndAssignedUserId(TaskStatus.OPEN, 2L)).thenReturn(List.of(testTask));

        List<TaskDTO> result = taskService.getTasksByStatusAndAssignedUser(TaskStatus.OPEN, 2L);

        assertEquals(1, result.size());
        assertTaskDtoMatchesTask(result.get(0), testTask);
        verify(taskRepository).findByStatusAndAssignedUserId(TaskStatus.OPEN, 2L);
    }

    @Test
    void getOpenTasks_DelegatesToStatusQuery() {
        when(taskRepository.findByStatus(TaskStatus.OPEN)).thenReturn(List.of(testTask));

        List<TaskDTO> result = taskService.getOpenTasks();

        assertEquals(1, result.size());
        assertEquals(TaskStatus.OPEN, result.get(0).getStatus());
        verify(taskRepository).findByStatus(TaskStatus.OPEN);
    }

    @Test
    void getCompletedTasks_DelegatesToStatusQuery() {
        testTask.setStatus(TaskStatus.COMPLETED);
        when(taskRepository.findByStatus(TaskStatus.COMPLETED)).thenReturn(List.of(testTask));

        List<TaskDTO> result = taskService.getCompletedTasks();

        assertEquals(1, result.size());
        assertEquals(TaskStatus.COMPLETED, result.get(0).getStatus());
        verify(taskRepository).findByStatus(TaskStatus.COMPLETED);
    }

    @Test
    void createTask_Success() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");
        request.setDescription("Description");
        request.setDueDate(LocalDateTime.of(2025, 1, 2, 10, 30));
        request.setAssignedUserId(2L);
        request.setRecurrence(RecurrenceType.WEEKLY);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignedUser));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(captor.capture())).thenAnswer(invocation -> {
            Task saved = invocation.getArgument(0);
            saved.setId(200L);
            return saved;
        });

        TaskDTO result = taskService.createTask(request, 1L);

        Task savedTask = captor.getValue();
        assertEquals("New Task", savedTask.getTitle());
        assertEquals("Description", savedTask.getDescription());
        assertEquals(request.getDueDate(), savedTask.getDueDate());
        assertEquals(assignedUser, savedTask.getAssignedUser());
        assertEquals(testUser, savedTask.getCreatedByUser());
        assertEquals(RecurrenceType.WEEKLY, savedTask.getRecurrence());
        assertEquals(200L, result.getId());
        verify(activityService).logActivity(savedTask, "CREATED", testUser, "Task created");
    }

    @Test
    void createTask_WithoutAssignee_Success() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Unassigned Task");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDTO result = taskService.createTask(request, 1L);

        assertNull(captor.getValue().getAssignedUser());
        assertNull(result.getAssignedUserId());
        verify(activityService).logActivity(captor.getValue(), "CREATED", testUser, "Task created");
    }

    @Test
    void createTask_ThrowsException_WhenCreatorNotFound() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.createTask(request, 1L));
        verify(taskRepository, never()).save(any(Task.class));
        verify(activityService, never()).logActivity(any(Task.class), any(), any(), any());
    }

    @Test
    void createTask_ThrowsException_WhenAssignedUserNotFound() {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("New Task");
        request.setAssignedUserId(2L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.createTask(request, 1L));
        verify(taskRepository, never()).save(any(Task.class));
        verify(activityService, never()).logActivity(any(Task.class), any(), any(), any());
    }

    @Test
    void updateTask_Success() {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle("Updated Task");
        request.setDescription("Updated description");
        request.setDueDate(LocalDateTime.of(2025, 2, 1, 11, 45));
        request.setStatus(TaskStatus.COMPLETED);
        request.setAssignedUserId(2L);
        request.setRecurrence(RecurrenceType.MONTHLY);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(assignedUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDTO result = taskService.updateTask(100L, request, 1L);

        assertEquals("Updated Task", testTask.getTitle());
        assertEquals("Updated description", testTask.getDescription());
        assertEquals(request.getDueDate(), testTask.getDueDate());
        assertEquals(TaskStatus.COMPLETED, testTask.getStatus());
        assertEquals(assignedUser, testTask.getAssignedUser());
        assertEquals(RecurrenceType.MONTHLY, testTask.getRecurrence());
        assertEquals("Updated Task", result.getTitle());
        verify(activityService).logActivity(testTask, "UPDATED", testUser, "Task updated");
    }

    @Test
    void updateTask_PreservesTitleWhenNullAndClearsNullableFields() {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setTitle(null);
        request.setDescription(null);
        request.setDueDate(null);
        request.setAssignedUserId(null);
        request.setRecurrence(null);
        request.setStatus(null);

        testTask.setRecurrence(RecurrenceType.DAILY);
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        taskService.updateTask(100L, request, 1L);

        assertEquals("Test Task", testTask.getTitle());
        assertNull(testTask.getDescription());
        assertNull(testTask.getDueDate());
        assertNull(testTask.getAssignedUser());
        assertNull(testTask.getRecurrence());
        assertEquals(TaskStatus.OPEN, testTask.getStatus());
    }

    @Test
    void updateTask_ThrowsException_WhenTaskNotFound() {
        when(taskRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.updateTask(100L, new UpdateTaskRequest(), 1L));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_ThrowsException_WhenUserNotFound() {
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.updateTask(100L, new UpdateTaskRequest(), 1L));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void updateTask_ThrowsException_WhenAssignedUserNotFound() {
        UpdateTaskRequest request = new UpdateTaskRequest();
        request.setAssignedUserId(2L);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.updateTask(100L, request, 1L));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void completeTask_NotAssigned_AssignsAutomatically() {
        User otherUser = new User("Other User");
        otherUser.setId(3L);
        testTask.setAssignedUser(otherUser);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDTO result = taskService.completeTask(100L, 1L);

        assertEquals(TaskStatus.COMPLETED, result.getStatus());
        assertEquals(1L, result.getAssignedUserId());
    }

    @Test
    void completeTask_KeepsExistingAssignmentForSameUser() {
        testTask.setAssignedUser(testUser);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDTO result = taskService.completeTask(100L, 1L);

        assertEquals(1L, result.getAssignedUserId());
        assertEquals(testUser, testTask.getAssignedUser());
        verify(activityService).logActivity(testTask, "COMPLETED", testUser, "Task completed");
    }

    @Test
    void completeTask_ThrowsException_WhenTaskNotFound() {
        when(taskRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.completeTask(100L, 1L));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void completeTask_ThrowsException_WhenUserNotFound() {
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.completeTask(100L, 1L));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void completeTask_WithDailyRecurrence_CreatesNextTask() {
        assertRecurrenceCreatesExpectedTask(RecurrenceType.DAILY, LocalDateTime.of(2025, 1, 2, 10, 0));
    }

    @Test
    void completeTask_WithWeeklyRecurrence_CreatesNextTask() {
        assertRecurrenceCreatesExpectedTask(RecurrenceType.WEEKLY, LocalDateTime.of(2025, 1, 8, 10, 0));
    }

    @Test
    void completeTask_WithMonthlyRecurrence_CreatesNextTask() {
        assertRecurrenceCreatesExpectedTask(RecurrenceType.MONTHLY, LocalDateTime.of(2025, 2, 1, 10, 0));
    }

    @Test
    void completeTask_WithRecurrenceAndNullDueDate_UsesCurrentTime() {
        testTask.setAssignedUser(testUser);
        testTask.setDueDate(null);
        testTask.setRecurrence(RecurrenceType.DAILY);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(taskCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        LocalDateTime before = LocalDateTime.now();
        taskService.completeTask(100L, 1L);
        LocalDateTime after = LocalDateTime.now();

        List<Task> savedTasks = taskCaptor.getAllValues();
        Task recurringTask = savedTasks.get(1);
        assertNotNull(recurringTask.getDueDate());
        assertTrue(!recurringTask.getDueDate().isBefore(before.minusSeconds(1)));
        assertTrue(!recurringTask.getDueDate().isAfter(after.plusSeconds(1)));
    }

    @Test
    void uncompleteTask_Success() {
        testTask.setStatus(TaskStatus.COMPLETED);
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TaskDTO result = taskService.uncompleteTask(100L);

        assertEquals(TaskStatus.OPEN, result.getStatus());
        verify(activityService).logActivity(testTask, "UNCOMPLETED", null, "Task marked as open again");
    }

    @Test
    void uncompleteTask_ThrowsException_WhenTaskNotFound() {
        when(taskRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.uncompleteTask(100L));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));

        taskService.deleteTask(100L);

        verify(taskRepository).delete(testTask);
    }

    @Test
    void deleteTask_ThrowsException_WhenNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> taskService.deleteTask(999L));
    }

    private void assertRecurrenceCreatesExpectedTask(RecurrenceType recurrenceType, LocalDateTime expectedDueDate) {
        testTask.setAssignedUser(testUser);
        testTask.setDueDate(LocalDateTime.of(2025, 1, 1, 10, 0));
        testTask.setRecurrence(recurrenceType);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(testTask));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        when(taskRepository.save(taskCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        taskService.completeTask(100L, 1L);

        List<Task> savedTasks = taskCaptor.getAllValues();
        assertEquals(2, savedTasks.size());

        Task completedTask = savedTasks.get(0);
        Task recurringTask = savedTasks.get(1);

        assertEquals(TaskStatus.COMPLETED, completedTask.getStatus());
        assertEquals(testTask.getTitle(), recurringTask.getTitle());
        assertEquals(testTask.getDescription(), recurringTask.getDescription());
        assertEquals(expectedDueDate, recurringTask.getDueDate());
        assertEquals(recurrenceType, recurringTask.getRecurrence());
        assertEquals(testUser, recurringTask.getCreatedByUser());
        assertNull(recurringTask.getAssignedUser());
        verify(activityService).logActivity(testTask, "COMPLETED", testUser, "Task completed");
        verify(activityService).logActivity(recurringTask, "CREATED", testUser, "Recurring task created from completion");
    }

    private Task createTaskEntity(Long id, String title, User creator, User assignee, TaskStatus status) {
        Task task = new Task(title, "Description", LocalDateTime.of(2025, 1, 1, 10, 0), assignee, creator);
        task.setId(id);
        task.setStatus(status);
        task.setRecurrence(RecurrenceType.DAILY);
        task.setCreatedAt(LocalDateTime.of(2024, 12, 31, 12, 0));
        return task;
    }

    private void assertTaskDtoMatchesTask(TaskDTO dto, Task task) {
        assertEquals(task.getId(), dto.getId());
        assertEquals(task.getTitle(), dto.getTitle());
        assertEquals(task.getDescription(), dto.getDescription());
        assertEquals(task.getStatus(), dto.getStatus());
        assertEquals(task.getCreatedAt(), dto.getCreatedAt());
        assertEquals(task.getDueDate(), dto.getDueDate());
        assertEquals(task.getRecurrence(), dto.getRecurrence());
        assertEquals(task.getAssignedUser().getId(), dto.getAssignedUserId());
        assertEquals(task.getAssignedUser().getName(), dto.getAssignedUserName());
        assertEquals(task.getCreatedByUser().getId(), dto.getCreatedByUserId());
        assertEquals(task.getCreatedByUser().getName(), dto.getCreatedByUserName());
    }
}
