package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.dto.UserDTO;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDTO testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserDTO(1L, "John Doe");
    }

    @Test
    void getAllUsers_ReturnsList() {
        when(userService.getAllUsers()).thenReturn(List.of(testUser));

        ResponseEntity<List<UserDTO>> response = userController.getAllUsers();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("John Doe", response.getBody().get(0).getName());
    }

    @Test
    void getUserById_ReturnsUser() {
        when(userService.getUserById(1L)).thenReturn(testUser);

        ResponseEntity<UserDTO> response = userController.getUserById(1L);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }
}
