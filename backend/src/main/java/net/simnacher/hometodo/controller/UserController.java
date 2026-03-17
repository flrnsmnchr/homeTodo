package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.dto.UserDTO;
import net.simnacher.hometodo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        logger.debug("Entering getAllUsers()");
        List<UserDTO> users = userService.getAllUsers();
        logger.debug("Exiting getAllUsers() with {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        logger.debug("Entering getUserById(id={})", id);
        UserDTO user = userService.getUserById(id);
        logger.debug("Exiting getUserById(id={}) with user: {}", id, user);
        return ResponseEntity.ok(user);
    }
}
