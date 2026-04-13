package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.engine.TurnResult;

import java.util.List;

/**
 * Response cho Turn result.
 */
@Data
@Builder
public class TurnResultResponse {
    private Long actorHeroId;
    private String actorName;
    private String actionType;
    private String skillName;
    private boolean turnSkipped;
    private String message;
    private List<TargetInfo> targets;
    private boolean gameEnded;
    private Long winnerId;

    @Data
    @Builder
    public static class TargetInfo {
        private Long heroId;
        private String name;
        private int damageTaken;
        private int healingReceived;
        private boolean wasCritical;
        private boolean wasDodged;
        private boolean wasBlocked;
        private boolean defeated;
        private List<String> effectsApplied;
    }

    public static TurnResultResponse from(TurnResult result, boolean gameEnded, Long winnerId) {
        TurnResultResponseBuilder builder = TurnResultResponse.builder()
                .actorHeroId(result.getActorHeroId())
                .actorName(result.getActorName())
                .actionType(result.getActionType())
                .skillName(result.getSkillName())
                .turnSkipped(result.isTurnSkipped())
                .message(result.getMessage())
                .gameEnded(gameEnded)
                .winnerId(winnerId);

        if (result.getTargetResults() != null) {
            builder.targets(result.getTargetResults().stream()
                    .map(tr -> TargetInfo.builder()
                            .heroId(tr.getTargetHeroId())
                            .name(tr.getTargetName())
                            .damageTaken(tr.getDamageResult() != null ? tr.getDamageResult().getFinalDamage() : 0)
                            .healingReceived(tr.getHealingDone())
                            .wasCritical(tr.getDamageResult() != null && tr.getDamageResult().isCritical())
                            .wasDodged(tr.getDamageResult() != null && tr.getDamageResult().isDodged())
                            .wasBlocked(tr.getDamageResult() != null && tr.getDamageResult().isBlocked())
                            .defeated(tr.isTargetDefeated())
                            .effectsApplied(tr.getEffectsApplied())
                            .build())
                    .toList());
        }

        return builder.build();
    }
}

