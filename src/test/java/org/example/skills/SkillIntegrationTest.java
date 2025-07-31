package org.example.skills;

import org.example.*;
import org.example.Character;
import org.junit.jupiter.api.Test;

import static org.example.utils.Constanst.MAX_MP;
import static org.junit.jupiter.api.Assertions.*;

class SkillIntegrationTest {

    @Test
    void testUltimateSkillInBattleContext() {
        // Arrange
        Character heroCharacter = new Character("Hiệp Sĩ", 15, 10, 12, 8);
        heroCharacter.setUltimateSkill(new MightySlash());

        Character monsterCharacter = new Character("Quái Vật", 12, 8, 10, 5);
        monsterCharacter.setUltimateSkill(new FireballBarrage());

        BattleCharacter hero = new BattleCharacter(heroCharacter, Team.TEAM_A);
        BattleCharacter monster = new BattleCharacter(monsterCharacter, Team.TEAM_B);

        // Initial MP is 50% of max
        int initialMp = MAX_MP / 2;
        assertEquals(initialMp, hero.getCurrentMp(), "Hero should start with 50% MP");
        assertEquals(initialMp, monster.getCurrentMp(), "Monster should start with 50% MP");

        // Verify hero can't use ultimate skill yet
        assertFalse(hero.canUseUltimateSkill(), "Hero shouldn't be able to use ultimate skill with 50% MP");

        // Fill hero's MP
        hero.increaseMp(MAX_MP);
        assertTrue(hero.canUseUltimateSkill(), "Hero should be able to use ultimate skill with full MP");

        // Execute ultimate skill
        int monsterOriginalHealth = monster.getCurrentHealth();
        int damage = hero.useUltimateSkill(monster);

        // Verify damage and MP reset
        assertEquals(heroCharacter.getStrength() * 3, damage, "Damage should match MightySlash formula");
        assertTrue(monster.getCurrentHealth() < monsterOriginalHealth, "Monster should take damage");
        assertEquals(0, hero.getCurrentMp(), "Hero's MP should be reset to 0 after using ultimate");
    }

    @Test
    void testMpGainFromCombat() {
        // Arrange
        Character heroCharacter = new Character("Hiệp Sĩ", 15, 10, 12, 8);
        Character monsterCharacter = new Character("Quái Vật", 12, 8, 10, 5);

        BattleCharacter hero = new BattleCharacter(heroCharacter, Team.TEAM_A);
        BattleCharacter monster = new BattleCharacter(monsterCharacter, Team.TEAM_B);

        int initialHeroMp = hero.getCurrentMp();
        int initialMonsterMp = monster.getCurrentMp();

        // Hero attacks monster
        hero.attack(monster);

        // Verify MP gain from attacking
        assertEquals(initialHeroMp + 10, hero.getCurrentMp(), "Hero should gain 10 MP from attacking");
        assertEquals(initialMonsterMp + 15, monster.getCurrentMp(), "Monster should gain 15 MP from being attacked");
    }

    @Test
    void testHealingSkills() {
        // Arrange - Create healer and injured ally
        Character healerCharacter = new Character("Y Sư", 5, 10, 8, 8);
        healerCharacter.setUltimateSkill(new HealthRestore());

        Character allyCharacter = new Character("Đồng Đội", 10, 10, 0, 10);

        BattleCharacter healer = new BattleCharacter(healerCharacter, Team.TEAM_A);
        BattleCharacter ally = new BattleCharacter(allyCharacter, Team.TEAM_A);

        // Injure the ally
        int originalHealth = ally.getCurrentHealth();
        ally.takeDamage(50);
        assertTrue(ally.getCurrentHealth() < originalHealth, "Ally should be injured");

        // Fill healer's MP to use skill
        healer.increaseMp(MAX_MP + 10);

        // Act - Use healing skill
        int healAmount = healer.useUltimateSkill(ally);

        // Assert
        int healingAmount = healerCharacter.getIntelligence() * 5;
        assertEquals(healingAmount, healAmount, "Should heal based on intelligence");
        assertEquals(originalHealth - 50 + healAmount, ally.getCurrentHealth(),
                     "Ally health should increase by heal amount");
        assertEquals(0, healer.getCurrentMp(), "Healer's MP should be reduced by 100 after using skill");
    }

    @Test
    void testManaRestoreSkill() {
        // Arrange
        Character supportCharacter = new Character("Hiền Giả", 6, 11, 7, 17);
        supportCharacter.setUltimateSkill(new ManaRestore());

        Character allyCharacter = new Character("Đồng Đội", 10, 10, 10, 10);

        BattleCharacter support = new BattleCharacter(supportCharacter, Team.TEAM_A);
        BattleCharacter ally = new BattleCharacter(allyCharacter, Team.TEAM_A);

        // Use some of ally's MP
        ally.increaseMp(-40); // Reduce by 40
        int originalMp = ally.getCurrentMp();

        // Fill support's MP to use skill
        support.increaseMp(MAX_MP);

        // Act - Use MP restore skill
        int restoreAmount = support.useUltimateSkill(ally);

        // Assert
        assertEquals(50, restoreAmount, "Should restore fixed amount of 50 MP");
        assertEquals(originalMp + 50, ally.getCurrentMp(), "Ally MP should increase by restore amount");
        assertEquals(0, support.getCurrentMp(), "Support's MP should be reset to 0");
    }
}
