package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.*;
import org.example.exception.GameException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.HeroRepository;
import org.example.repository.PlayerRepository;
import org.example.repository.TeamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service quản lý Team.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeamService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final HeroRepository heroRepository;

    /**
     * Tạo team mới cho player.
     */
    public Team createTeam(Long playerId, String teamName) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player không tồn tại: " + playerId));

        Team team = Team.builder()
                .name(teamName)
                .player(player)
                .build();

        Team saved = teamRepository.save(team);
        log.info("Tạo team mới: {} cho player {}", teamName, player.getName());

        return saved;
    }

    /**
     * Lấy team theo ID.
     */
    @Transactional(readOnly = true)
    public Team getTeam(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team không tồn tại: " + teamId));
    }

    /**
     * Lấy teams của player.
     */
    @Transactional(readOnly = true)
    public List<Team> getTeamsByPlayer(Long playerId) {
        return teamRepository.findByPlayerId(playerId);
    }

    /**
     * Thêm hero vào team.
     */
    public Team addHeroToTeam(Long teamId, Long heroId, int row, int col) {
        Team team = getTeam(teamId);

        if (team.isFull()) {
            throw new GameException("Team đã đầy (max 5 heroes)!");
        }

        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> new ResourceNotFoundException("Hero không tồn tại: " + heroId));

        // Check hero belongs to same player
        if (!hero.getOwner().getId().equals(team.getPlayer().getId())) {
            throw new GameException("Hero không thuộc về player này!");
        }

        // Check position
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            throw new GameException("Vị trí không hợp lệ! (0-2)");
        }

        boolean added = team.addHero(hero, row, col);
        if (!added) {
            throw new GameException("Không thể thêm hero vào vị trí này!");
        }

        log.info("Thêm hero {} vào team {} tại vị trí ({}, {})",
                hero.getName(), team.getName(), row, col);

        return teamRepository.save(team);
    }

    /**
     * Xóa hero khỏi team.
     */
    public Team removeHeroFromTeam(Long teamId, Long heroId) {
        Team team = getTeam(teamId);

        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> new ResourceNotFoundException("Hero không tồn tại: " + heroId));

        boolean removed = team.removeHero(hero);
        if (!removed) {
            throw new GameException("Hero không có trong team!");
        }

        log.info("Xóa hero {} khỏi team {}", hero.getName(), team.getName());

        return teamRepository.save(team);
    }

    /**
     * Set team làm active team cho player.
     */
    public Player setActiveTeam(Long playerId, Long teamId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player không tồn tại: " + playerId));

        Team team = getTeam(teamId);

        if (!team.getPlayer().getId().equals(playerId)) {
            throw new GameException("Team không thuộc về player này!");
        }

        if (team.getSlots().isEmpty()) {
            throw new GameException("Team không có hero nào!");
        }

        player.setActiveTeam(team);
        log.info("Set active team {} cho player {}", team.getName(), player.getName());

        return playerRepository.save(player);
    }

    /**
     * Di chuyển hero trong team.
     */
    public Team moveHeroInTeam(Long teamId, Long heroId, int newRow, int newCol) {
        Team team = getTeam(teamId);

        // Find slot
        TeamSlot slot = team.getSlots().stream()
                .filter(s -> s.getHero().getId().equals(heroId))
                .findFirst()
                .orElseThrow(() -> new GameException("Hero không có trong team!"));

        // Check new position is valid
        if (newRow < 0 || newRow > 2 || newCol < 0 || newCol > 2) {
            throw new GameException("Vị trí không hợp lệ! (0-2)");
        }

        // Check new position is empty
        boolean occupied = team.getSlots().stream()
                .anyMatch(s -> !s.getHero().getId().equals(heroId)
                        && s.getPositionRow() == newRow
                        && s.getPositionCol() == newCol);

        if (occupied) {
            throw new GameException("Vị trí đã có hero khác!");
        }

        slot.setPositionRow(newRow);
        slot.setPositionCol(newCol);

        log.info("Di chuyển hero {} trong team {} đến vị trí ({}, {})",
                slot.getHero().getName(), team.getName(), newRow, newCol);

        return teamRepository.save(team);
    }
}

