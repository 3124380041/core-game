package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.DungeonMapResponse;
import org.example.dto.DungeonResponse;
import org.example.dto.DungeonRunResponse;
import org.example.dto.MatchResponse;
import org.example.service.DungeonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API cho logic pho ban.
 */
@RestController
@RequestMapping("/api/dungeons")
@RequiredArgsConstructor
public class DungeonController {

    private final DungeonService dungeonService;

    @GetMapping
    public ResponseEntity<List<DungeonResponse>> getDungeons() {
        return ResponseEntity.ok(dungeonService.getDungeons());
    }

    @GetMapping("/{dungeonId}/maps")
    public ResponseEntity<List<DungeonMapResponse>> getMaps(@PathVariable Long dungeonId) {
        return ResponseEntity.ok(dungeonService.getDungeonMaps(dungeonId));
    }

    @PostMapping("/{dungeonId}/runs")
    public ResponseEntity<DungeonRunResponse> startRun(@PathVariable Long dungeonId,
                                                       @RequestParam Long playerId) {
        return ResponseEntity.ok(dungeonService.startRun(playerId, dungeonId));
    }

    @PostMapping("/start-battle")
    public ResponseEntity<DungeonService.DungeonBattleStartResult> startBattle(@RequestParam Long playerId,
                                                                                @RequestParam Long dungeonId) {
        return ResponseEntity.ok(dungeonService.startRunAndBattle(playerId, dungeonId));
    }

    @PostMapping("/runs/{runId}/start-battle")
    public ResponseEntity<MatchResponse> startCurrentMapBattle(@PathVariable Long runId,
                                                               @RequestParam Long playerId) {
        return ResponseEntity.ok(dungeonService.startCurrentMapBattleResponse(runId, playerId));
    }

    @PostMapping("/runs/{runId}/resolve")
    public ResponseEntity<DungeonRunResponse> resolveRun(@PathVariable Long runId,
                                                         @RequestParam Long playerId) {
        return ResponseEntity.ok(dungeonService.resolveRun(runId, playerId));
    }

    @GetMapping("/runs/{runId}")
    public ResponseEntity<DungeonRunResponse> getRun(@PathVariable Long runId,
                                                     @RequestParam Long playerId) {
        return ResponseEntity.ok(dungeonService.getRun(runId, playerId));
    }
}
