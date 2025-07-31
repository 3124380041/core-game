package org.example;

/**
 * Interface định nghĩa kỹ năng của nhân vật
 */
public interface Skill {
    /**
     * Tên của kỹ năng
     */
    String getName();

    /**
     * Mô tả về kỹ năng
     */
    String getDescription();

    /**
     * Thực hiện kỹ năng
     * 
     * @param user Người sử dụng kỹ năng
     * @param target Mục tiêu của kỹ năng
     * @return Lượng sát thương gây ra (nếu là kỹ năng gây sát thương)
     */
    int execute(BattleCharacter user, BattleCharacter target);
}
