package org.example.domain.runtime;

import lombok.Data;
import org.example.domain.entity.Hero;
import org.example.domain.entity.HeroSkill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Runtime state của hero trong trận đấu.
 * Mở rộng từ BattleCharacter cũ.
 */
@Data
public class HeroRuntimeState {

    private Long heroId;
    private Hero hero;

    // ==================== RUNTIME STATE ====================
    private int currentHp;
    private int currentMp;  // Nộ Khí (0-100)
    private boolean alive = true;
    private int teamIndex; // 0 = team1, 1 = team2

    // Position in 3x3 grid
    private int positionRow;
    private int positionCol;

    // ==================== EFFECTS & COOLDOWNS ====================
    private List<ActiveEffect> activeEffects = new ArrayList<>();
    private Map<Long, Integer> skillCooldowns = new HashMap<>(); // skillId -> remaining cooldown

    // ==================== COMBAT FLAGS ====================
    private boolean isStunned = false;

    // ==================== CONSTANTS ====================
    public static final int MAX_MP = 100;
    public static final int INITIAL_MP = 50;
    public static final int MP_ON_ATTACK = 10;
    public static final int MP_ON_DAMAGE = 15;
    public static final int MP_PER_ROUND = 25;

    /**
     * Khởi tạo runtime state từ hero.
     */
    public HeroRuntimeState(Hero hero, int teamIndex, int row, int col) {
        this.heroId = hero.getId();
        this.hero = hero;
        this.teamIndex = teamIndex;
        this.positionRow = row;
        this.positionCol = col;

        // Initialize combat values
        this.currentHp = hero.getMaxHp();
        this.currentMp = INITIAL_MP;
        this.alive = true;

        // Initialize skill cooldowns
        for (HeroSkill skill : hero.getSkills()) {
            skillCooldowns.put(skill.getSkillTemplate().getId(), 0);
        }
    }

    // ==================== DAMAGE & HEALING ====================

    /**
     * Nhận sát thương.
     * @return Sát thương thực tế sau khi tính defense
     */
    public int takeDamage(int rawDamage) {
        int defense = hero.getTotalDefense();
        int actualDamage = Math.max(1, rawDamage - defense);

        currentHp = Math.max(0, currentHp - actualDamage);

        if (currentHp <= 0) {
            alive = false;
        } else {
            // Tăng MP khi nhận sát thương và còn sống
            increaseMp(MP_ON_DAMAGE);
        }

        return actualDamage;
    }

    /**
     * Nhận sát thương magic (defense giảm 50%).
     */
    public int takeMagicDamage(int rawDamage) {
        int defense = hero.getTotalDefense() / 2;
        int actualDamage = Math.max(1, rawDamage - defense);

        currentHp = Math.max(0, currentHp - actualDamage);

        if (currentHp <= 0) {
            alive = false;
        } else {
            increaseMp(MP_ON_DAMAGE);
        }

        return actualDamage;
    }

    /**
     * Nhận sát thương chuẩn (bypass defense).
     */
    public int takeTrueDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);

        if (currentHp <= 0) {
            alive = false;
        } else {
            increaseMp(MP_ON_DAMAGE);
        }

        return damage;
    }

    /**
     * Hồi máu.
     * @return Lượng máu thực tế được hồi
     */
    public int heal(int amount) {
        int maxHp = hero.getMaxHp();
        int actualHeal = Math.min(amount, maxHp - currentHp);
        currentHp += actualHeal;

        // Revive nếu đang chết và được heal
        if (actualHeal > 0 && !alive) {
            alive = true;
        }

        return actualHeal;
    }

    /**
     * Hồi sinh với % HP.
     */
    public int revive(double hpPercent) {
        if (alive) return 0;

        int healAmount = (int) (hero.getMaxHp() * hpPercent);
        currentHp = healAmount;
        alive = true;
        currentMp = INITIAL_MP;

        return healAmount;
    }

    // ==================== MP SYSTEM ====================

    /**
     * Tăng MP.
     */
    public void increaseMp(int amount) {
        currentMp = Math.min(MAX_MP, currentMp + amount);
    }

    /**
     * Kiểm tra có thể dùng ultimate không.
     */
    public boolean canUseUltimate() {
        return currentMp >= MAX_MP;
    }

    /**
     * Sử dụng MP cho ultimate.
     */
    public void consumeUltimateMp() {
        currentMp = currentMp - MAX_MP;
    }

    /**
     * Tăng MP sau khi tấn công.
     */
    public void onAttack() {
        increaseMp(MP_ON_ATTACK);
    }

    /**
     * Tăng MP cuối mỗi round.
     */
    public void onRoundEnd() {
        if (alive) {
            increaseMp(MP_PER_ROUND);
        }
    }

    // ==================== EFFECTS ====================

    /**
     * Thêm effect.
     */
    public void addEffect(ActiveEffect effect) {
        activeEffects.add(effect);
    }

    /**
     * Xóa effect đã hết hạn.
     */
    public void removeExpiredEffects() {
        activeEffects.removeIf(ActiveEffect::isExpired);
    }

    /**
     * Tick tất cả effects (giảm duration).
     */
    public void tickEffects() {
        for (ActiveEffect effect : activeEffects) {
            effect.tick();
        }
    }

    /**
     * Kiểm tra có bị stun không.
     */
    public boolean isStunned() {
        return activeEffects.stream()
                .anyMatch(e -> e.getEffectType().name().equals("STUN") && !e.isExpired());
    }

    // ==================== COOLDOWNS ====================

    /**
     * Kiểm tra skill có sẵn sàng không.
     */
    public boolean isSkillReady(Long skillId) {
        return skillCooldowns.getOrDefault(skillId, 0) <= 0;
    }

    /**
     * Set cooldown cho skill.
     */
    public void setSkillCooldown(Long skillId, int cooldown) {
        skillCooldowns.put(skillId, cooldown);
    }

    /**
     * Giảm cooldown cuối mỗi turn.
     */
    public void reduceCooldowns() {
        skillCooldowns.replaceAll((k, v) -> Math.max(0, v - 1));
    }

    // ==================== UTILITY ====================

    /**
     * Lấy tên hero.
     */
    public String getName() {
        return hero.getName();
    }

    /**
     * Lấy % HP còn lại.
     */
    public double getHpPercent() {
        return (double) currentHp / hero.getMaxHp();
    }

    @Override
    public String toString() {
        return hero.getName() + " [Team" + (teamIndex + 1) + 
               " | HP: " + currentHp + "/" + hero.getMaxHp() + 
               " | MP: " + currentMp + "/" + MAX_MP + 
               " | Pos: (" + positionRow + "," + positionCol + ")" +
               (alive ? "" : " DEFEATED") + 
               (isStunned() ? " STUNNED" : "") + "]";
    }
}

