package org.example.skills;

import org.example.BattleCharacter;
import org.example.Skill;

/**
 * Kỹ năng hồi phục MP cho đồng minh
 */
public class ManaRestore implements Skill {
    @Override
    public String getName() {
        return "Khí Nguyên Chú";
    }

    @Override
    public String getDescription() {
        return "Phục hồi 50 điểm nộ khí cho đồng minh";
    }

    @Override
    public int execute(BattleCharacter user, BattleCharacter target) {
        // Phục hồi MP cho target
        int amount = 50;
        int oldMp = target.getCurrentMp();
        target.increaseMp(amount);
        int actualRestored = target.getCurrentMp() - oldMp;

        return actualRestored; // Trả về lượng MP đã hồi phục
    }
}
