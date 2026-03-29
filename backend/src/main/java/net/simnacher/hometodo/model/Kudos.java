package net.simnacher.hometodo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "kudos")
public class Kudos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private TaskActivityLog activity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "giver_id", nullable = false)
    private User giver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(nullable = false)
    private boolean seen = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Kudos() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TaskActivityLog getActivity() { return activity; }
    public void setActivity(TaskActivityLog activity) { this.activity = activity; }

    public User getGiver() { return giver; }
    public void setGiver(User giver) { this.giver = giver; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public boolean isSeen() { return seen; }
    public void setSeen(boolean seen) { this.seen = seen; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
