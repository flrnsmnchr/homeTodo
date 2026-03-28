package net.simnacher.hometodo.dto;

public class UserStatisticsDTO {
    private Long userId;
    private String userName;
    private long totalTasks;
    private long openTasks;
    private long completedTasks;

    public UserStatisticsDTO(Long userId, String userName, long totalTasks, long openTasks, long completedTasks) {
        this.userId = userId;
        this.userName = userName;
        this.totalTasks = totalTasks;
        this.openTasks = openTasks;
        this.completedTasks = completedTasks;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public long getTotalTasks() { return totalTasks; }
    public void setTotalTasks(long totalTasks) { this.totalTasks = totalTasks; }
    public long getOpenTasks() { return openTasks; }
    public void setOpenTasks(long openTasks) { this.openTasks = openTasks; }
    public long getCompletedTasks() { return completedTasks; }
    public void setCompletedTasks(long completedTasks) { this.completedTasks = completedTasks; }
}
