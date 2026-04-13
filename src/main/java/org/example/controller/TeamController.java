package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Player;
import org.example.domain.entity.Team;
import org.example.dto.PlayerResponse;
import org.example.dto.TeamSummary;
import org.example.service.TeamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Team.
 */
@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /**
     * Tạo team mới cho player.
     * POST /api/teams?playerId={playerId}&name={name}
     */
    @PostMapping
    public ResponseEntity<TeamSummary> createTeam(@RequestParam Long playerId,
                                                   @RequestParam String name) {
        Team team = teamService.createTeam(playerId, name);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TeamSummary.from(team));
    }

    /**
     * Lấy team theo ID.
     * GET /api/teams/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamSummary> getTeam(@PathVariable Long id) {
        Team team = teamService.getTeam(id);
        return ResponseEntity.ok(TeamSummary.from(team));
    }

    /**
     * Lấy teams của player.
     * GET /api/teams?playerId={playerId}
     */
    @GetMapping
    public ResponseEntity<List<TeamSummary>> getTeamsByPlayer(@RequestParam Long playerId) {
        List<Team> teams = teamService.getTeamsByPlayer(playerId);
        return ResponseEntity.ok(teams.stream()
                .map(TeamSummary::from)
                .toList());
    }

    /**
     * Thêm hero vào team.
     * POST /api/teams/{id}/heroes?heroId={heroId}&row={row}&col={col}
     */
    @PostMapping("/{id}/heroes")
    public ResponseEntity<TeamSummary> addHeroToTeam(@PathVariable Long id,
                                                      @RequestParam Long heroId,
                                                      @RequestParam int row,
                                                      @RequestParam int col) {
        Team team = teamService.addHeroToTeam(id, heroId, row, col);
        return ResponseEntity.ok(TeamSummary.from(team));
    }

    /**
     * Xóa hero khỏi team.
     * DELETE /api/teams/{id}/heroes/{heroId}
     */
    @DeleteMapping("/{id}/heroes/{heroId}")
    public ResponseEntity<TeamSummary> removeHeroFromTeam(@PathVariable Long id,
                                                           @PathVariable Long heroId) {
        Team team = teamService.removeHeroFromTeam(id, heroId);
        return ResponseEntity.ok(TeamSummary.from(team));
    }

    /**
     * Di chuyển hero trong team.
     * PUT /api/teams/{id}/heroes/{heroId}/move?row={row}&col={col}
     */
    @PutMapping("/{id}/heroes/{heroId}/move")
    public ResponseEntity<TeamSummary> moveHeroInTeam(@PathVariable Long id,
                                                       @PathVariable Long heroId,
                                                       @RequestParam int row,
                                                       @RequestParam int col) {
        Team team = teamService.moveHeroInTeam(id, heroId, row, col);
        return ResponseEntity.ok(TeamSummary.from(team));
    }

    /**
     * Set active team cho player.
     * POST /api/teams/{id}/activate?playerId={playerId}
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<PlayerResponse> setActiveTeam(@PathVariable Long id,
                                                         @RequestParam Long playerId) {
        Player player = teamService.setActiveTeam(playerId, id);
        return ResponseEntity.ok(PlayerResponse.from(player));
    }
}

