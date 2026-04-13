package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.domain.entity.HeroSkill;
import org.example.domain.entity.SkillTemplate;
import org.example.domain.enums.DamageType;
import org.example.domain.enums.SkillType;
import org.example.domain.enums.TargetType;

/**
 * Summary của Skill.
 */
@Data
@Builder
public class SkillSummary {
    private Long id;
    private String name;
    private String description;
    private SkillType skillType;
    private TargetType targetType;
    private DamageType damageType;
    private double scaling;
    private int cooldown;
    private int mpCost;
    private int skillLevel;

    public static SkillSummary from(HeroSkill heroSkill) {
        SkillTemplate template = heroSkill.getSkillTemplate();
        return SkillSummary.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .skillType(template.getSkillType())
                .targetType(template.getTargetType())
                .damageType(template.getDamageType())
                .scaling(heroSkill.getEffectiveScaling())
                .cooldown(heroSkill.getEffectiveCooldown())
                .mpCost(template.getMpCost())
                .skillLevel(heroSkill.getSkillLevel())
                .build();
    }
}

