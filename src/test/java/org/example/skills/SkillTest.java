package org.example.skills;

import org.example.BattleCharacter;
import org.example.Character;
import org.example.Skill;
import org.example.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SkillTest {

    private Character attackerChar;
    private Character healerChar;
    private Character targetChar;
    private BattleCharacter attacker;
    private BattleCharacter target;
    private BattleCharacter healer;

    @BeforeEach
    void setUp() {
        // Initialize characters for testing
        attackerChar = new Character("Attacker", 10, 10, 10, 10);
        targetChar = new Character("Target", 8, 8, 8, 8);
        healerChar = new Character("Healer", 10, 10, 10, 10);

        // Create battle characters
        attacker = new BattleCharacter(attackerChar, Team.TEAM_A);
        target = new BattleCharacter(targetChar, Team.TEAM_B);
        healer = new BattleCharacter(healerChar, Team.TEAM_B);

        // Ensure target has full health
        int maxHealth = targetChar.getHealth();
        while (target.getCurrentHealth() < maxHealth) {
            target.heal(maxHealth);
        }
    }

    @Test
    void testThunderStrike() {
        // Arrange
        Skill skill = new ThunderStrike();
        int expectedDamage = attackerChar.getIntelligence() * 4;
        int targetOriginalHealth = target.getCurrentHealth();

        // Act
        int actualDamage = skill.execute(attacker, target);

        // Assert
        assertEquals(expectedDamage, actualDamage, "Damage calculation should match formula");
        assertEquals(targetOriginalHealth - actualDamage + (targetChar.getVitality() / 2),
                target.getCurrentHealth(), "Target health should be reduced by damage minus defense");

        // Verify skill properties
        assertEquals("Lôi Đình Nhất Kích", skill.getName(), "Skill name should match");
        assertTrue(skill.getDescription().contains("sấm sét"), "Skill description should contain key elements");
    }

    @Test
    void testMightySlash() {
        // Arrange
        Skill skill = new MightySlash();
        int expectedDamage = attackerChar.getStrength() * 3;
        int targetOriginalHealth = target.getCurrentHealth();

        // Act
        int actualDamage = skill.execute(attacker, target);

        // Assert
        assertEquals(expectedDamage, actualDamage, "Damage calculation should match formula");
        assertEquals(targetOriginalHealth - actualDamage + (targetChar.getVitality() / 2),
                target.getCurrentHealth(), "Target health should be reduced by damage minus defense");
    }

    @Test
    void testShadowStrike() {
        // Arrange
        Skill skill = new ShadowStrike();
        int expectedDamage = attackerChar.getAgility() * 3;
        int targetOriginalHealth = target.getCurrentHealth();

        // Act
        int actualDamage = skill.execute(attacker, target);

        // Assert
        assertEquals(expectedDamage, actualDamage, "Damage calculation should match formula");
        assertEquals(targetOriginalHealth - actualDamage + (targetChar.getVitality() / 2),
                target.getCurrentHealth(), "Target health should be reduced by damage minus defense");
    }

    @Test
    void testFireballBarrage() {
        // Arrange
        Skill skill = new FireballBarrage();
        int expectedDamage = attackerChar.getIntelligence() * 3 + attackerChar.getAgility() * 2;
        int targetOriginalHealth = target.getCurrentHealth();

        // Act
        int actualDamage = skill.execute(attacker, target);

        // Assert
        assertEquals(expectedDamage, actualDamage, "Damage calculation should match formula");
        assertEquals(targetOriginalHealth - actualDamage + (targetChar.getVitality() / 2),
                target.getCurrentHealth(), "Target health should be reduced by damage minus defense");
    }

    @Test
    void testPoisonStrike() {
        // Arrange
        Skill skill = new PoisonStrike();
        int expectedDamage = attackerChar.getStrength() * 2 + attackerChar.getAgility() * 2;
        int targetOriginalHealth = target.getCurrentHealth();
        int attackerOriginalHealth = attacker.getCurrentHealth();

        // Damage attacker to test healing
        attacker.takeDamage(20);
        attackerOriginalHealth = attacker.getCurrentHealth();

        // Act
        int actualDamage = skill.execute(attacker, target);

        // Assert
        assertEquals(expectedDamage, actualDamage, "Damage calculation should match formula");
        assertEquals(targetOriginalHealth - actualDamage + (targetChar.getVitality() / 2),
                target.getCurrentHealth(), "Target health should be reduced by damage minus defense");
        assertTrue(attacker.getCurrentHealth() > attackerOriginalHealth,
                "Attacker should heal from poison strike");
        assertEquals(attackerOriginalHealth + (actualDamage / 3),
                attacker.getCurrentHealth(), "Attacker should heal by 1/3 of damage");
    }

    @Test
    void testHealthRestore() {
        // Arrange
        Skill skill = new HealthRestore();

        // Damage target to test healing
        target.takeDamage(100);
        int targetOriginalHealth = target.getCurrentHealth();

        // Act
        int healAmount = skill.execute(healer, target);

        // Assert
        int healingValue = healer.getCharacter().getIntelligence() * 5;
        assertEquals(healingValue, healAmount, "Should heal based on intelligence");
        assertEquals(targetOriginalHealth + healingValue,
                target.getCurrentHealth(), "Target health should increase by heal amount");
    }

    @Test
    void testHealthRestoreWithCapAtMaxHealth() {
        // Arrange
        Skill skill = new HealthRestore();

        // Damage target just a little
        target.takeDamage(10);
        int targetOriginalHealth = target.getCurrentHealth();
        int maxHealth = targetChar.getHealth();

        // Act
        int healAmount = skill.execute(attacker, target);

        // Assert
        assertEquals(maxHealth - targetOriginalHealth, healAmount,
                "Should heal only up to max health");
        assertEquals(maxHealth, target.getCurrentHealth(),
                "Target health should be capped at max health");
    }

    @Test
    void testManaRestore() {
        // Arrange
        Skill skill = new ManaRestore();

        // Ensure target has some MP used
        target.increaseMp(-70); // Reduce MP by 70
        int targetOriginalMp = target.getCurrentMp();

        // Act
        int restoredAmount = skill.execute(attacker, target);

        // Assert
        assertEquals(50, restoredAmount, "Should restore fixed amount of 50 MP");
        assertEquals(targetOriginalMp + 50, target.getCurrentMp(),
                "Target MP should increase by the restored amount");
    }

    @Test
    void testShieldBash() {
        // Arrange
        Skill skill = new ShieldBash();
        int expectedDamage = attackerChar.getVitality() * 2;
        int targetOriginalHealth = target.getCurrentHealth();

        // Damage attacker to test self-healing
        attacker.takeDamage(30);
        int attackerOriginalHealth = attacker.getCurrentHealth();

        // Act
        int actualDamage = skill.execute(attacker, target);

        // Assert
        assertEquals(expectedDamage, actualDamage, "Damage calculation should match formula");
        assertEquals(targetOriginalHealth - actualDamage + (targetChar.getVitality() / 2),
                target.getCurrentHealth(), "Target health should be reduced by damage minus defense");
        assertEquals(attackerOriginalHealth + 15, attacker.getCurrentHealth(),
                "Attacker should heal by 15 from shield bash");
    }
}
