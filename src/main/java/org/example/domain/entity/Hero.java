package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.enums.EquipmentSlot;
import org.example.domain.enums.HeroType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entity đại diện cho hero (nhân vật chiến đấu).
 * Mở rộng từ Character cũ với combat stats mới.
 */
@Entity
@Table(name = "heroes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Hero {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private HeroType type;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "stars", nullable = false)
    private int stars; // Rarity 1-6 stars

    // ==================== BASE STATS ====================
    
    @Column(name = "base_hp", nullable = false)
    private int baseHp;         // Máu cơ bản

    @Column(name = "base_attack", nullable = false)
    private int baseAttack;     // Sức tấn công cơ bản

    @Column(name = "base_defense", nullable = false)
    private int baseDefense;    // Phòng thủ cơ bản

    @Column(name = "base_intelligence", nullable = false)
    private int baseIntelligence; // Trí tuệ (cho phép thuật)

    @Column(name = "base_speed", nullable = false)
    private int baseSpeed;      // Tốc độ (quyết định thứ tự đánh)

    // ==================== COMBAT STATS (Tỷ lệ %) ====================

    @Column(name = "crit_rate", nullable = false)
    @Builder.Default
    private double critRate = 0.05;     // Tỷ lệ chí mạng (5% mặc định)

    @Column(name = "crit_damage", nullable = false)
    @Builder.Default
    private double critDamage = 1.5;    // Sát thương chí mạng (150%)

    @Column(name = "dodge_rate", nullable = false)
    @Builder.Default
    private double dodgeRate = 0.05;    // Tỷ lệ né (5% mặc định)

    @Column(name = "block_rate", nullable = false)
    @Builder.Default
    private double blockRate = 0.0;     // Tỷ lệ đỡ

    @Column(name = "counter_rate", nullable = false)
    @Builder.Default
    private double counterRate = 0.0;   // Tỷ lệ phản đòn

    @Column(name = "stun_resist", nullable = false)
    @Builder.Default
    private double stunResist = 0.0;    // Kháng choáng

    @Column(name = "crit_resist", nullable = false)
    @Builder.Default
    private double critResist = 0.0;    // Kháng chí mạng

    // ==================== RELATIONSHIPS ====================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Player owner;

    @OneToMany(mappedBy = "hero", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HeroSkill> skills = new ArrayList<>();

    @OneToMany(mappedBy = "hero", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<HeroEquipment> equipment = new ArrayList<>();

    // ==================== CALCULATED STATS ====================

    /**
     * Tính HP tối đa (bao gồm bonus từ equipment).
     */
    public int getMaxHp() {
        int bonus = getEquipmentBonus("hp");
        return baseHp + bonus;
    }

    /**
     * Tính Attack (bao gồm bonus từ equipment).
     */
    public int getTotalAttack() {
        int bonus = getEquipmentBonus("attack");
        return baseAttack + bonus;
    }

    /**
     * Tính Defense (bao gồm bonus từ equipment).
     */
    public int getTotalDefense() {
        int bonus = getEquipmentBonus("defense");
        return baseDefense + bonus;
    }

    /**
     * Tính Intelligence (bao gồm bonus từ equipment).
     */
    public int getTotalIntelligence() {
        int bonus = getEquipmentBonus("intelligence");
        return baseIntelligence + bonus;
    }

    /**
     * Tính Speed (bao gồm bonus từ equipment).
     */
    public int getTotalSpeed() {
        int bonus = getEquipmentBonus("speed");
        return baseSpeed + bonus;
    }

    /**
     * Lấy bonus stat từ equipment.
     */
    private int getEquipmentBonus(String statName) {
        // TODO: Implement equipment bonus calculation
        return 0;
    }

    /**
     * Kiểm tra hero có đủ skill chưa (max 4).
     */
    public boolean canAddSkill() {
        return skills.size() < 4;
    }

    /**
     * Thêm skill cho hero.
     */
    public void addSkill(HeroSkill skill) {
        if (canAddSkill()) {
            skills.add(skill);
            skill.setHero(this);
        }
    }

    @Override
    public String toString() {
        return name + " [" + type + " ★" + stars + " Lv." + level + 
               " | HP:" + getMaxHp() + " ATK:" + getTotalAttack() + 
               " DEF:" + getTotalDefense() + " SPD:" + getTotalSpeed() + "]";
    }
}

