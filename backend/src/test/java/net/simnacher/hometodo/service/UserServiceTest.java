package net.simnacher.hometodo.service;
import net.simnacher.hometodo.dto.UserDTO;
import net.simnacher.hometodo.dto.UserStatisticsDTO;
import net.simnacher.hometodo.model.Task;
import net.simnacher.hometodo.model.TaskStatus;
import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.TaskRepository;
import net.simnacher.hometodo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe");
        testUser.setId(1L);
    }

    @Test
    void getUserStatistics_ReturnsCorrectCounts() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));
        when(taskRepository.countByAssignedUserId(1L)).thenReturn(5L);
        when(taskRepository.countByAssignedUserIdAndStatus(1L, TaskStatus.OPEN)).thenReturn(3L);
        when(taskRepository.countByAssignedUserIdAndStatus(1L, TaskStatus.COMPLETED)).thenReturn(2L);

        List<UserStatisticsDTO> stats = userService.getUserStatistics();

        assertEquals(1, stats.size());
        UserStatisticsDTO userStat = stats.get(0);
        assertEquals(1L, userStat.getUserId());
        assertEquals("John Doe", userStat.getUserName());
        assertEquals(5L, userStat.getTotalTasks());
        assertEquals(3L, userStat.getOpenTasks());
        assertEquals(2L, userStat.getCompletedTasks());
    }

    @Test
    void getAllUsers_ReturnsList() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    void getUserById_ThrowsException_WhenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(999L));
    }
}
