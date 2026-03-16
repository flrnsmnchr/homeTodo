package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.service.TaskActivityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HistoryControllerTest {

    @Mock
    private TaskActivityService activityService;

    @InjectMocks
    private HistoryController historyController;

    @Test
    void getAllHistory_ReturnsList() {
        Map<String, Object> log = Map.of("action", "CREATED");
        when(activityService.getAllActivityLogs()).thenReturn(List.of(log));

        ResponseEntity<List<Map<String, Object>>> response = historyController.getAllHistory();

        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("CREATED", response.getBody().get(0).get("action"));
    }

    @Test
    void getTaskHistory_ReturnsList() {
        Map<String, Object> log = Map.of("taskId", 10L);
        when(activityService.getActivityLogForTask(10L)).thenReturn(List.of(log));

        ResponseEntity<List<Map<String, Object>>> response = historyController.getTaskHistory(10L);

        assertNotNull(response.getBody());
        assertEquals(10L, response.getBody().get(0).get("taskId"));
    }
}
