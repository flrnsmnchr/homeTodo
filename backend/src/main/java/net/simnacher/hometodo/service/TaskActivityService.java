package net.simnacher.hometodo.service;

import net.simnacher.hometodo.model.Task;
import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.TaskActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskActivityService {

    private final TaskActivityRepository activityRepository;

    public TaskActivityService(TaskActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Transactional
    public void logActivity(Task task, String action, User user, String details) {
        net.simnacher.hometodo.model.TaskActivityLog log = new net.simnacher.hometodo.model.TaskActivityLog();
        log.setTaskId(task.getId());
        log.setAction(action);
        log.setUser(user);
        log.setTimestamp(LocalDateTime.now());
        log.setDetails(details);
        
        activityRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getActivityLogForTask(Long taskId) {
        return activityRepository.findByTaskIdOrderByTimestampDesc(taskId).stream()
                .map(log -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", log.getId());
                    map.put("taskId", log.getTaskId());
                    map.put("action", log.getAction());
                    map.put("userId", log.getUser() != null ? log.getUser().getId() : null);
                    map.put("userName", log.getUser() != null ? log.getUser().getName() : null);
                    map.put("timestamp", log.getTimestamp());
                    map.put("details", log.getDetails());
                    return map;
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllActivityLogs() {
        return activityRepository.findAllByOrderByTimestampDesc().stream()
                .map(log -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", log.getId());
                    map.put("taskId", log.getTaskId());
                    map.put("action", log.getAction());
                    map.put("userId", log.getUser() != null ? log.getUser().getId() : null);
                    map.put("userName", log.getUser() != null ? log.getUser().getName() : null);
                    map.put("timestamp", log.getTimestamp());
                    map.put("details", log.getDetails());
                    return map;
                })
                .toList();
    }
}
