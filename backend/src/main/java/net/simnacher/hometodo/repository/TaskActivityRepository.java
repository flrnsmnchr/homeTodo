package net.simnacher.hometodo.repository;

import net.simnacher.hometodo.model.TaskActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskActivityRepository extends JpaRepository<TaskActivityLog, Long> {

    List<TaskActivityLog> findByTaskIdOrderByTimestampDesc(Long taskId);

    List<TaskActivityLog> findAllByOrderByTimestampDesc();
}
