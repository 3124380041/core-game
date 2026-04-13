package org.example.engine;

import org.example.domain.enums.TargetType;
import org.example.domain.runtime.HeroRuntimeState;
import org.example.domain.runtime.MatchRuntimeState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Component chọn target cho skill/attack.
 */
@Component
public class TargetSelector {

    private final Random random = new Random();

    /**
     * Chọn targets dựa trên TargetType.
     */
    public List<HeroRuntimeState> selectTargets(MatchRuntimeState match,
                                                  HeroRuntimeState actor,
                                                  TargetType targetType,
                                                  List<Long> specifiedTargetIds) {
        int teamIndex = actor.getTeamIndex();

        switch (targetType) {
            case SINGLE:
                return selectSingleTarget(match, teamIndex, specifiedTargetIds);

            case AOE:
                return selectAllEnemies(match, teamIndex);

            case ROW:
                return selectRow(match, teamIndex, specifiedTargetIds);

            case LOWEST_HP:
                return selectLowestHp(match, teamIndex);

            case RANDOM:
                return selectRandomTarget(match, teamIndex);

            case SELF:
                return Collections.singletonList(actor);

            case ALL_ALLIES:
                return selectAllAllies(match, teamIndex);

            default:
                return Collections.emptyList();
        }
    }

    /**
     * Chọn một target cụ thể.
     */
    private List<HeroRuntimeState> selectSingleTarget(MatchRuntimeState match, int teamIndex,
                                                       List<Long> specifiedTargetIds) {
        if (specifiedTargetIds == null || specifiedTargetIds.isEmpty()) {
            // Nếu không chỉ định, chọn random
            return selectRandomTarget(match, teamIndex);
        }

        Long targetId = specifiedTargetIds.get(0);
        return match.getAliveEnemies(teamIndex).stream()
                .filter(h -> h.getHeroId().equals(targetId))
                .collect(Collectors.toList());
    }

    /**
     * Chọn tất cả enemies còn sống.
     */
    private List<HeroRuntimeState> selectAllEnemies(MatchRuntimeState match, int teamIndex) {
        return new ArrayList<>(match.getAliveEnemies(teamIndex));
    }

    /**
     * Chọn enemies ở một row.
     */
    private List<HeroRuntimeState> selectRow(MatchRuntimeState match, int teamIndex,
                                              List<Long> specifiedTargetIds) {
        // Mặc định chọn front row (row 0)
        int targetRow = 0;

        // Nếu có chỉ định target, lấy row của target đó
        if (specifiedTargetIds != null && !specifiedTargetIds.isEmpty()) {
            Long targetId = specifiedTargetIds.get(0);
            HeroRuntimeState target = match.getHeroState(targetId).orElse(null);
            if (target != null) {
                targetRow = target.getPositionRow();
            }
        }

        final int row = targetRow;
        return match.getAliveEnemies(teamIndex).stream()
                .filter(h -> h.getPositionRow() == row)
                .collect(Collectors.toList());
    }

    /**
     * Chọn enemy có HP% thấp nhất.
     */
    private List<HeroRuntimeState> selectLowestHp(MatchRuntimeState match, int teamIndex) {
        return match.getAliveEnemies(teamIndex).stream()
                .min((a, b) -> Double.compare(a.getHpPercent(), b.getHpPercent()))
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }

    /**
     * Chọn random enemy.
     */
    private List<HeroRuntimeState> selectRandomTarget(MatchRuntimeState match, int teamIndex) {
        List<HeroRuntimeState> enemies = match.getAliveEnemies(teamIndex);
        if (enemies.isEmpty()) {
            return Collections.emptyList();
        }
        int randomIndex = random.nextInt(enemies.size());
        return Collections.singletonList(enemies.get(randomIndex));
    }

    /**
     * Chọn tất cả allies còn sống.
     */
    private List<HeroRuntimeState> selectAllAllies(MatchRuntimeState match, int teamIndex) {
        return new ArrayList<>(match.getAliveAllies(teamIndex));
    }

    /**
     * Chọn ally có HP% thấp nhất (cho healing).
     */
    public List<HeroRuntimeState> selectWeakestAlly(MatchRuntimeState match, int teamIndex) {
        return match.getWeakestAlly(teamIndex)
                .map(Collections::singletonList)
                .orElse(Collections.emptyList());
    }
}

