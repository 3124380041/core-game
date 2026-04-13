package org.example.engine;

import org.example.domain.entity.Hero;
import org.example.domain.enums.DamageType;
import org.example.domain.runtime.HeroRuntimeState;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * Component tính toán damage.
 */
@Component
public class DamageCalculator {

    private final Random random = new Random();

    /**
     * Tính damage cơ bản cho basic attack.
     */
    public DamageResult calculateBasicAttack(HeroRuntimeState attacker, HeroRuntimeState defender) {
        Hero attackerHero = attacker.getHero();
        Hero defenderHero = defender.getHero();

        // Base damage = ATK * 2 + SPD / 2 (giống formula cũ)
        int baseDamage = attackerHero.getTotalAttack() * 2 + attackerHero.getTotalSpeed() / 2;

        return calculateDamage(attacker, defender, baseDamage, DamageType.PHYSICAL);
    }

    /**
     * Tính damage cho skill.
     */
    public DamageResult calculateSkillDamage(HeroRuntimeState attacker, HeroRuntimeState defender,
                                              double scaling, String scalingStat, DamageType damageType) {
        Hero attackerHero = attacker.getHero();

        // Calculate base damage from scaling stat
        int statValue;
        if ("INTELLIGENCE".equals(scalingStat)) {
            statValue = attackerHero.getTotalIntelligence();
        } else {
            statValue = attackerHero.getTotalAttack();
        }

        int baseDamage = (int) (statValue * scaling);

        return calculateDamage(attacker, defender, baseDamage, damageType);
    }

    /**
     * Tính damage với tất cả modifier (crit, dodge, block).
     */
    public DamageResult calculateDamage(HeroRuntimeState attacker, HeroRuntimeState defender,
                                         int baseDamage, DamageType damageType) {
        Hero attackerHero = attacker.getHero();
        Hero defenderHero = defender.getHero();

        DamageResult.DamageResultBuilder result = DamageResult.builder()
                .baseDamage(baseDamage)
                .damageType(damageType.name());

        // === DODGE CHECK ===
        double dodgeChance = defenderHero.getDodgeRate();
        if (random.nextDouble() < dodgeChance) {
            return result
                    .isDodged(true)
                    .finalDamage(0)
                    .build();
        }

        int finalDamage = baseDamage;

        // === CRITICAL CHECK ===
        double critChance = attackerHero.getCritRate() - defenderHero.getCritResist();
        boolean isCritical = random.nextDouble() < Math.max(0, critChance);

        if (isCritical) {
            finalDamage = (int) (finalDamage * attackerHero.getCritDamage());
            result.isCritical(true);
        }

        // === BLOCK CHECK ===
        double blockChance = defenderHero.getBlockRate();
        boolean isBlocked = random.nextDouble() < blockChance;

        if (isBlocked) {
            finalDamage = (int) (finalDamage * 0.5);
            result.isBlocked(true);
        }

        // === DEFENSE REDUCTION ===
        int defense = defenderHero.getTotalDefense();
        switch (damageType) {
            case PHYSICAL:
                finalDamage = Math.max(1, finalDamage - defense);
                break;
            case MAGIC:
                finalDamage = Math.max(1, finalDamage - defense / 2);
                break;
            case TRUE:
                // No defense reduction
                break;
        }

        return result.finalDamage(finalDamage).build();
    }

    /**
     * Tính lượng heal.
     */
    public int calculateHealing(HeroRuntimeState healer, double scaling, String scalingStat) {
        Hero healerHero = healer.getHero();

        int statValue;
        if ("INTELLIGENCE".equals(scalingStat)) {
            statValue = healerHero.getTotalIntelligence();
        } else {
            statValue = healerHero.getTotalAttack();
        }

        return (int) (statValue * scaling);
    }
}

