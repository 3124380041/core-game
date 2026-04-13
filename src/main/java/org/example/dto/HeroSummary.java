package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.domain.entity.Hero;
import org.example.domain.enums.HeroType;

/**
 * Summary của Hero (cho list).
 */
@Data
@Builder
public class HeroSummary {
    private Long id;
    private String name;
    private HeroType type;
    private int level;
    private int stars;
    private int maxHp;
    private int attack;
    private int defense;
    private int speed;

    public static HeroSummary from(Hero hero) {
        return HeroSummary.builder()
                .id(hero.getId())
                .name(hero.getName())
                .type(hero.getType())
                .level(hero.getLevel())
                .stars(hero.getStars())
                .maxHp(hero.getMaxHp())
                .attack(hero.getTotalAttack())
                .defense(hero.getTotalDefense())
                .speed(hero.getTotalSpeed())
                .build();
    }
}

