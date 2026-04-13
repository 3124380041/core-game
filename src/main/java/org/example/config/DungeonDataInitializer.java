package org.example.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.Dungeon;
import org.example.domain.entity.DungeonMap;
import org.example.domain.entity.Hero;
import org.example.domain.entity.Team;
import org.example.domain.enums.HeroType;
import org.example.repository.DungeonRepository;
import org.example.repository.HeroRepository;
import org.example.repository.TeamRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seed du lieu pho ban co ban de test logic PvE.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DungeonDataInitializer implements CommandLineRunner {

    private final DungeonRepository dungeonRepository;
    private final TeamRepository teamRepository;
    private final HeroRepository heroRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (dungeonRepository.count() > 0) {
            return;
        }

        Team map1Team = createEnemyTeam(
                "Slime Camp",
                createMonster("Slime Xanh", HeroType.TANK, 260, 28, 18, 10, 20),
                createMonster("Slime Do", HeroType.ATTACK_PHYS, 220, 34, 14, 8, 24),
                createMonster("Slime Phep", HeroType.ATTACK_MAGIC, 200, 20, 10, 34, 22)
        );

        Team map2Team = createEnemyTeam(
                "Goblin Squad",
                createMonster("Goblin Chien", HeroType.ATTACK_PHYS, 300, 42, 18, 10, 30),
                createMonster("Goblin No", HeroType.ATTACK_MAGIC, 250, 20, 12, 44, 26),
                createMonster("Goblin Thu Linh", HeroType.TANK, 420, 36, 30, 16, 18)
        );

        Dungeon dungeon = Dungeon.builder()
                .code("DUNGEON_FOREST_1")
                .name("Rung Toi Tap 1")
                .description("Vuot 2 map quai de dọn sạch khu rung")
                .recommendedPower(1000)
                .build();
        dungeon = dungeonRepository.save(dungeon);

        DungeonMap map1 = DungeonMap.builder()
                .dungeon(dungeon)
                .mapIndex(1)
                .name("Bai dat Slime")
                .enemyTeam(map1Team)
                .firstClearGoldReward(100)
                .build();

        DungeonMap map2 = DungeonMap.builder()
                .dungeon(dungeon)
                .mapIndex(2)
                .name("Trai Goblin")
                .enemyTeam(map2Team)
                .firstClearGoldReward(250)
                .build();

        dungeon.getMaps().add(map1);
        dungeon.getMaps().add(map2);
        dungeonRepository.save(dungeon);

        log.info("Seed dungeon data completed: {} with {} maps", dungeon.getCode(), dungeon.getMaps().size());
    }

    private Team createEnemyTeam(String teamName, Hero h1, Hero h2, Hero h3) {
        Team team = Team.builder().name(teamName).build();

        team.addHero(h1, 0, 0);
        team.addHero(h2, 0, 1);
        team.addHero(h3, 1, 1);

        return teamRepository.save(team);
    }

    private Hero createMonster(String name,
                               HeroType type,
                               int hp,
                               int atk,
                               int def,
                               int intelligence,
                               int speed) {
        Hero hero = Hero.builder()
                .name(name)
                .type(type)
                .level(1)
                .stars(1)
                .baseHp(hp)
                .baseAttack(atk)
                .baseDefense(def)
                .baseIntelligence(intelligence)
                .baseSpeed(speed)
                .critRate(0.03)
                .critDamage(1.3)
                .dodgeRate(0.02)
                .build();

        return heroRepository.save(hero);
    }
}

