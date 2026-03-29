package net.simnacher.hometodo.service;

import net.simnacher.hometodo.model.Task;
import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.KudosRepository;
import net.simnacher.hometodo.repository.TaskActivityRepository;
import net.simnacher.hometodo.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskActivityService {

    private final TaskActivityRepository activityRepository;
    private final TaskRepository taskRepository;
    private final KudosRepository kudosRepository;

    public TaskActivityService(TaskActivityRepository activityRepository, 
                               TaskRepository taskRepository,
                               KudosRepository kudosRepository) {
        this.activityRepository = activityRepository;
        this.taskRepository = taskRepository;
        this.kudosRepository = kudosRepository;
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
                    map.put("taskTitle", taskRepository.findById(log.getTaskId()).map(Task::getTitle).orElse("Deleted Task"));
                    map.put("action", log.getAction());
                    map.put("userId", log.getUser() != null ? log.getUser().getId() : null);
                    map.put("userName", log.getUser() != null ? log.getUser().getName() : null);
                    map.put("timestamp", log.getTimestamp());
                    map.put("details", log.getDetails());
                    map.put("kudosCount", kudosRepository.countByActivityId(log.getId()));
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
                    map.put("taskTitle", taskRepository.findById(log.getTaskId()).map(Task::getTitle).orElse("Deleted Task"));
                    map.put("action", log.getAction());
                    map.put("userId", log.getUser() != null ? log.getUser().getId() : null);
                    map.put("userName", log.getUser() != null ? log.getUser().getName() : null);
                    map.put("timestamp", log.getTimestamp());
                    map.put("details", log.getDetails());
                    map.put("kudosCount", kudosRepository.countByActivityId(log.getId()));
                    return map;
                })
                .toList();
    }
}
