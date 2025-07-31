package org.example.skills;

import org.example.BattleCharacter;
import org.example.Skill;

/**
 * Kỹ năng chém mạnh, gây sát thương dựa vào sức mạnh
 */
public class MightySlash implements Skill {
    @Override
    public String getName() {
        return "Chém Mạnh";
    }

    @Override
    public String getDescription() {
        return "Tung một đòn chém cực mạnh, gây sát thương gấp 3 lần sức mạnh";
    }

    @Override
    public int execute(BattleCharacter user, BattleCharacter target) {
        int damage = user.getCharacter().getStrength() * 3;
        target.takeDamage(damage);
        return damage;
    }
}
