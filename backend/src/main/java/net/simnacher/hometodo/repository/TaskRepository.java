package net.simnacher.hometodo.repository;

import net.simnacher.hometodo.model.Task;
import net.simnacher.hometodo.model.TaskStatus;
import net.simnacher.hometodo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByAssignedUser(User user);

    List<Task> findByAssignedUserId(Long userId);

    List<Task> findByStatusAndAssignedUserId(TaskStatus status, Long userId);

    List<Task> findByCreatedByUser(User user);
}
