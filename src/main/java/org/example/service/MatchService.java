package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.*;
import org.example.domain.enums.MatchMode;
import org.example.domain.enums.MatchStatus;
import org.example.domain.runtime.MatchRuntimeState;
import org.example.dto.CombatActionRequest;
import org.example.dto.CreateMatchRequest;
import org.example.engine.CombatEngine;
import org.example.engine.TurnResult;
import org.example.exception.GameException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.CombatLogRepository;
import org.example.repository.MatchRepository;
import org.example.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service quản lý Match và combat.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MatchService {

    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;
    private final CombatLogRepository combatLogRepository;
    private final CombatEngine combatEngine;

    // In-memory cache cho match runtime states
    private final Map<Long, MatchRuntimeState> activeMatches = new ConcurrentHashMap<>();

    /**
     * Tạo match mới.
     */
    public Match createMatch(CreateMatchRequest request) {
        Player player1 = playerRepository.findById(request.getPlayer1Id())
                .orElseThrow(() -> new ResourceNotFoundException("Player 1 không tồn tại: " + request.getPlayer1Id()));

        Player player2 = playerRepository.findById(request.getPlayer2Id())
                .orElseThrow(() -> new ResourceNotFoundException("Player 2 không tồn tại: " + request.getPlayer2Id()));

        if (player1.getActiveTeam() == null || player1.getActiveTeam().getSlots().isEmpty()) {
            throw new GameException("Player 1 chưa có team!");
        }

        if (player2.getActiveTeam() == null || player2.getActiveTeam().getSlots().isEmpty()) {
            throw new GameException("Player 2 chưa có team!");
        }

        Match match = Match.builder()
                .player1(player1)
                .player2(player2)
                .team1(player1.getActiveTeam())
                .team2(player2.getActiveTeam())
                .status(MatchStatus.IN_PROGRESS)
                .mode(MatchMode.PVP)
                .build();

        Match saved = matchRepository.save(match);

        // Initialize runtime state
        MatchRuntimeState runtimeState = new MatchRuntimeState(saved);
        activeMatches.put(saved.getId(), runtimeState);

        log.info("Tạo match mới: {} vs {} (Match #{})", 
                player1.getName(), player2.getName(), saved.getId());

        return saved;
    }

    /**
     * Lấy match theo ID.
     */
    @Transactional(readOnly = true)
    public Match getMatch(Long matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match không tồn tại: " + matchId));
    }

    /**
     * Lấy runtime state của match.
     */
    public MatchRuntimeState getMatchState(Long matchId) {
        MatchRuntimeState state = activeMatches.get(matchId);
        if (state == null) {
            // Try to load from DB
            Match match = getMatch(matchId);
            if (match.getStatus() == MatchStatus.IN_PROGRESS) {
                state = new MatchRuntimeState(match);
                activeMatches.put(matchId, state);
            } else {
                // Return a finished state for completed matches
                state = new MatchRuntimeState(match);
                state.setStatus(MatchStatus.COMPLETED);
                if (match.getWinner() != null) {
                    state.setWinnerTeamIndex(
                        match.getWinner().getId().equals(match.getPlayer1().getId()) ? 0 : 1
                    );
                }
            }
        }
        return state;
    }

    /**
     * Submit action cho turn hiện tại.
     */
    public TurnResult submitAction(Long matchId, Long playerId, CombatActionRequest action) {
        MatchRuntimeState state = getMatchState(matchId);

        // Validate match is in progress
        if (!state.isInProgress()) {
            throw new GameException("Match đã kết thúc!");
        }

        // Validate it's player's turn
        var currentHero = state.getCurrentTurnHero();
        if (currentHero == null) {
            throw new GameException("Không có hero nào để hành động!");
        }

        int expectedTeam = currentHero.getTeamIndex();
        Match match = state.getMatch();
        Long expectedPlayerId = expectedTeam == 0 ? match.getPlayer1().getId() : match.getPlayer2().getId();

        if (!expectedPlayerId.equals(playerId)) {
            throw new GameException("Không phải lượt của bạn!");
        }

        // Validate hero belongs to player
        if (action.getHeroId() != null && !action.getHeroId().equals(currentHero.getHeroId())) {
            throw new GameException("Hero không đúng lượt!");
        }

        // Execute turn
        TurnResult result = combatEngine.executeTurn(state, action);

        // Log combat action
        logCombatAction(state, result);

        // Advance turn
        if (!state.nextTurn()) {
            // End of round
            combatEngine.processRoundEnd(state);
            state.startNewRound();
            combatEngine.processRoundStart(state);
        }

        // Check game end
        if (state.checkGameEnd()) {
            endMatch(state);
        }

        return result;
    }

    /**
     * Auto-play một turn (cho AI hoặc AFK).
     */
    public TurnResult autoPlayTurn(Long matchId) {
        MatchRuntimeState state = getMatchState(matchId);

        if (!state.isInProgress()) {
            throw new GameException("Match đã kết thúc!");
        }

        var currentHero = state.getCurrentTurnHero();
        if (currentHero == null) {
            throw new GameException("Không có hero nào để hành động!");
        }

        // Create auto action (basic attack on random enemy)
        CombatActionRequest action = new CombatActionRequest();
        action.setHeroId(currentHero.getHeroId());
        action.setActionType("ATTACK");

        Match match = state.getMatch();
        int teamIndex = currentHero.getTeamIndex();
        Long playerId = teamIndex == 0 ? match.getPlayer1().getId() : match.getPlayer2().getId();

        return submitAction(matchId, playerId, action);
    }

    /**
     * Simulate full battle (cho testing).
     */
    public MatchRuntimeState simulateBattle(Long matchId) {
        MatchRuntimeState state = getMatchState(matchId);

        int maxRounds = 100;
        int round = 0;

        while (state.isInProgress() && round < maxRounds) {
            try {
                autoPlayTurn(matchId);
            } catch (Exception e) {
                log.warn("Error in auto-play: {}", e.getMessage());
                break;
            }
            round++;
        }

        return state;
    }

    /**
     * Simulate full battle và trả về turn history.
     */
    public SimulateBattleResult simulateBattleWithHistory(Long matchId) {
        MatchRuntimeState state = getMatchState(matchId);
        List<TurnResult> turnHistory = new java.util.ArrayList<>();

        int maxRounds = 100;
        int turnCount = 0;

        while (state.isInProgress() && turnCount < maxRounds) {
            try {
                var currentHero = state.getCurrentTurnHero();
                if (currentHero == null) break;

                // Create auto action
                CombatActionRequest action = new CombatActionRequest();
                action.setHeroId(currentHero.getHeroId());
                action.setActionType("ATTACK");

                // Execute turn directly
                TurnResult result = combatEngine.executeTurn(state, action);
                turnHistory.add(result);

                // Log combat action
                logCombatAction(state, result);

                // Advance turn
                if (!state.nextTurn()) {
                    combatEngine.processRoundEnd(state);
                    state.startNewRound();
                    combatEngine.processRoundStart(state);
                }

                // Check game end
                if (state.checkGameEnd()) {
                    endMatch(state);
                }

            } catch (Exception e) {
                log.warn("Error in simulate: {}", e.getMessage());
                break;
            }
            turnCount++;
        }

        return new SimulateBattleResult(state, turnHistory);
    }

    /**
     * Result container cho simulate battle.
     */
    public record SimulateBattleResult(MatchRuntimeState state, List<TurnResult> turnHistory) {}

    /**
     * Kết thúc match.
     */
    private void endMatch(MatchRuntimeState state) {
        Match match = state.getMatch();
        
        Player winner = null;
        if (state.getWinnerTeamIndex() != null) {
            winner = state.getWinnerTeamIndex() == 0 ? match.getPlayer1() : match.getPlayer2();
        }

        match.endMatch(winner);
        matchRepository.save(match);

        // Keep state in cache but mark as completed (don't remove immediately)
        // This allows clients to still access final state
        state.setStatus(MatchStatus.COMPLETED);

        log.info("Match #{} kết thúc! Winner: {}", match.getId(), 
                winner != null ? winner.getName() : "Draw");
    }

    /**
     * Log combat action.
     */
    private void logCombatAction(MatchRuntimeState state, TurnResult result) {
        if (result.isTurnSkipped()) return;

        Match match = state.getMatch();
        
        // Create combat log based on action type
        CombatLog log;
        if ("SKILL".equals(result.getActionType()) && !result.getTargetResults().isEmpty()) {
            var targetResult = result.getTargetResults().get(0);
            log = CombatLog.skill(
                    match,
                    state.getCurrentRound(),
                    state.getCurrentTurnIndex(),
                    result.getActorName(),
                    result.getActorHeroId(),
                    targetResult.getTargetName(),
                    targetResult.getTargetHeroId(),
                    result.getSkillName(),
                    targetResult.getDamageResult() != null ? targetResult.getDamageResult().getFinalDamage() : 0,
                    targetResult.getHealingDone(),
                    targetResult.getDamageResult() != null && targetResult.getDamageResult().isCritical(),
                    targetResult.getDamageResult() != null && targetResult.getDamageResult().isDodged()
            );
        } else if (!result.getTargetResults().isEmpty()) {
            var targetResult = result.getTargetResults().get(0);
            var damageResult = targetResult.getDamageResult();
            log = CombatLog.attack(
                    match,
                    state.getCurrentRound(),
                    state.getCurrentTurnIndex(),
                    result.getActorName(),
                    result.getActorHeroId(),
                    targetResult.getTargetName(),
                    targetResult.getTargetHeroId(),
                    damageResult != null ? damageResult.getFinalDamage() : 0,
                    damageResult != null && damageResult.isCritical(),
                    damageResult != null && damageResult.isDodged(),
                    damageResult != null && damageResult.isBlocked()
            );
        } else {
            return;
        }

        combatLogRepository.save(log);

        // Log deaths
        for (var targetResult : result.getTargetResults()) {
            if (targetResult.isTargetDefeated()) {
                CombatLog deathLog = CombatLog.death(
                        match,
                        state.getCurrentRound(),
                        state.getCurrentTurnIndex(),
                        targetResult.getTargetName(),
                        targetResult.getTargetHeroId()
                );
                combatLogRepository.save(deathLog);
            }
        }
    }

    /**
     * Lấy combat log của match.
     */
    @Transactional(readOnly = true)
    public List<CombatLog> getCombatLogs(Long matchId) {
        return combatLogRepository.findByMatchIdOrderByTimestampAsc(matchId);
    }

    /**
     * Lấy matches của player.
     */
    @Transactional(readOnly = true)
    public List<Match> getMatchesByPlayer(Long playerId) {
        return matchRepository.findByPlayer1IdOrPlayer2Id(playerId, playerId);
    }

    /**
     * Lấy matches đang active của player.
     */
    @Transactional(readOnly = true)
    public List<Match> getActiveMatchesByPlayer(Long playerId) {
        List<Match> asPlayer1 = matchRepository.findByPlayer1IdAndStatus(playerId, MatchStatus.IN_PROGRESS);
        List<Match> asPlayer2 = matchRepository.findByPlayer2IdAndStatus(playerId, MatchStatus.IN_PROGRESS);
        asPlayer1.addAll(asPlayer2);
        return asPlayer1;
    }

    /**
     * Tao tran dau pho ban (player vs doi quai).
     */
    public Match createDungeonMatch(Long playerId, Team enemyTeam, Long dungeonMapId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player không tồn tại: " + playerId));

        if (player.getActiveTeam() == null || player.getActiveTeam().getSlots().isEmpty()) {
            throw new GameException("Player chưa có team để vào phó bản!");
        }

        if (enemyTeam == null || enemyTeam.getSlots() == null || enemyTeam.getSlots().isEmpty()) {
            throw new GameException("Map phó bản chưa cấu hình đội quái!");
        }

        Player systemPlayer = playerRepository.findByUsername("system_dungeon")
                .orElseGet(() -> playerRepository.save(Player.builder()
                        .username("system_dungeon")
                        .name("Dungeon Master")
                        .passwordHash("{system}")
                        .level(999)
                        .experience(0)
                        .build()));

        Match match = Match.builder()
                .player1(player)
                .player2(systemPlayer)
                .team1(player.getActiveTeam())
                .team2(enemyTeam)
                .status(MatchStatus.IN_PROGRESS)
                .mode(MatchMode.DUNGEON)
                .sourceType("DUNGEON_MAP")
                .sourceId(dungeonMapId)
                .build();

        Match saved = matchRepository.save(match);
        MatchRuntimeState runtimeState = new MatchRuntimeState(saved);
        activeMatches.put(saved.getId(), runtimeState);

        log.info("Tạo dungeon match: player {} vs map {} (Match #{})", player.getName(), dungeonMapId, saved.getId());
        return saved;
    }
}
