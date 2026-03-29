package net.simnacher.hometodo.dto;

import java.time.LocalDateTime;

public class KudosDTO {
    private Long id;
    private Long activityId;
    private String taskTitle;
    private String giverName;
    private LocalDateTime createdAt;

    public KudosDTO(Long id, Long activityId, String taskTitle, String giverName, LocalDateTime createdAt) {
        this.id = id;
        this.activityId = activityId;
        this.taskTitle = taskTitle;
        this.giverName = giverName;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getActivityId() { return activityId; }
    public void setActivityId(Long activityId) { this.activityId = activityId; }
    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }
    public String getGiverName() { return giverName; }
    public void setGiverName(String giverName) { this.giverName = giverName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
