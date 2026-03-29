package net.simnacher.hometodo.service;

import net.simnacher.hometodo.dto.KudosDTO;
import net.simnacher.hometodo.model.Kudos;
import net.simnacher.hometodo.model.TaskActivityLog;
import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.KudosRepository;
import net.simnacher.hometodo.repository.TaskActivityRepository;
import net.simnacher.hometodo.repository.TaskRepository;
import net.simnacher.hometodo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KudosService {

    private final KudosRepository kudosRepository;
    private final TaskActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public KudosService(KudosRepository kudosRepository, 
                        TaskActivityRepository activityRepository, 
                        UserRepository userRepository,
                        TaskRepository taskRepository) {
        this.kudosRepository = kudosRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void giveKudo(Long activityId, Long giverId) {
        TaskActivityLog activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new RuntimeException("Activity not found"));

        if (activity.getUser() == null) {
            throw new RuntimeException("Cannot give kudos to system activity");
        }

        if (activity.getUser().getId().equals(giverId)) {
            throw new RuntimeException("Cannot give kudos to your own activity");
        }

        if (kudosRepository.existsByActivityIdAndGiverId(activityId, giverId)) {
            return; // Already gave kudo
        }

        User giver = userRepository.findById(giverId)
                .orElseThrow(() -> new RuntimeException("Giver user not found"));

        Kudos kudos = new Kudos();
        kudos.setActivity(activity);
        kudos.setGiver(giver);
        kudos.setReceiver(activity.getUser());
        
        kudosRepository.save(kudos);
    }

    @Transactional(readOnly = true)
    public List<KudosDTO> getUnseenKudos(Long userId) {
        return kudosRepository.findByReceiverIdAndSeenFalse(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markKudosSeen(Long userId) {
        List<Kudos> unseen = kudosRepository.findByReceiverIdAndSeenFalse(userId);
        unseen.forEach(k -> k.setSeen(true));
        kudosRepository.saveAll(unseen);
    }

    private KudosDTO toDTO(Kudos k) {
        String title = taskRepository.findById(k.getActivity().getTaskId())
                .map(t -> t.getTitle())
                .orElse("Deleted Task");
        
        return new KudosDTO(
                k.getId(),
                k.getActivity().getId(),
                title,
                k.getGiver().getName(),
                k.getCreatedAt()
        );
    }
}
