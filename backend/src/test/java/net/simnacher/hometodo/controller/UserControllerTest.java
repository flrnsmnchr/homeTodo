package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.dto.UserDTO;
import net.simnacher.hometodo.dto.UserStatisticsDTO;
import net.simnacher.hometodo.service.UserService;
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
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO testUser;
    private UserStatisticsDTO testStats;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO(1L, "John Doe");
        testStats = new UserStatisticsDTO(1L, "John Doe", 5L, 2L, 3L);
    }

    @Test
    void getAllUsers_ReturnsList() {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getName());
        verify(userService).getAllUsers();
    }

    @Test
    void getUserStatistics_ReturnsList() {
        when(userService.getUserStatistics()).thenReturn(List.of(testStats));

        ResponseEntity<List<UserStatisticsDTO>> response = userController.getUserStatistics();

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(5L, response.getBody().get(0).getTotalTasks());
        verify(userService).getUserStatistics();
    }

    @Test
    void getUserById_ReturnsUser() {
        when(userService.getUserById(1L)).thenReturn(testUser);

        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        verify(userService).getUserById(1L);
    }
}
