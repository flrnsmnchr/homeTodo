package net.simnacher.hometodo.controller;

import net.simnacher.hometodo.dto.KudosDTO;
import net.simnacher.hometodo.service.KudosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kudos")
public class KudosController {

    private final KudosService kudosService;

    public KudosController(KudosService kudosService) {
        this.kudosService = kudosService;
    }

    @PostMapping("/{activityId}")
    public ResponseEntity<Void> giveKudo(@PathVariable Long activityId, @RequestParam Long userId) {
        kudosService.giveKudo(activityId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unseen")
    public ResponseEntity<List<KudosDTO>> getUnseenKudos(@RequestParam Long userId) {
        return ResponseEntity.ok(kudosService.getUnseenKudos(userId));
    }

    @PostMapping("/mark-seen")
    public ResponseEntity<Void> markKudosSeen(@RequestParam Long userId) {
        kudosService.markKudosSeen(userId);
        return ResponseEntity.ok().build();
    }
}
