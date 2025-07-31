package org.example.skills;

import org.example.BattleCharacter;
import org.example.Skill;

/**
 * Kỹ năng hồi phục HP cho đồng minh
 */
public class HealthRestore implements Skill {
    @Override
    public String getName() {
        return "Hồi Xuân Thuật";
    }

    @Override
    public String getDescription() {
        return "Phục hồi HP cho đồng minh dựa vào chỉ số trí tuệ";
    }

    @Override
    public int execute(BattleCharacter user, BattleCharacter target) {
        // Phục hồi HP cho target
        int amount = user.getCharacter().getIntelligence() * 5;
        int oldHP = target.getCurrentHealth();
        target.heal(amount);

        return target.getCurrentHealth() - oldHP; // Trả về lượng HP đã hồi phục
    }
}
