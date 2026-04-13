package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity liên kết Hero với Skill.
 * Một hero có thể có nhiều skill, mỗi skill có level riêng.
 */
@Entity
@Table(name = "hero_skills")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"hero", "skillTemplate"})
public class HeroSkill {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_id", nullable = false)
    private Hero hero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_template_id", nullable = false)
    private SkillTemplate skillTemplate;

    /**
     * Level của skill (1-10).
     */
    @Column(name = "skill_level", nullable = false)
    @Builder.Default
    private int skillLevel = 1;

    /**
     * Slot index của skill (0-3).
     */
    @Column(name = "slot_index", nullable = false)
    private int slotIndex;

    /**
     * Tính scaling tại level hiện tại.
     * Mỗi level tăng 10% scaling.
     */
    public double getEffectiveScaling() {
        double baseScaling = skillTemplate.getScaling();
        return baseScaling * (1 + (skillLevel - 1) * 0.1);
    }

    /**
     * Lấy cooldown (có thể giảm theo level).
     */
    public int getEffectiveCooldown() {
        int baseCooldown = skillTemplate.getCooldown();
        // Giảm 1 cooldown mỗi 5 level
        int reduction = skillLevel / 5;
        return Math.max(0, baseCooldown - reduction);
    }
}

