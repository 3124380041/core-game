package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.domain.entity.Hero;
import org.example.domain.enums.HeroType;

import java.util.List;

/**
 * Response đầy đủ cho Hero.
 */
@Data
@Builder
public class HeroResponse {
    private Long id;
    private String name;
    private HeroType type;
    private int level;
    private int stars;

    // Base stats
    private int baseHp;
    private int baseAttack;
    private int baseDefense;
    private int baseIntelligence;
    private int baseSpeed;

    // Computed stats
    private int maxHp;
    private int totalAttack;
    private int totalDefense;
    private int totalIntelligence;
    private int totalSpeed;

    // Combat stats
    private double critRate;
    private double critDamage;
    private double dodgeRate;
    private double blockRate;
    private double counterRate;
    private double stunResist;
    private double critResist;

    // Skills
    private List<SkillSummary> skills;

    public static HeroResponse from(Hero hero) {
        HeroResponseBuilder builder = HeroResponse.builder()
                .id(hero.getId())
                .name(hero.getName())
                .type(hero.getType())
                .level(hero.getLevel())
                .stars(hero.getStars())
                .baseHp(hero.getBaseHp())
                .baseAttack(hero.getBaseAttack())
                .baseDefense(hero.getBaseDefense())
                .baseIntelligence(hero.getBaseIntelligence())
                .baseSpeed(hero.getBaseSpeed())
                .maxHp(hero.getMaxHp())
                .totalAttack(hero.getTotalAttack())
                .totalDefense(hero.getTotalDefense())
                .totalIntelligence(hero.getTotalIntelligence())
                .totalSpeed(hero.getTotalSpeed())
                .critRate(hero.getCritRate())
                .critDamage(hero.getCritDamage())
                .dodgeRate(hero.getDodgeRate())
                .blockRate(hero.getBlockRate())
                .counterRate(hero.getCounterRate())
                .stunResist(hero.getStunResist())
                .critResist(hero.getCritResist());

        if (hero.getSkills() != null) {
            builder.skills(hero.getSkills().stream()
                    .map(SkillSummary::from)
                    .toList());
        }

        return builder.build();
    }
}

