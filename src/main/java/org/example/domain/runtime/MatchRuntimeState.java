package org.example.domain.runtime;

import lombok.Data;
import org.example.domain.entity.Hero;
import org.example.domain.entity.Match;
import org.example.domain.entity.Team;
import org.example.domain.entity.TeamSlot;
import org.example.domain.enums.MatchStatus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Runtime state của trận đấu.
 * Lưu tất cả thông tin runtime cần thiết cho combat engine.
 */
@Data
public class MatchRuntimeState {

    private Long matchId;
    private Match match;

    private List<HeroRuntimeState> team1States = new ArrayList<>();
    private List<HeroRuntimeState> team2States = new ArrayList<>();

    private List<HeroRuntimeState> turnOrder = new ArrayList<>();
    private int currentTurnIndex = 0;
    private int currentRound = 1;

    private MatchStatus status = MatchStatus.IN_PROGRESS;
    private Integer winnerTeamIndex; // 0 or 1

    /**
     * Khởi tạo match runtime từ Match entity.
     */
    public MatchRuntimeState(Match match) {
        this.matchId = match.getId();
        this.match = match;

        // Initialize team 1 states
        if (match.getTeam1() != null) {
            for (TeamSlot slot : match.getTeam1().getSlots()) {
                HeroRuntimeState state = new HeroRuntimeState(
                        slot.getHero(),
                        0,
                        slot.getPositionRow(),
                        slot.getPositionCol()
                );
                team1States.add(state);
            }
        }

        // Initialize team 2 states
        if (match.getTeam2() != null) {
            for (TeamSlot slot : match.getTeam2().getSlots()) {
                HeroRuntimeState state = new HeroRuntimeState(
                        slot.getHero(),
                        1,
                        slot.getPositionRow(),
                        slot.getPositionCol()
                );
                team2States.add(state);
            }
        }

        // Calculate initial turn order
        calculateTurnOrder();
    }

    /**
     * Tính thứ tự lượt dựa trên speed.
     */
    public void calculateTurnOrder() {
        turnOrder.clear();

        // Add all alive heroes
        for (HeroRuntimeState state : team1States) {
            if (state.isAlive()) {
                turnOrder.add(state);
            }
        }
        for (HeroRuntimeState state : team2States) {
            if (state.isAlive()) {
                turnOrder.add(state);
            }
        }

        // Sort by speed (highest first)
        turnOrder.sort(Comparator.comparing(
                (HeroRuntimeState h) -> h.getHero().getTotalSpeed()
        ).reversed());
    }

    /**
     * Lấy hero đang có lượt hiện tại.
     */
    public HeroRuntimeState getCurrentTurnHero() {
        if (turnOrder.isEmpty() || currentTurnIndex >= turnOrder.size()) {
            return null;
        }
        return turnOrder.get(currentTurnIndex);
    }

    /**
     * Chuyển sang lượt tiếp theo.
     * @return true nếu còn lượt trong round, false nếu hết round
     */
    public boolean nextTurn() {
        currentTurnIndex++;

        // Skip defeated heroes
        while (currentTurnIndex < turnOrder.size() &&
               !turnOrder.get(currentTurnIndex).isAlive()) {
            currentTurnIndex++;
        }

        return currentTurnIndex < turnOrder.size();
    }

    /**
     * Bắt đầu round mới.
     */
    public void startNewRound() {
        currentRound++;
        currentTurnIndex = 0;

        // Recalculate turn order (some heroes may have died)
        calculateTurnOrder();

        // Round end effects: increase MP, tick effects
        for (HeroRuntimeState state : getAllStates()) {
            if (state.isAlive()) {
                state.onRoundEnd();
                state.tickEffects();
                state.removeExpiredEffects();
            }
        }
    }

    /**
     * Lấy tất cả hero states.
     */
    public List<HeroRuntimeState> getAllStates() {
        List<HeroRuntimeState> all = new ArrayList<>();
        all.addAll(team1States);
        all.addAll(team2States);
        return all;
    }

    /**
     * Lấy states của team đối thủ.
     */
    public List<HeroRuntimeState> getEnemyTeam(int teamIndex) {
        return teamIndex == 0 ? team2States : team1States;
    }

    /**
     * Lấy states của team đồng minh.
     */
    public List<HeroRuntimeState> getAllyTeam(int teamIndex) {
        return teamIndex == 0 ? team1States : team2States;
    }

    /**
     * Lấy hero state theo ID.
     */
    public Optional<HeroRuntimeState> getHeroState(Long heroId) {
        return getAllStates().stream()
                .filter(s -> s.getHeroId().equals(heroId))
                .findFirst();
    }

    /**
     * Lấy danh sách enemies còn sống.
     */
    public List<HeroRuntimeState> getAliveEnemies(int teamIndex) {
        return getEnemyTeam(teamIndex).stream()
                .filter(HeroRuntimeState::isAlive)
                .collect(Collectors.toList());
    }

    /**
     * Lấy danh sách allies còn sống.
     */
    public List<HeroRuntimeState> getAliveAllies(int teamIndex) {
        return getAllyTeam(teamIndex).stream()
                .filter(HeroRuntimeState::isAlive)
                .collect(Collectors.toList());
    }

    /**
     * Lấy ally yếu nhất (% HP thấp nhất).
     */
    public Optional<HeroRuntimeState> getWeakestAlly(int teamIndex) {
        return getAliveAllies(teamIndex).stream()
                .min(Comparator.comparing(HeroRuntimeState::getHpPercent));
    }

    /**
     * Lấy enemy ngẫu nhiên.
     */
    public Optional<HeroRuntimeState> getRandomEnemy(int teamIndex) {
        List<HeroRuntimeState> enemies = getAliveEnemies(teamIndex);
        if (enemies.isEmpty()) {
            return Optional.empty();
        }
        int randomIndex = (int) (Math.random() * enemies.size());
        return Optional.of(enemies.get(randomIndex));
    }

    /**
     * Lấy enemies ở hàng trước (row = 0).
     */
    public List<HeroRuntimeState> getFrontRowEnemies(int teamIndex) {
        return getAliveEnemies(teamIndex).stream()
                .filter(h -> h.getPositionRow() == 0)
                .collect(Collectors.toList());
    }

    /**
     * Kiểm tra trận đấu đã kết thúc chưa.
     * @return true nếu một team đã thua hết
     */
    public boolean checkGameEnd() {
        boolean team1Alive = team1States.stream().anyMatch(HeroRuntimeState::isAlive);
        boolean team2Alive = team2States.stream().anyMatch(HeroRuntimeState::isAlive);

        if (!team1Alive) {
            status = MatchStatus.COMPLETED;
            winnerTeamIndex = 1;
            return true;
        }
        if (!team2Alive) {
            status = MatchStatus.COMPLETED;
            winnerTeamIndex = 0;
            return true;
        }

        return false;
    }

    /**
     * Kiểm tra trận đấu còn đang diễn ra không.
     */
    public boolean isInProgress() {
        return status == MatchStatus.IN_PROGRESS;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Match #").append(matchId).append(" - Round ").append(currentRound).append(" ===\n");

        sb.append("Team 1:\n");
        for (HeroRuntimeState state : team1States) {
            sb.append("  ").append(state).append("\n");
        }

        sb.append("Team 2:\n");
        for (HeroRuntimeState state : team2States) {
            sb.append("  ").append(state).append("\n");
        }

        if (status == MatchStatus.COMPLETED) {
            sb.append("Winner: Team ").append(winnerTeamIndex + 1);
        }

        return sb.toString();
    }
}

