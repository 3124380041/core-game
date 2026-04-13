package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.enums.EquipmentSlot;

/**
 * Entity đại diện cho item/equipment template.
 */
@Entity
@Table(name = "item_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"equipmentSet"})
public class ItemTemplate {

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
    private EquipmentSlot slot;

    /**
     * Rarity (1-5 stars).
     */
    @Column(nullable = false)
    @Builder.Default
    private int rarity = 1;

    // ==================== STAT BONUSES ====================

    @Builder.Default
    private int bonusHp = 0;

    @Builder.Default
    private int bonusAttack = 0;

    @Builder.Default
    private int bonusDefense = 0;

    @Builder.Default
    private int bonusIntelligence = 0;

    @Builder.Default
    private int bonusSpeed = 0;

    @Builder.Default
    private double bonusCritRate = 0.0;

    @Builder.Default
    private double bonusCritDamage = 0.0;

    @Builder.Default
    private double bonusDodgeRate = 0.0;

    @Builder.Default
    private double bonusBlockRate = 0.0;

    // ==================== SET BONUS ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_set_id")
    private EquipmentSet equipmentSet;
}

