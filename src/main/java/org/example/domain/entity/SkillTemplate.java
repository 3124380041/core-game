package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.enums.DamageType;
import org.example.domain.enums.SkillType;
import org.example.domain.enums.TargetType;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity định nghĩa skill template.
 */
@Entity
@Table(name = "skill_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"effects"})
public class SkillTemplate {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkillType skillType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DamageType damageType;

    /**
     * Hệ số scaling (ví dụ: 1.5 = 150% ATK).
     */
    @Column(nullable = false)
    @Builder.Default
    private double scaling = 1.0;

    /**
     * Stat dùng để scale (ATTACK hoặc INTELLIGENCE).
     */
    @Column(nullable = false)
    @Builder.Default
    private String scalingStat = "ATTACK";

    /**
     * Cooldown (số lượt chờ).
     */
    @Column(nullable = false)
    @Builder.Default
    private int cooldown = 0;

    /**
     * MP cost (Nộ Khí).
     */
    @Column(nullable = false)
    @Builder.Default
    private int mpCost = 0;

    /**
     * Danh sách effects mà skill gây ra.
     */
    @OneToMany(mappedBy = "skillTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EffectTemplate> effects = new ArrayList<>();

    /**
     * Thêm effect vào skill.
     */
    public void addEffect(EffectTemplate effect) {
        effects.add(effect);
        effect.setSkillTemplate(this);
    }
}

