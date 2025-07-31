package org.example;

import lombok.Data;

import static org.example.utils.Constanst.MAX_MP;

/**
 * Represents a character's state during a battle.
 * This class wraps a Character and adds battle-specific attributes like current health.
 */
@Data
public class BattleCharacter {
    private Character character;
    private int currentHealth;
    private int currentEssence;
    private int currentMp;      // Nộ khí hiện tại
    private boolean defeated;
    private Team team;

    /**
     * Creates a new battle character based on a character.
     *
     * @param character The base character
     * @param team The team this character belongs to
     */
    public BattleCharacter(Character character, Team team) {
        this.character = character;

        // Start with full health based on character's maxHealth
        this.currentHealth = character.getHealth();

        // Start with the character's essence
        this.currentEssence = character.getIntelligence();

        this.currentMp = 50;

        this.defeated = false;
        this.team = team;
    }

    /**
     * Perform a basic attack on another battle character.
     *
     * @param target The battle character to attack
     * @return The amount of damage dealt
     */
    public int attack(BattleCharacter target) {
        int damage = calculateDamage();
        target.takeDamage(damage);

        // Tăng MP khi tấn công
        increaseMp(10);

        return damage;
    }

    /**
     * Calculate the damage for a basic attack based on strength and speed.
     *
     * @return The calculated damage
     */
    private int calculateDamage() {
        // Basic damage formula: strength * 2 + speed / 2
        return character.getStrength() * 2 + character.getAgility() / 2;
    }

    /**
     * Take damage from an attack.
     *
     * @param damage The amount of raw damage to take
     * @return The actual damage taken after defense calculations
     */
    public int takeDamage(int damage) {
        // Reduce damage based on constitution (defense)
        int actualDamage = Math.max(1, damage - (character.getVitality() / 2));
        currentHealth = Math.max(0, currentHealth - actualDamage);

        // Check if defeated
        if (currentHealth <= 0) {
            defeated = true;
        } else {
            // Tăng MP khi nhận sát thương và còn sống
            increaseMp(15);
        }

        return actualDamage;
    }

    /**
     * Use a special ability that costs essence.
     *
     * @param essenceCost The amount of essence required
     * @return true if there was enough essence to use the ability, false otherwise
     */
    public boolean useSpecialAbility(int essenceCost) {
        if (currentEssence >= essenceCost) {
            currentEssence -= essenceCost;
            return true;
        }
        return false;
    }

    /**
     * Restore some health, not exceeding max health.
     *
     * @param amount The amount of health to restore
     */
    public void heal(int amount) {
        currentHealth = Math.min(character.getHealth(), currentHealth + amount);
        if (currentHealth > 0) {
            defeated = false;
        }
    }

    /**
     * Restore some essence.
     *
     * @param amount The amount of essence to restore
     */
    public void restoreEssence(int amount) {
        currentEssence += amount;
    }

    /**
     * Tăng nộ khí sau mỗi lần tấn công hoặc bị tấn công
     * 
     * @param amount Lượng nộ khí tăng lên
     */
    public void increaseMp(int amount) {
        currentMp = Math.min(MAX_MP, currentMp + amount);
    }

    /**
     * Kiểm tra xem có thể sử dụng kỹ năng tối thượng không
     * 
     * @return true nếu nộ khí đã đầy
     */
    public boolean canUseUltimateSkill() {
        return currentMp >= MAX_MP;
    }

    /**
     * Sử dụng kỹ năng tối thượng
     * 
     * @param target Mục tiêu của kỹ năng
     * @return Sát thương gây ra
     */
    public int useUltimateSkill(BattleCharacter target) {
        if (!canUseUltimateSkill()) {
            return 0;
        }

        // Reset MP về 0 sau khi sử dụng kỹ năng
        currentMp = currentMp - MAX_MP;

        // Thực hiện kỹ năng
        return character.getUltimateSkill().execute(this, target);
    }


    /**
     * Get the character's name.
     *
     * @return The character's name
     */
    public String getName() {
        return character.getName();
    }


    /**
     * Get the maximum health.
     *
     * @return The maximum health
     */
    public int getMaxHealth() {
        return character.getHealth();
    }

    /**
     * Get the current MP value.
     *
     * @return The current MP
     */
    public int getCurrentMp() {
        return currentMp;
    }

    @Override
    public String toString() {
        return character.getName() + " [" + team + "] [STR: " + character.getStrength() + ", AGI: " + character.getAgility() +
               ", VIT: " + character.getVitality() + ", ESS: " + currentEssence + "/" + character.getIntelligence() +
               ", HP: " + currentHealth + "/" + character.getHealth() + 
                          ", MP: " + currentMp + "/" + MAX_MP + "]" +
               (defeated ? " [DEFEATED]" : ""); 
    }
}
