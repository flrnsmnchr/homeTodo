package net.simnacher.hometodo.dto;

import net.simnacher.hometodo.model.RecurrenceType;

import java.time.LocalDateTime;

public class CreateTaskRequest {

    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Long assignedUserId;
    private RecurrenceType recurrence;

    public CreateTaskRequest() {
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
        return "CreateTaskRequest{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", dueDate=" + dueDate +
                ", assignedUserId=" + assignedUserId +
                ", recurrence=" + recurrence +
                '}';
    }
}
