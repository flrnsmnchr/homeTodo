package net.simnacher.hometodo.service;

import net.simnacher.hometodo.dto.UserDTO;
import net.simnacher.hometodo.dto.UserStatisticsDTO;
import net.simnacher.hometodo.model.TaskStatus;
import net.simnacher.hometodo.model.User;
import net.simnacher.hometodo.repository.TaskRepository;
import net.simnacher.hometodo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public UserService(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional(readOnly = true)
    public List<UserStatisticsDTO> getUserStatistics() {
        return userRepository.findAll().stream().map(user -> {
            long total = taskRepository.countByAssignedUserId(user.getId());
            long open = taskRepository.countByAssignedUserIdAndStatus(user.getId(), TaskStatus.OPEN);
            long completed = taskRepository.countByAssignedUserIdAndStatus(user.getId(), TaskStatus.COMPLETED);
            return new UserStatisticsDTO(user.getId(), user.getName(), total, open, completed);
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toDTO(user);
    }

    private UserDTO toDTO(User user) {
        return new UserDTO(user.getId(), user.getName());
    }
}
