package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * Request để submit action trong combat.
 */
@Data
public class CombatActionRequest {
    
    @NotNull
    private Long heroId;

    /**
     * ID của skill để sử dụng. Null = basic attack.
     */
    private Long skillId;

    /**
     * Danh sách target IDs. Có thể để trống để auto-select.
     */
    private List<Long> targetIds;

    /**
     * Action type: ATTACK, SKILL, PASS
     */
    private String actionType = "ATTACK";
}

