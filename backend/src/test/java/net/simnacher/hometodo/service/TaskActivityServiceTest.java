package net.simnacher.hometodo.service;

import net.simnacher.hometodo.model.Task;
import net.simnacher.hometodo.model.TaskActivityLog;
import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.KudosRepository;
import net.simnacher.hometodo.repository.TaskActivityRepository;
import net.simnacher.hometodo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskActivityServiceTest {

    @Mock
    private TaskActivityRepository activityRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private KudosRepository kudosRepository;

    @InjectMocks
    private TaskActivityService taskActivityService;

    private User testUser;
    private Task testTask;
    private TaskActivityLog testLog;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe");
        testUser.setId(1L);

        testTask = new Task();
        testTask.setId(10L);
        testTask.setTitle("Test Task");

        testLog = new TaskActivityLog();
        testLog.setId(100L);
        testLog.setTaskId(10L);
        testLog.setAction("CREATED");
        testLog.setUser(testUser);
        testLog.setTimestamp(LocalDateTime.now());
        testLog.setDetails("Task created");
    }

    @Test
    void logActivity_SavesLog() {
        taskActivityService.logActivity(testTask, "UPDATED", testUser, "Updated details");

        verify(activityRepository).save(any(TaskActivityLog.class));
    }

    @Test
    void getActivityLogForTask_ReturnsMappedList() {
        when(activityRepository.findByTaskIdOrderByTimestampDesc(10L)).thenReturn(List.of(testLog));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(testTask));

        List<Map<String, Object>> result = taskActivityService.getActivityLogForTask(10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CREATED", result.get(0).get("action"));
        assertEquals("John Doe", result.get(0).get("userName"));
        assertEquals(1L, result.get(0).get("userId"));
        assertEquals("Test Task", result.get(0).get("taskTitle"));
    }

    @Test
    void getAllActivityLogs_ReturnsMappedList() {
        when(activityRepository.findAllByOrderByTimestampDesc()).thenReturn(List.of(testLog));
        when(taskRepository.findById(10L)).thenReturn(Optional.of(testTask));

        List<Map<String, Object>> result = taskActivityService.getAllActivityLogs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CREATED", result.get(0).get("action"));
        assertEquals("Test Task", result.get(0).get("taskTitle"));
    }
}
