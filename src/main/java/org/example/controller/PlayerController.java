package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Hero;
import org.example.domain.entity.Player;
import org.example.dto.CreatePlayerRequest;
import org.example.dto.HeroSummary;
import org.example.dto.PlayerResponse;
import org.example.service.HeroService;
import org.example.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Player.
 */
@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;
    private final HeroService heroService;

    /**
     * Tạo player mới.
     * POST /api/players
     */
    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        Player player = playerService.createPlayer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PlayerResponse.from(player));
    }

    /**
     * Lấy player theo ID.
     * GET /api/players/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayer(@PathVariable Long id) {
        Player player = playerService.getPlayer(id);
        return ResponseEntity.ok(PlayerResponse.from(player));
    }

    /**
     * Lấy tất cả heroes của player.
     * GET /api/players/{id}/heroes
     */
    @GetMapping("/{id}/heroes")
    public ResponseEntity<List<HeroSummary>> getPlayerHeroes(@PathVariable Long id) {
        List<Hero> heroes = heroService.getHeroesByPlayer(id);
        return ResponseEntity.ok(heroes.stream()
                .map(HeroSummary::from)
                .toList());
    }

    /**
     * Lấy tất cả players.
     * GET /api/players
     */
    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players.stream()
                .map(PlayerResponse::from)
                .toList());
    }

    /**
     * Cập nhật tên player.
     * PUT /api/players/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(@PathVariable Long id, 
                                                         @RequestParam String name) {
        Player player = playerService.updatePlayer(id, name);
        return ResponseEntity.ok(PlayerResponse.from(player));
    }

    /**
     * Tăng level player.
     * POST /api/players/{id}/levelup
     */
    @PostMapping("/{id}/levelup")
    public ResponseEntity<PlayerResponse> levelUp(@PathVariable Long id) {
        Player player = playerService.levelUp(id);
        return ResponseEntity.ok(PlayerResponse.from(player));
    }
}

