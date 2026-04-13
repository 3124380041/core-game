package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho Equipment Set.
 * Khi trang bị đủ số lượng item cùng set sẽ được bonus thêm.
 */
@Entity
@Table(name = "equipment_sets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"items"})
public class EquipmentSet {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    // ==================== 2-PIECE BONUS ====================
    @Builder.Default
    @Column(name = "bonus2_hp")
    private int bonus2Hp = 0;
    @Builder.Default
    @Column(name = "bonus2_attack")
    private int bonus2Attack = 0;
    @Builder.Default
    @Column(name = "bonus2_defense")
    private int bonus2Defense = 0;
    @Builder.Default
    @Column(name = "bonus2_speed")
    private int bonus2Speed = 0;
    @Builder.Default
    @Column(name = "bonus2_crit_rate")
    private double bonus2CritRate = 0.0;

    // ==================== 4-PIECE BONUS ====================
    @Builder.Default
    @Column(name = "bonus4_hp")
    private int bonus4Hp = 0;

    @Builder.Default
    @Column(name = "bonus4_attack")
    private int bonus4Attack = 0;

    @Builder.Default
    @Column(name = "bonus4_defense")
    private int bonus4Defense = 0;

    @Builder.Default
    @Column(name = "bonus4_speed")
    private int bonus4Speed = 0;

    @Builder.Default
    @Column(name = "bonus4_crit_rate")
    private double bonus4CritRate = 0.0;

    @Builder.Default
    @Column(name = "bonus4_crit_damage")
    private double bonus4CritDamage = 0.0;

    // ==================== 6-PIECE BONUS ====================
    @Builder.Default
    @Column(name = "bonus6_hp")
    private int bonus6Hp = 0;

    @Builder.Default
    @Column(name = "bonus6_attack")
    private int bonus6Attack = 0;

    @Builder.Default
    @Column(name = "bonus6_defense")
    private int bonus6Defense = 0;

    @Builder.Default
    @Column(name = "bonus6_speed")
    private int bonus6Speed = 0;

    @Builder.Default
    @Column(name = "bonus6_crit_rate")
    private double bonus6CritRate = 0.0;

    @Builder.Default
    @Column(name = "bonus6_crit_damage")
    private double bonus6CritDamage = 0.0;

    /**
     * Special effect khi đủ 6 pieces.
     */
    @Column(name = "bonus6_special_effect")
    private String bonus6SpecialEffect;

    @OneToMany(mappedBy = "equipmentSet", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ItemTemplate> items = new ArrayList<>();
}

