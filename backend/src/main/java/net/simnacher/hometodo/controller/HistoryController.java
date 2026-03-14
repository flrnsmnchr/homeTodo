package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.service.TaskActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final TaskActivityService activityService;

    public HistoryController(TaskActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllHistory() {
        return ResponseEntity.ok(activityService.getAllActivityLogs());
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<Map<String, Object>>> getTaskHistory(@PathVariable Long taskId) {
        return ResponseEntity.ok(activityService.getActivityLogForTask(taskId));
    }
}
