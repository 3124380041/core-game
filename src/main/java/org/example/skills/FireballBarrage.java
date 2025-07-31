package org.example.skills;

import org.example.BattleCharacter;
import org.example.Skill;

/**
 * Kỹ năng tấn công cầu lửa
 */
public class FireballBarrage implements Skill {
    @Override
    public String getName() {
        return "Hỏa Cầu Liên Hoàn";
    }

    @Override
    public String getDescription() {
        return "Phóng một loạt cầu lửa vào đối thủ, gây sát thương lớn";
    }

    @Override
    public int execute(BattleCharacter user, BattleCharacter target) {
        // Gây sát thương dựa vào essence và strength
        int damage = user.getCharacter().getIntelligence() * 3 + user.getCharacter().getAgility() * 2;
        target.takeDamage(damage);
        return damage;
    }
}
