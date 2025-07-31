package org.example.skills;

import org.example.BattleCharacter;
import org.example.Skill;

/**
 * Kỹ năng đòn đánh bóng tối, gây sát thương dựa vào tốc độ
 */
public class ShadowStrike implements Skill {
    @Override
    public String getName() {
        return "Ám Ảnh Kích";
    }

    @Override
    public String getDescription() {
        return "Tung ra đòn đánh nhanh như bóng tối, gây sát thương dựa vào tốc độ";
    }

    @Override
    public int execute(BattleCharacter user, BattleCharacter target) {
        int damage = user.getCharacter().getAgility() * 3;
        target.takeDamage(damage);
        return damage;
    }
}
