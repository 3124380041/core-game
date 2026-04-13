package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.CombatLog;
import org.example.domain.entity.Match;
import org.example.domain.runtime.MatchRuntimeState;
import org.example.dto.*;
import org.example.engine.TurnResult;
import org.example.service.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Match.
 */
@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * Tạo match mới.
     * POST /api/matches/start
     */
    @PostMapping("/start")
    public ResponseEntity<MatchResponse> createMatch(@Valid @RequestBody CreateMatchRequest request) {
        Match match = matchService.createMatch(request);
        MatchRuntimeState state = matchService.getMatchState(match.getId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(MatchResponse.fromRuntime(state));
    }

    /**
     * Lấy trạng thái match.
     * GET /api/matches/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatch(@PathVariable Long id) {
        try {
            MatchRuntimeState state = matchService.getMatchState(id);
            return ResponseEntity.ok(MatchResponse.fromRuntime(state));
        } catch (Exception e) {
            // Match đã kết thúc, trả về từ DB
            Match match = matchService.getMatch(id);
            return ResponseEntity.ok(MatchResponse.from(match));
        }
    }

    /**
     * Submit action cho turn hiện tại.
     * POST /api/matches/{id}/action
     */
    @PostMapping("/{id}/action")
    public ResponseEntity<TurnResultResponse> submitAction(@PathVariable Long id,
                                                            @RequestParam Long playerId,
                                                            @Valid @RequestBody CombatActionRequest action) {
        TurnResult result = matchService.submitAction(id, playerId, action);
        MatchRuntimeState state = matchService.getMatchState(id);

        Long winnerId = null;
        if (state.getWinnerTeamIndex() != null) {
            winnerId = state.getWinnerTeamIndex() == 0
                    ? state.getMatch().getPlayer1().getId()
                    : state.getMatch().getPlayer2().getId();
        }

        return ResponseEntity.ok(TurnResultResponse.from(result, !state.isInProgress(), winnerId));
    }

    /**
     * Auto-play một turn (AI/AFK).
     * POST /api/matches/{id}/auto
     */
    @PostMapping("/{id}/auto")
    public ResponseEntity<TurnResultResponse> autoPlay(@PathVariable Long id) {
        TurnResult result = matchService.autoPlayTurn(id);
        MatchRuntimeState state = matchService.getMatchState(id);

        Long winnerId = null;
        if (state.getWinnerTeamIndex() != null) {
            winnerId = state.getWinnerTeamIndex() == 0
                    ? state.getMatch().getPlayer1().getId()
                    : state.getMatch().getPlayer2().getId();
        }

        return ResponseEntity.ok(TurnResultResponse.from(result, !state.isInProgress(), winnerId));
    }

    /**
     * Simulate full battle.
     * POST /api/matches/{id}/simulate
     */
    @PostMapping("/{id}/simulate")
    public ResponseEntity<MatchResponse> simulateBattle(@PathVariable Long id) {
        MatchRuntimeState state = matchService.simulateBattle(id);
        return ResponseEntity.ok(MatchResponse.fromRuntime(state));
    }

    /**
     * Simulate full battle với turn history (cho animation).
     * POST /api/matches/{id}/simulate-with-history
     */
    @PostMapping("/{id}/simulate-with-history")
    public ResponseEntity<SimulateBattleResponse> simulateBattleWithHistory(@PathVariable Long id) {
        var result = matchService.simulateBattleWithHistory(id);
        var state = result.state();

        Long winnerId = null;
        if (state.getWinnerTeamIndex() != null) {
            winnerId = state.getWinnerTeamIndex() == 0
                    ? state.getMatch().getPlayer1().getId()
                    : state.getMatch().getPlayer2().getId();
        }

        List<TurnResultResponse> turnResponses = result.turnHistory().stream()
                .map(tr -> TurnResultResponse.from(tr, false, null))
                .toList();

        // Mark last turn as game ended
        if (!turnResponses.isEmpty() && !state.isInProgress()) {
            var lastTurn = turnResponses.get(turnResponses.size() - 1);
            lastTurn.setGameEnded(true);
            lastTurn.setWinnerId(winnerId);
        }

        return ResponseEntity.ok(SimulateBattleResponse.builder()
                .matchResult(MatchResponse.fromRuntime(state))
                .turnHistory(turnResponses)
                .build());
    }

    /**
     * Lấy combat log của match.
     * GET /api/matches/{id}/logs
     */
    @GetMapping("/{id}/logs")
    public ResponseEntity<List<CombatLog>> getCombatLogs(@PathVariable Long id) {
        List<CombatLog> logs = matchService.getCombatLogs(id);
        return ResponseEntity.ok(logs);
    }

    /**
     * Lấy matches của player.
     * GET /api/matches?playerId={playerId}
     */
    @GetMapping
    public ResponseEntity<List<MatchResponse>> getMatchesByPlayer(@RequestParam Long playerId,
                                                                    @RequestParam(defaultValue = "false") boolean activeOnly) {
        List<Match> matches;
        if (activeOnly) {
            matches = matchService.getActiveMatchesByPlayer(playerId);
        } else {
            matches = matchService.getMatchesByPlayer(playerId);
        }

        return ResponseEntity.ok(matches.stream()
                .map(MatchResponse::from)
                .toList());
    }
}

