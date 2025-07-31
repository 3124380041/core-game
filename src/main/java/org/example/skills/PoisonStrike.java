package org.example.skills;

import org.example.BattleCharacter;
import org.example.Skill;

/**
 * Kỹ năng tấn công độc
 */
public class PoisonStrike implements Skill {
    @Override
    public String getName() {
        return "Độc Trảo";
    }

    @Override
    public String getDescription() {
        return "Tấn công bằng móng vuốt tẩm độc, gây sát thương và hồi máu cho bản thân";
    }

    @Override
    public int execute(BattleCharacter user, BattleCharacter target) {
        // Gây sát thương dựa vào strength và speed
        int damage = user.getCharacter().getStrength() * 2 + user.getCharacter().getAgility() * 2;
        target.takeDamage(damage);

        // Hút máu về bản thân
        user.heal(damage / 3);

        return damage;
    }
}
