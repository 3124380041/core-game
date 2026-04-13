package org.example.domain.enums;

/**
 * Thời điểm kích hoạt effect.
 */
public enum EffectTrigger {
    IMMEDIATE,      // Ngay lập tức
    TURN_START,     // Đầu lượt
    TURN_END,       // Cuối lượt
    ON_HIT,         // Khi bị đánh
    ON_ATTACK,      // Khi tấn công
    ON_DEATH        // Khi chết
}

