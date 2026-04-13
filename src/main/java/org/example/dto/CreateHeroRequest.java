package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.example.domain.enums.HeroType;

/**
 * Request để tạo hero mới.
 */
@Data
public class CreateHeroRequest {
    
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Type is required")
    private HeroType type;

    private int stars = 1;

    // Base stats
    private int baseHp = 100;
    private int baseAttack = 10;
    private int baseDefense = 5;
    private int baseIntelligence = 10;
    private int baseSpeed = 10;

    // Combat stats (optional, có default)
    private double critRate = 0.05;
    private double dodgeRate = 0.05;
}

