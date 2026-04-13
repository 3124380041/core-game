package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.enums.EffectTrigger;
import org.example.domain.enums.EffectType;
import org.example.domain.enums.StatType;

/**
 * Entity định nghĩa effect template.
 */
@Entity
@Table(name = "effect_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"skillTemplate"})
public class EffectTemplate {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private EffectType effectType;

    @Enumerated(EnumType.STRING)
    @Column(name = "effect_trigger")
    private EffectTrigger trigger;

    /**
     * Giá trị của effect.
     * - DAMAGE/HEAL: số lượng hoặc % của stat
     * - BUFF/DEBUFF: % tăng/giảm stat
     * - POISON/BURN: damage per turn
     */
    @Column(name = "effect_value")
    @Builder.Default
    private int value = 0;

    /**
     * Có phải giá trị theo % không.
     */
    @Builder.Default
    private boolean isPercentage = false;

    /**
     * Stat bị ảnh hưởng (cho BUFF/DEBUFF).
     */
    @Enumerated(EnumType.STRING)
    private StatType targetStat;

    /**
     * Thời gian hiệu lực (số lượt, -1 = vĩnh viễn).
     */
    @Builder.Default
    private int duration = 0;

    /**
     * Tỷ lệ áp dụng effect (0.0 - 1.0).
     */
    @Builder.Default
    private double chance = 1.0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_template_id")
    private SkillTemplate skillTemplate;
}
