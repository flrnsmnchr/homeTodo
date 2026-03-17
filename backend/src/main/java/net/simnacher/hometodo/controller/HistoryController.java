package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.service.TaskActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private static final Logger logger = LoggerFactory.getLogger(HistoryController.class);
    private final TaskActivityService activityService;

    public HistoryController(TaskActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllHistory() {
        logger.debug("Entering getAllHistory()");
        List<Map<String, Object>> history = activityService.getAllActivityLogs();
        logger.debug("Exiting getAllHistory() with {} records", history.size());
        return ResponseEntity.ok(history);
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Map<String, Object>>> getTaskHistory(@PathVariable Long taskId) {
        logger.debug("Entering getTaskHistory(taskId={})", taskId);
        List<Map<String, Object>> history = activityService.getActivityLogForTask(taskId);
        logger.debug("Exiting getTaskHistory(taskId={}) with {} records", taskId, history.size());
        return ResponseEntity.ok(history);
    }
}
