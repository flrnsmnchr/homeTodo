package net.simnacher.hometodo.repository;

import net.simnacher.hometodo.model.Kudos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KudosRepository extends JpaRepository<Kudos, Long> {
    List<Kudos> findByReceiverIdAndSeenFalse(Long receiverId);
    long countByActivityId(Long activityId);
    boolean existsByActivityIdAndGiverId(Long activityId, Long giverId);
}
