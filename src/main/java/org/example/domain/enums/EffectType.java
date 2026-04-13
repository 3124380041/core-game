package org.example.domain.enums;

/**
 * Loại effect.
 */
public enum EffectType {
    DAMAGE,     // Gây sát thương
    HEAL,       // Hồi máu
    BUFF,       // Tăng chỉ số
    DEBUFF,     // Giảm chỉ số
    POISON,     // Trúng độc (DOT)
    BURN,       // Bỏng (DOT + giảm defense)
    STUN,       // Choáng (bỏ lượt)
    REVIVE      // Hồi sinh
}

