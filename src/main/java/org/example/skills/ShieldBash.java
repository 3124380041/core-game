package org.example.skills;

import org.example.BattleCharacter;
import org.example.Skill;

/**
 * Kỹ năng của nhân vật tanker
 */
public class ShieldBash implements Skill {
    @Override
    public String getName() {
        return "Lá Chắn Thần";
    }

    @Override
    public String getDescription() {
        return "Đập khiên vào kẻ địch, gây sát thương dựa vào chỉ số constitution và cung cấp khiên bảo vệ cho bản thân";
    }

    @Override
    public int execute(BattleCharacter user, BattleCharacter target) {
        // Gây sát thương dựa vào chỉ số constitution
        int damage = user.getCharacter().getVitality() * 2;
        target.takeDamage(damage);

        // Tự hồi máu, tăng khả năng phòng thủ
        user.heal(15);

        return damage;
    }
}
