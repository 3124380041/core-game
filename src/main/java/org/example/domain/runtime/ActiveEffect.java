package org.example.domain.runtime;

import lombok.Builder;
import lombok.Data;
import org.example.domain.enums.EffectTrigger;
import org.example.domain.enums.EffectType;
import org.example.domain.enums.StatType;

/**
 * Runtime instance của effect đang active trên hero.
 */
@Data
@Builder
public class ActiveEffect {

    private Long effectTemplateId;
    private String name;
    private EffectType effectType;
    private EffectTrigger trigger;

    private int value;
    private boolean isPercentage;
    private StatType targetStat;

    private int remainingDuration; // -1 = permanent
    private Long sourceHeroId;     // Hero gây effect

    /**
     * Giảm duration.
     */
    public void tick() {
        if (remainingDuration > 0) {
            remainingDuration--;
        }
    }

    /**
     * Kiểm tra effect đã hết hạn chưa.
     */
    public boolean isExpired() {
        return remainingDuration == 0;
    }

    /**
     * Kiểm tra effect là permanent không.
     */
    public boolean isPermanent() {
        return remainingDuration == -1;
    }

    /**
     * Apply effect (cho DOT effects như Poison, Burn).
     * @return damage/heal value
     */
    public int apply(HeroRuntimeState target) {
        switch (effectType) {
            case POISON:
                return target.takeTrueDamage(value);
            case BURN:
                return target.takeTrueDamage(value);
            case HEAL:
                return target.heal(value);
            default:
                return 0;
        }
    }

    /**
     * Lấy stat modifier (cho BUFF/DEBUFF).
     */
    public int getStatModifier() {
        if (effectType == EffectType.BUFF) {
            return value;
        } else if (effectType == EffectType.DEBUFF) {
            return -value;
        }
        return 0;
    }

    @Override
    public String toString() {
        return name + " [" + effectType + 
               (remainingDuration >= 0 ? " " + remainingDuration + " turns" : " permanent") + "]";
    }
}

