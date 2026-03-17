package net.simnacher.hometodo.dto;

import net.simnacher.hometodo.model.RecurrenceType;
import net.simnacher.hometodo.model.TaskStatus;

import java.time.LocalDateTime;

public class UpdateTaskRequest {

    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime dueDate;
    private Long assignedUserId;
    private RecurrenceType recurrence;

    public UpdateTaskRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public RecurrenceType getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(RecurrenceType recurrence) {
        this.recurrence = recurrence;
    }

    @Override
    public String toString() {
        return "UpdateTaskRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", dueDate=" + dueDate +
                ", assignedUserId=" + assignedUserId +
                ", recurrence=" + recurrence +
                '}';
    }
}
