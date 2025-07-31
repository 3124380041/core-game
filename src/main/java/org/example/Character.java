package org.example;

import lombok.Data;

/**
 * Represents a character in the turn-based game with various attributes.
 */
@Data
public class Character {
    private String name;
    private int strength;      // Sức mạnh vật lý của nhân vật
    private int agility;         // Sự nhanh nhẹn, tốc độ di chuyển và ra đòn
    private int vitality;  // Sức bền, khả năng chịu đựng và phòng thủ
    private int intelligence;       // Năng lượng để sử dụng các kỹ năng đặc biệt (Nguyên Khí kỹ)
    private int health;        // Máu tối đa của nhân vật
    private Skill ultimateSkill; // Kỹ năng đặc biệt của nhân vật

    /**
     * Creates a new character with the specified attributes.
     *
     * @param name The name of the character
     * @param strength The physical strength of the character
     * @param agility The agility and attack speed of the character
     * @param vitality The endurance and defense of the character
     * @param intelligence The energy for special abilities
     */
    public Character(String name, int strength, int agility, int vitality, int intelligence) {
        this.name = name;
        this.strength = strength;
        this.agility = agility;
        this.vitality = vitality;
        this.intelligence = intelligence;

        // Calculate max health based on constitution and strength
        this.health = calculateMaxHealth();
    }

    public void setStrength(int strength) {
        this.strength = strength;
        // Update max health when strength changes
        this.health = calculateMaxHealth();
    }

    public void setVitality(int vitality) {
        this.vitality = vitality;
        // Update max health when constitution changes
        this.health = calculateMaxHealth();
    }

    /**
     * Calculate the maximum health this character would have in battle.
     * 
     * @return The calculated maximum health
     */
    public int calculateMaxHealth() {
        //TODO update this formula to be more complex
        return vitality * 10 + strength * 5;
    }

    @Override
    public String toString() {
        return name + " [STR: " + strength + ", SPD: " + agility + ", VIT: " + vitality +
                          ", ESS: " + intelligence + ", Max HP: " + health + "]";
    }
}
