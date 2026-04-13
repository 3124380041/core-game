package org.example.engine;

import lombok.Builder;
import lombok.Data;

/**
 * Kết quả tính toán damage.
 */
@Data
@Builder
public class DamageResult {
    private int baseDamage;
    private int finalDamage;
    private boolean isCritical;
    private boolean isDodged;
    private boolean isBlocked;
    private String damageType; // PHYSICAL, MAGIC, TRUE
}

