package org.example.domain.enums;

/**
 * Loại sát thương.
 */
public enum DamageType {
    PHYSICAL,   // Sát thương vật lý - bị giảm bởi defense
    MAGIC,      // Sát thương phép - bị giảm bởi defense/2
    TRUE        // Sát thương chuẩn - không bị giảm
}

