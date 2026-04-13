package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity lưu log các hành động trong trận đấu.
 */
@Entity
@Table(name = "combat_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"match"})
public class CombatLog {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "round_number", nullable = false)
    private int roundNumber;

    @Column(name = "turn_number", nullable = false)
    private int turnNumber;

    @Column(name = "timestamp", nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // ==================== ACTION INFO ====================

    @Column(name = "actor_name", nullable = false)
    private String actorName;

    @Column(name = "actor_hero_id", nullable = false)
    private Long actorHeroId;

    @Column(name = "action_type", nullable = false)
    private String actionType;  // ATTACK, SKILL, EFFECT, DEATH, REVIVE

    @Column(name = "skill_name")
    private String skillName;

    @Column(name = "target_name")
    private String targetName;

    @Column(name = "target_hero_id")
    private Long targetHeroId;

    // ==================== RESULT INFO ====================

    @Column(name = "damage_dealt", nullable = false)
    @Builder.Default
    private int damageDealt = 0;

    @Column(name = "healing_done", nullable = false)
    @Builder.Default
    private int healingDone = 0;

    @Column(name = "was_critical", nullable = false)
    @Builder.Default
    private boolean wasCritical = false;

    @Column(name = "was_dodged", nullable = false)
    @Builder.Default
    private boolean wasDodged = false;

    @Column(name = "was_blocked", nullable = false)
    @Builder.Default
    private boolean wasBlocked = false;

    @Column(name = "effects_applied")
    private String effectsApplied;

    @Column(name = "message")
    private String message;

    /**
     * Tạo log từ basic attack.
     */
    public static CombatLog attack(Match match, int round, int turn,
                                    String actor, Long actorId,
                                    String target, Long targetId,
                                    int damage, boolean crit, boolean dodged, boolean blocked) {
        return CombatLog.builder()
                .match(match)
                .roundNumber(round)
                .turnNumber(turn)
                .actorName(actor)
                .actorHeroId(actorId)
                .actionType("ATTACK")
                .targetName(target)
                .targetHeroId(targetId)
                .damageDealt(damage)
                .wasCritical(crit)
                .wasDodged(dodged)
                .wasBlocked(blocked)
                .message(buildAttackMessage(actor, target, damage, crit, dodged, blocked))
                .build();
    }

    /**
     * Tạo log từ skill.
     */
    public static CombatLog skill(Match match, int round, int turn,
                                   String actor, Long actorId,
                                   String target, Long targetId,
                                   String skillName, int damage, int healing,
                                   boolean crit, boolean dodged) {
        return CombatLog.builder()
                .match(match)
                .roundNumber(round)
                .turnNumber(turn)
                .actorName(actor)
                .actorHeroId(actorId)
                .actionType("SKILL")
                .skillName(skillName)
                .targetName(target)
                .targetHeroId(targetId)
                .damageDealt(damage)
                .healingDone(healing)
                .wasCritical(crit)
                .wasDodged(dodged)
                .message(buildSkillMessage(actor, skillName, target, damage, healing))
                .build();
    }

    /**
     * Tạo log khi hero chết.
     */
    public static CombatLog death(Match match, int round, int turn, String heroName, Long heroId) {
        return CombatLog.builder()
                .match(match)
                .roundNumber(round)
                .turnNumber(turn)
                .actorName(heroName)
                .actorHeroId(heroId)
                .actionType("DEATH")
                .message(heroName + " đã bị đánh bại!")
                .build();
    }

    /**
     * Tạo log khi hồi sinh.
     */
    public static CombatLog revive(Match match, int round, int turn,
                                    String heroName, Long heroId, int hpRestored) {
        return CombatLog.builder()
                .match(match)
                .roundNumber(round)
                .turnNumber(turn)
                .actorName(heroName)
                .actorHeroId(heroId)
                .actionType("REVIVE")
                .healingDone(hpRestored)
                .message(heroName + " đã hồi sinh với " + hpRestored + " HP!")
                .build();
    }

    private static String buildAttackMessage(String actor, String target, int damage,
                                              boolean crit, boolean dodged, boolean blocked) {
        if (dodged) {
            return target + " né được đòn tấn công của " + actor + "!";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(actor).append(" tấn công ").append(target);
        if (blocked) {
            sb.append(" (BLOCKED)");
        }
        if (crit) {
            sb.append(" (CRITICAL)");
        }
        sb.append(" gây ").append(damage).append(" sát thương!");
        return sb.toString();
    }

    private static String buildSkillMessage(String actor, String skill, String target,
                                             int damage, int healing) {
        StringBuilder sb = new StringBuilder();
        sb.append("⚡ ").append(actor).append(" sử dụng [").append(skill).append("]");
        if (target != null) {
            sb.append(" vào ").append(target);
        }
        if (damage > 0) {
            sb.append(" gây ").append(damage).append(" sát thương!");
        }
        if (healing > 0) {
            sb.append(" hồi ").append(healing).append(" HP!");
        }
        return sb.toString();
    }
}

