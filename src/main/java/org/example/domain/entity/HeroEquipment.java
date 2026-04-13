package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.enums.EquipmentSlot;

/**
 * Entity liên kết Hero với Item (equipment đã trang bị).
 */
@Entity
@Table(name = "hero_equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"hero", "itemTemplate"})
public class HeroEquipment {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_id", nullable = false)
    private Hero hero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_template_id", nullable = false)
    private ItemTemplate itemTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "slot", nullable = false)
    private EquipmentSlot slot;

    /**
     * Level của item (1-15, tăng stat).
     */
    @Column(name = "enhance_level", nullable = false)
    @Builder.Default
    private int enhanceLevel = 0;

    /**
     * Tính bonus HP từ item + enhance level.
     */
    public int getEffectiveBonusHp() {
        int base = itemTemplate.getBonusHp();
        return (int) (base * (1 + enhanceLevel * 0.1));
    }

    /**
     * Tính bonus Attack từ item + enhance level.
     */
    public int getEffectiveBonusAttack() {
        int base = itemTemplate.getBonusAttack();
        return (int) (base * (1 + enhanceLevel * 0.1));
    }

    /**
     * Tính bonus Defense từ item + enhance level.
     */
    public int getEffectiveBonusDefense() {
        int base = itemTemplate.getBonusDefense();
        return (int) (base * (1 + enhanceLevel * 0.1));
    }

    /**
     * Tính bonus Speed từ item + enhance level.
     */
    public int getEffectiveBonusSpeed() {
        int base = itemTemplate.getBonusSpeed();
        return (int) (base * (1 + enhanceLevel * 0.1));
    }
}

