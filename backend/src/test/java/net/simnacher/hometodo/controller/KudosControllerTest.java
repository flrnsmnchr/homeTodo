package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.dto.KudosDTO;
import net.simnacher.hometodo.service.KudosService;
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
class KudosControllerTest {

    @Mock
    private KudosService kudosService;

    @InjectMocks
    private KudosController kudosController;

    private KudosDTO testKudo;

    @BeforeEach
    void setUp() {
        testKudo = new KudosDTO(1L, 10L, "Test Task", "John Doe", LocalDateTime.of(2025, 1, 1, 10, 0));
    }

    @Test
    void giveKudo_ReturnsOk() {
        ResponseEntity<Void> response = kudosController.giveKudo(10L, 2L);

        assertEquals(200, response.getStatusCode().value());
        verify(kudosService).giveKudo(10L, 2L);
    }

    @Test
    void getUnseenKudos_ReturnsList() {
        when(kudosService.getUnseenKudos(2L)).thenReturn(List.of(testKudo));

        ResponseEntity<List<KudosDTO>> response = kudosController.getUnseenKudos(2L);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Task", response.getBody().get(0).getTaskTitle());
        verify(kudosService).getUnseenKudos(2L);
    }

    @Test
    void markKudosSeen_ReturnsOk() {
        ResponseEntity<Void> response = kudosController.markKudosSeen(2L);

        assertEquals(200, response.getStatusCode().value());
        verify(kudosService).markKudosSeen(2L);
    }
}
