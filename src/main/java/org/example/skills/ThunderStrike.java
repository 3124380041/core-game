package org.example.skills;

import org.example.BattleCharacter;
import org.example.Skill;

/**
 * Kỹ năng sấm sét, gây sát thương dựa vào essence
 */
public class ThunderStrike implements Skill {
    @Override
    public String getName() {
        return "Lôi Đình Nhất Kích";
    }

    @Override
    public String getDescription() {
        return "Triệu hồi sấm sét đánh vào kẻ địch, gây sát thương phép thuật dựa vào essence";
    }

    @Override
    public int execute(BattleCharacter user, BattleCharacter target) {
        int damage = user.getCharacter().getIntelligence() * 4;
        target.takeDamage(damage);
        return damage;
    }
}
