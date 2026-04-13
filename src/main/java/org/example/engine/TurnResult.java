package org.example.engine;

import lombok.Builder;
import lombok.Data;
import org.example.domain.runtime.HeroRuntimeState;

import java.util.ArrayList;
import java.util.List;

/**
 * Kết quả của một turn.
 */
@Data
@Builder
public class TurnResult {
    private Long actorHeroId;
    private String actorName;
    private String actionType; // ATTACK, SKILL, SKIP
    private String skillName;

    @Builder.Default
    private List<TargetResult> targetResults = new ArrayList<>();

    private boolean turnSkipped; // Hero bị stun
    private String message;

    /**
     * Kết quả cho mỗi target.
     */
    @Data
    @Builder
    public static class TargetResult {
        private Long targetHeroId;
        private String targetName;
        private DamageResult damageResult;
        private int healingDone;
        private List<String> effectsApplied;
        private boolean targetDefeated;
        private boolean targetRevived;
    }
}

