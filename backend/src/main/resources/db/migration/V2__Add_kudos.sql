CREATE TABLE kudos (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    activity_id INTEGER NOT NULL,
    giver_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    seen BOOLEAN NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (activity_id) REFERENCES task_activity_log(id),
    FOREIGN KEY (giver_id) REFERENCES users(id),
    FOREIGN KEY (receiver_id) REFERENCES users(id)
);
