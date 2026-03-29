package net.simnacher.hometodo.service;

import net.simnacher.hometodo.dto.KudosDTO;
import net.simnacher.hometodo.model.Kudos;
import net.simnacher.hometodo.model.TaskActivityLog;
import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.KudosRepository;
import net.simnacher.hometodo.repository.TaskActivityRepository;
import net.simnacher.hometodo.repository.TaskRepository;
import net.simnacher.hometodo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KudosServiceTest {

    @Mock
    private KudosRepository kudosRepository;
    @Mock
    private TaskActivityRepository activityRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private KudosService kudosService;

    private User giver;
    private User receiver;
    private TaskActivityLog activity;

    @BeforeEach
    void setUp() {
        giver = new User("Giver");
        giver.setId(1L);
        
        receiver = new User("Receiver");
        receiver.setId(2L);
        
        activity = new TaskActivityLog();
        activity.setId(100L);
        activity.setUser(receiver);
    }

    @Test
    void giveKudo_Success() {
        when(activityRepository.findById(100L)).thenReturn(Optional.of(activity));
        when(userRepository.findById(1L)).thenReturn(Optional.of(giver));
        when(kudosRepository.existsByActivityIdAndGiverId(100L, 1L)).thenReturn(false);

        kudosService.giveKudo(100L, 1L);

        verify(kudosRepository).save(any(Kudos.class));
    }

    @Test
    void giveKudo_SameUser_ThrowsException() {
        activity.setUser(giver);
        when(activityRepository.findById(100L)).thenReturn(Optional.of(activity));

        assertThrows(RuntimeException.class, () -> kudosService.giveKudo(100L, 1L));
    }

    @Test
    void getUnseenKudos_ReturnsList() {
        Kudos k = new Kudos();
        k.setActivity(activity);
        k.setGiver(giver);
        
        when(kudosRepository.findByReceiverIdAndSeenFalse(2L)).thenReturn(List.of(k));
        when(taskRepository.findById(any())).thenReturn(Optional.empty());

        List<KudosDTO> result = kudosService.getUnseenKudos(2L);

        assertEquals(1, result.size());
        assertEquals("Giver", result.get(0).getGiverName());
    }
}
