package org.example.domain.enums;

/**
 * Loại mục tiêu của skill.
 */
public enum TargetType {
    SINGLE,         // Một mục tiêu
    AOE,            // Tất cả đối thủ
    ROW,            // Một hàng (trong grid 3x3)
    COLUMN,         // Một cột (trong grid 3x3)
    LOWEST_HP,      // Mục tiêu có HP thấp nhất
    RANDOM,         // Ngẫu nhiên
    SELF,           // Bản thân
    ALL_ALLIES      // Tất cả đồng minh
}

