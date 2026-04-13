//package org.example.config;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.domain.entity.*;
//import org.example.domain.enums.*;
//import org.example.domain.runtime.MatchRuntimeState;
//import org.example.dto.CombatActionRequest;
//import org.example.dto.CreateMatchRequest;
//import org.example.engine.TurnResult;
//import org.example.repository.*;
//import org.example.service.MatchService;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Khởi tạo dữ liệu và chạy test battles.
// */
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class DataInitializer implements CommandLineRunner {
//
//    private final PlayerRepository playerRepository;
//    private final HeroRepository heroRepository;
//    private final TeamRepository teamRepository;
//    private final SkillTemplateRepository skillTemplateRepository;
//    private final ItemTemplateRepository itemTemplateRepository;
//    private final EquipmentSetRepository equipmentSetRepository;
//    private final MatchService matchService;
//
//    @Override
//    @Transactional
//    public void run(String... args) {
//        log.info("");
//        log.info("╔══════════════════════════════════════════════════════════════╗");
//        log.info("║          TURN-BASED BATTLE GAME - KHỞI ĐỘNG                 ║");
//        log.info("╚══════════════════════════════════════════════════════════════╝");
//
//        // Tạo dữ liệu
//        createSkillTemplates();
//        createItemTemplates();
//        Player player1 = createPlayer1();
//        Player player2 = createPlayer2();
//
//        // Hiển thị thông tin
//        printDatabaseInfo();
//        log.info("");
//        log.info("Player 1: {} (ID: {}) - {} heroes", player1.getName(), player1.getId(),
//                heroRepository.findByOwnerId(player1.getId()).size());
//        log.info("Player 2: {} (ID: {}) - {} heroes", player2.getName(), player2.getId(),
//                heroRepository.findByOwnerId(player2.getId()).size());
//
//        // Chạy 5 trận đấu test
//        runTestBattles(player1, player2);
//    }
//
//    private void createSkillTemplates() {
//        // Chém Mạnh
//        SkillTemplate mightySlash = SkillTemplate.builder()
//                .name("Chém Mạnh")
//                .description("Tung một nhát chém mạnh gây sát thương vật lý")
//                .skillType(SkillType.ACTIVE)
//                .targetType(TargetType.SINGLE)
//                .damageType(DamageType.PHYSICAL)
//                .scaling(3.0)
//                .scalingStat("ATTACK")
//                .cooldown(2)
//                .build();
//        EffectTemplate slashDamage = EffectTemplate.builder()
//                .name("Sát thương Chém").effectType(EffectType.DAMAGE)
//                .trigger(EffectTrigger.IMMEDIATE).value(0).build();
//        mightySlash.addEffect(slashDamage);
//        skillTemplateRepository.save(mightySlash);
//
//        // Cầu Lửa
//        SkillTemplate fireball = SkillTemplate.builder()
//                .name("Cầu Lửa")
//                .description("Phóng quả cầu lửa gây sát thương diện rộng")
//                .skillType(SkillType.ACTIVE)
//                .targetType(TargetType.AOE)
//                .damageType(DamageType.MAGIC)
//                .scaling(2.0)
//                .scalingStat("INTELLIGENCE")
//                .cooldown(3)
//                .build();
//        EffectTemplate fireballDamage = EffectTemplate.builder()
//                .name("Sát thương Lửa").effectType(EffectType.DAMAGE)
//                .trigger(EffectTrigger.IMMEDIATE).value(0).build();
//        fireball.addEffect(fireballDamage);
//        skillTemplateRepository.save(fireball);
//
//        // Sấm Sét
//        SkillTemplate thunderStrike = SkillTemplate.builder()
//                .name("Sấm Sét")
//                .description("Triệu hồi sấm sét đánh vào kẻ địch")
//                .skillType(SkillType.ACTIVE)
//                .targetType(TargetType.SINGLE)
//                .damageType(DamageType.MAGIC)
//                .scaling(4.0)
//                .scalingStat("INTELLIGENCE")
//                .cooldown(4)
//                .mpCost(50)
//                .build();
//        EffectTemplate thunderDamage = EffectTemplate.builder()
//                .name("Sát thương Sấm").effectType(EffectType.DAMAGE)
//                .trigger(EffectTrigger.IMMEDIATE).value(0).build();
//        thunderStrike.addEffect(thunderDamage);
//        skillTemplateRepository.save(thunderStrike);
//
//        // Độc Kích
//        SkillTemplate poisonStrike = SkillTemplate.builder()
//                .name("Độc Kích")
//                .description("Đâm gây sát thương và nhiễm độc")
//                .skillType(SkillType.ACTIVE)
//                .targetType(TargetType.SINGLE)
//                .damageType(DamageType.PHYSICAL)
//                .scaling(2.0)
//                .scalingStat("ATTACK")
//                .cooldown(2)
//                .build();
//        EffectTemplate poisonDamage = EffectTemplate.builder()
//                .name("Sát thương Độc").effectType(EffectType.DAMAGE)
//                .trigger(EffectTrigger.IMMEDIATE).value(0).build();
//        poisonStrike.addEffect(poisonDamage);
//        skillTemplateRepository.save(poisonStrike);
//
//        // Hồi Xuân Thuật
//        SkillTemplate healthRestore = SkillTemplate.builder()
//                .name("Hồi Xuân Thuật")
//                .description("Hồi phục máu cho đồng minh")
//                .skillType(SkillType.ACTIVE)
//                .targetType(TargetType.SINGLE)
//                .damageType(DamageType.MAGIC)
//                .scaling(2.5)
//                .scalingStat("INTELLIGENCE")
//                .cooldown(2)
//                .build();
//        EffectTemplate healEffect = EffectTemplate.builder()
//                .name("Hồi máu").effectType(EffectType.HEAL)
//                .trigger(EffectTrigger.IMMEDIATE).value(30).isPercentage(true).build();
//        healthRestore.addEffect(healEffect);
//        skillTemplateRepository.save(healthRestore);
//
//        // Tăng Lực
//        SkillTemplate attackBuff = SkillTemplate.builder()
//                .name("Tăng Lực")
//                .description("Tăng sức tấn công cho bản thân")
//                .skillType(SkillType.ACTIVE)
//                .targetType(TargetType.SELF)
//                .damageType(DamageType.MAGIC)
//                .scaling(0)
//                .cooldown(3)
//                .build();
//        EffectTemplate atkBuffEffect = EffectTemplate.builder()
//                .name("Tăng Sức Mạnh").effectType(EffectType.BUFF)
//                .trigger(EffectTrigger.IMMEDIATE).targetStat(StatType.ATTACK)
//                .value(30).isPercentage(true).duration(3).build();
//        attackBuff.addEffect(atkBuffEffect);
//        skillTemplateRepository.save(attackBuff);
//
//        // Khiên Bảo Vệ
//        SkillTemplate defenseBuff = SkillTemplate.builder()
//                .name("Khiên Bảo Vệ")
//                .description("Tăng phòng thủ cho bản thân")
//                .skillType(SkillType.ACTIVE)
//                .targetType(TargetType.SELF)
//                .damageType(DamageType.MAGIC)
//                .scaling(0)
//                .cooldown(3)
//                .build();
//        EffectTemplate defBuffEffect = EffectTemplate.builder()
//                .name("Tăng Phòng Thủ").effectType(EffectType.BUFF)
//                .trigger(EffectTrigger.IMMEDIATE).targetStat(StatType.DEFENSE)
//                .value(50).isPercentage(true).duration(3).build();
//        defenseBuff.addEffect(defBuffEffect);
//        skillTemplateRepository.save(defenseBuff);
//
//        log.info("✓ Đã tạo {} skill templates", skillTemplateRepository.count());
//    }
//
//    private void createItemTemplates() {
//        // Equipment Sets
//        EquipmentSet warriorSet = EquipmentSet.builder()
//                .name("Chiến Binh Set")
//                .description("Bộ trang bị dành cho chiến binh")
//                .bonus2Attack(50).bonus4Attack(100).bonus6Attack(200)
//                .build();
//        equipmentSetRepository.save(warriorSet);
//
//        EquipmentSet mageSet = EquipmentSet.builder()
//                .name("Pháp Sư Set")
//                .description("Bộ trang bị dành cho pháp sư")
//                .bonus2Hp(100).bonus4Hp(200).bonus6Hp(400)
//                .build();
//        equipmentSetRepository.save(mageSet);
//
//        EquipmentSet tankSet = EquipmentSet.builder()
//                .name("Thủ Vệ Set")
//                .description("Bộ trang bị dành cho tank")
//                .bonus2Defense(30).bonus4Defense(60).bonus6Defense(120)
//                .build();
//        equipmentSetRepository.save(tankSet);
//
//        // Items
//        createItem("Kiếm Thép", EquipmentSlot.WEAPON_1, 3, warriorSet, 0, 50, 0, 0, 10);
//        createItem("Kiếm Lửa", EquipmentSlot.WEAPON_1, 4, warriorSet, 0, 80, 0, 0, 15);
//        createItem("Gậy Phép", EquipmentSlot.WEAPON_1, 3, mageSet, 50, 20, 0, 60, 5);
//        createItem("Khiên Sắt", EquipmentSlot.WEAPON_2, 3, tankSet, 100, 0, 40, 0, 0);
//        createItem("Giáp Chiến Binh", EquipmentSlot.ARMOR, 4, warriorSet, 150, 30, 30, 0, 5);
//        createItem("Giáp Thủ Vệ", EquipmentSlot.ARMOR, 5, tankSet, 300, 0, 60, 0, 0);
//
//        log.info("✓ Đã tạo {} item templates, {} equipment sets",
//                itemTemplateRepository.count(), equipmentSetRepository.count());
//    }
//
//    private void createItem(String name, EquipmentSlot slot, int rarity, EquipmentSet set,
//                            int hp, int atk, int def, int intel, int spd) {
//        ItemTemplate item = ItemTemplate.builder()
//                .name(name).slot(slot).rarity(rarity).equipmentSet(set)
//                .bonusHp(hp).bonusAttack(atk).bonusDefense(def)
//                .bonusIntelligence(intel).bonusSpeed(spd)
//                .build();
//        itemTemplateRepository.save(item);
//    }
//
//    private Player createPlayer1() {
//        Player player = Player.builder()
//                .username("player1").name("Người Chơi 1").level(10).build();
//        player = playerRepository.save(player);
//
//        // 10 heroes
//        List<Hero> heroes = new ArrayList<>();
//        heroes.add(createHero("Chiến Binh", HeroType.ATTACK_PHYS, 5, player, 500, 80, 40, 20, 50));
//        heroes.add(createHero("Pháp Sư", HeroType.ATTACK_MAGIC, 4, player, 300, 30, 20, 90, 60));
//        heroes.add(createHero("Hiệp Sĩ", HeroType.TANK, 5, player, 800, 50, 80, 30, 30));
//        heroes.add(createHero("Thầy Thuốc", HeroType.SUPPORT, 4, player, 350, 25, 30, 70, 55));
//        heroes.add(createHero("Sát Thủ", HeroType.ATTACK_PHYS, 4, player, 350, 100, 25, 25, 90));
//        heroes.add(createHero("Cung Thủ Lửa", HeroType.ATTACK_PHYS, 3, player, 320, 75, 25, 30, 70));
//        heroes.add(createHero("Pháp Sư Băng", HeroType.ATTACK_MAGIC, 4, player, 280, 25, 18, 85, 65));
//        heroes.add(createHero("Thủ Hộ", HeroType.TANK, 4, player, 700, 45, 70, 25, 25));
//        heroes.add(createHero("Tu Sĩ", HeroType.SUPPORT, 3, player, 320, 20, 25, 60, 50));
//        heroes.add(createHero("Kiếm Khách", HeroType.ATTACK_PHYS, 5, player, 400, 95, 35, 20, 85));
//
//        // Gán skills
//        assignSkills(heroes.get(0), "Chém Mạnh", "Tăng Lực");
//        assignSkills(heroes.get(1), "Cầu Lửa", "Sấm Sét");
//        assignSkills(heroes.get(2), "Khiên Bảo Vệ");
//        assignSkills(heroes.get(3), "Hồi Xuân Thuật");
//        assignSkills(heroes.get(4), "Độc Kích", "Chém Mạnh");
//        assignSkills(heroes.get(5), "Độc Kích");
//        assignSkills(heroes.get(6), "Sấm Sét", "Cầu Lửa");
//        assignSkills(heroes.get(7), "Khiên Bảo Vệ", "Tăng Lực");
//        assignSkills(heroes.get(8), "Hồi Xuân Thuật");
//        assignSkills(heroes.get(9), "Chém Mạnh", "Độc Kích");
//
//        // Team (5 heroes)
//        Team team = Team.builder().name("Đội Alpha").player(player).build();
//        team = teamRepository.save(team);
//        team.addHero(heroes.get(2), 0, 1);  // Hiệp Sĩ - Front center
//        team.addHero(heroes.get(0), 0, 0);  // Chiến Binh - Front left
//        team.addHero(heroes.get(4), 1, 2);  // Sát Thủ - Middle right
//        team.addHero(heroes.get(1), 2, 0);  // Pháp Sư - Back left
//        team.addHero(heroes.get(3), 2, 2);  // Thầy Thuốc - Back right
//        team = teamRepository.save(team);
//
//        player.setActiveTeam(team);
//        log.info("✓ Player 1: {} heroes, team '{}' có {} heroes",
//                heroes.size(), team.getName(), team.getHeroCount());
//        return playerRepository.save(player);
//    }
//
//    private Player createPlayer2() {
//        Player player = Player.builder()
//                .username("player2").name("Người Chơi 2").level(10).build();
//        player = playerRepository.save(player);
//
//        // 10 heroes
//        List<Hero> heroes = new ArrayList<>();
//        heroes.add(createHero("Kỵ Sĩ", HeroType.TANK, 5, player, 750, 55, 75, 25, 35));
//        heroes.add(createHero("Cung Thủ", HeroType.ATTACK_PHYS, 4, player, 320, 85, 30, 35, 80));
//        heroes.add(createHero("Phù Thủy", HeroType.ATTACK_MAGIC, 5, player, 280, 25, 25, 95, 55));
//        heroes.add(createHero("Linh Mục", HeroType.SUPPORT, 4, player, 380, 20, 35, 65, 50));
//        heroes.add(createHero("Ninja", HeroType.ATTACK_PHYS, 4, player, 300, 95, 20, 30, 95));
//        heroes.add(createHero("Đấu Sĩ", HeroType.ATTACK_PHYS, 4, player, 450, 70, 45, 25, 55));
//        heroes.add(createHero("Thần Bí", HeroType.ATTACK_MAGIC, 3, player, 260, 20, 20, 80, 60));
//        heroes.add(createHero("Vệ Binh", HeroType.TANK, 3, player, 650, 40, 65, 20, 30));
//        heroes.add(createHero("Đạo Sĩ", HeroType.SUPPORT, 4, player, 340, 25, 30, 70, 55));
//        heroes.add(createHero("Sát Thủ Đêm", HeroType.ATTACK_PHYS, 5, player, 320, 105, 22, 25, 100));
//
//        // Gán skills
//        assignSkills(heroes.get(0), "Khiên Bảo Vệ", "Tăng Lực");
//        assignSkills(heroes.get(1), "Độc Kích", "Chém Mạnh");
//        assignSkills(heroes.get(2), "Sấm Sét", "Cầu Lửa");
//        assignSkills(heroes.get(3), "Hồi Xuân Thuật");
//        assignSkills(heroes.get(4), "Chém Mạnh", "Tăng Lực");
//        assignSkills(heroes.get(5), "Chém Mạnh", "Khiên Bảo Vệ");
//        assignSkills(heroes.get(6), "Cầu Lửa");
//        assignSkills(heroes.get(7), "Khiên Bảo Vệ");
//        assignSkills(heroes.get(8), "Hồi Xuân Thuật");
//        assignSkills(heroes.get(9), "Độc Kích", "Chém Mạnh");
//
//        // Team (5 heroes)
//        Team team = Team.builder().name("Đội Beta").player(player).build();
//        team = teamRepository.save(team);
//        team.addHero(heroes.get(0), 0, 1);  // Kỵ Sĩ - Front center
//        team.addHero(heroes.get(1), 1, 0);  // Cung Thủ - Middle left
//        team.addHero(heroes.get(4), 1, 2);  // Ninja - Middle right
//        team.addHero(heroes.get(2), 2, 1);  // Phù Thủy - Back center
//        team.addHero(heroes.get(3), 2, 2);  // Linh Mục - Back right
//        team = teamRepository.save(team);
//
//        player.setActiveTeam(team);
//        log.info("✓ Player 2: {} heroes, team '{}' có {} heroes",
//                heroes.size(), team.getName(), team.getHeroCount());
//        return playerRepository.save(player);
//    }
//
//    private Hero createHero(String name, HeroType type, int stars, Player owner,
//                            int hp, int atk, int def, int intel, int spd) {
//        Hero hero = Hero.builder()
//                .name(name).type(type).level(30).stars(stars)
//                .baseHp(hp).baseAttack(atk).baseDefense(def)
//                .baseIntelligence(intel).baseSpeed(spd)
//                .critRate(0.1 + (stars * 0.02)).critDamage(1.5)
//                .dodgeRate(type == HeroType.ATTACK_PHYS ? 0.1 : 0.05)
//                .blockRate(type == HeroType.TANK ? 0.2 : 0.05)
//                .owner(owner)
//                .build();
//        hero = heroRepository.save(hero);
//        owner.addHero(hero);
//        return hero;
//    }
//
//    private void assignSkills(Hero hero, String... skillNames) {
//        int slot = 0;
//        for (String skillName : skillNames) {
//            skillTemplateRepository.findByName(skillName).ifPresent(template -> {
//                HeroSkill heroSkill = HeroSkill.builder()
//                        .hero(hero).skillTemplate(template).skillLevel(3)
//                        .slotIndex(hero.getSkills().size()).build();
//                hero.getSkills().add(heroSkill);
//            });
//        }
//        heroRepository.save(hero);
//    }
//
//    private void printDatabaseInfo() {
//        log.info("");
//        log.info("📊 DỮ LIỆU ĐÃ TẠO:");
//        log.info("  ├── Players: {}", playerRepository.count());
//        log.info("  ├── Heroes: {}", heroRepository.count());
//        log.info("  ├── Skills: {}", skillTemplateRepository.count());
//        log.info("  ├── Items: {}", itemTemplateRepository.count());
//        log.info("  └── Equipment Sets: {}", equipmentSetRepository.count());
//    }
//
//    /**
//     * Chạy 5 trận đấu test và in log.
//     */
//    private void runTestBattles(Player player1, Player player2) {
//        log.info("");
//        log.info("╔══════════════════════════════════════════════════════════════╗");
//        log.info("║               CHẠY 5 TRẬN ĐẤU TEST                           ║");
//        log.info("╚══════════════════════════════════════════════════════════════╝");
//
//        int player1Wins = 0;
//        int player2Wins = 0;
//
//        for (int i = 1; i <= 5; i++) {
//            log.info("");
//            log.info("┌──────────────────────────────────────────────────────────────┐");
//            log.info("│                    TRẬN ĐẤU #{} / 5                           │", i);
//            log.info("│        {} vs {}            │",
//                    String.format("%-18s", player1.getName()),
//                    String.format("%-18s", player2.getName()));
//            log.info("└──────────────────────────────────────────────────────────────┘");
//
//            try {
//                // Tạo match
//                CreateMatchRequest request = new CreateMatchRequest();
//                request.setPlayer1Id(player1.getId());
//                request.setPlayer2Id(player2.getId());
//
//                Match match = matchService.createMatch(request);
//                log.info("📋 Match #{} đã được tạo (ID: {})", i, match.getId());
//
//                // Lấy trạng thái match
//                MatchRuntimeState state = matchService.getMatchState(match.getId());
//
//                // In ra đội hình
//                printTeamLineup("🔵 Team 1 - " + player1.getName(), state.getTeam1States());
//                printTeamLineup("🔴 Team 2 - " + player2.getName(), state.getTeam2States());
//
//                log.info("");
//                log.info("⚔️  BẮT ĐẦU TRẬN ĐẤU ⚔️");
//                log.info("────────────────────────────────────────");
//
//                // Simulate battle
//                int turnCount = 0;
//                int maxTurns = 100;
//
//                while (state.isInProgress() && turnCount < maxTurns) {
//                    var currentHero = state.getCurrentTurnHero();
//                    if (currentHero == null) break;
//
//                    // Tạo action (auto attack)
//                    CombatActionRequest action = new CombatActionRequest();
//                    action.setHeroId(currentHero.getHeroId());
//                    action.setActionType("ATTACK");
//
//                    Long playerId = currentHero.getTeamIndex() == 0
//                            ? player1.getId()
//                            : player2.getId();
//
//                    TurnResult result = matchService.submitAction(match.getId(), playerId, action);
//
//                    // In log turn (chỉ in những turn quan trọng)
//                    if (!result.isTurnSkipped() && result.getMessage() != null) {
//                        String teamIcon = currentHero.getTeamIndex() == 0 ? "🔵" : "🔴";
//                        log.info("  {} Turn {}: {}", teamIcon, turnCount + 1, result.getMessage());
//                    }
//
//                    // Refresh state
//                    state = matchService.getMatchState(match.getId());
//                    turnCount++;
//                }
//
//                // Kết quả
//                log.info("────────────────────────────────────────");
//                log.info("📊 KẾT THÚC TRẬN ĐẤU - {} lượt", turnCount);
//
//                if (state.getWinnerTeamIndex() != null) {
//                    String winner = state.getWinnerTeamIndex() == 0
//                            ? player1.getName()
//                            : player2.getName();
//                    String icon = state.getWinnerTeamIndex() == 0 ? "🔵" : "🔴";
//                    log.info("{} 🏆 CHIẾN THẮNG: {}", icon, winner);
//
//                    if (state.getWinnerTeamIndex() == 0) player1Wins++;
//                    else player2Wins++;
//                } else {
//                    log.info("⚖️  Trận đấu hòa hoặc chưa kết thúc");
//                }
//
//                // In trạng thái cuối
//                printFinalState(state);
//
//            } catch (Exception e) {
//                log.error("❌ Lỗi trong trận đấu #{}: {}", i, e.getMessage(), e);
//            }
//        }
//
//        // Tổng kết
//        log.info("");
//        log.info("╔══════════════════════════════════════════════════════════════╗");
//        log.info("║                    TỔNG KẾT 5 TRẬN                           ║");
//        log.info("╠══════════════════════════════════════════════════════════════╣");
//        log.info("║  🔵 {}: {} trận thắng                          ║",
//                String.format("%-15s", player1.getName()), player1Wins);
//        log.info("║  🔴 {}: {} trận thắng                          ║",
//                String.format("%-15s", player2.getName()), player2Wins);
//        log.info("╚══════════════════════════════════════════════════════════════╝");
//
//        if (player1Wins > player2Wins) {
//            log.info("🎉 {} là người chơi xuất sắc nhất!", player1.getName());
//        } else if (player2Wins > player1Wins) {
//            log.info("🎉 {} là người chơi xuất sắc nhất!", player2.getName());
//        } else {
//            log.info("🤝 Hai người chơi ngang tài ngang sức!");
//        }
//    }
//
//    private void printTeamLineup(String teamName, List<org.example.domain.runtime.HeroRuntimeState> states) {
//        log.info("");
//        log.info("{}:", teamName);
//        log.info("  ┌─────────────────────┬──────┬──────┬──────┬─────┐");
//        log.info("  │ Hero                │  HP  │  ATK │  DEF │ SPD │");
//        log.info("  ├─────────────────────┼──────┼──────┼──────┼─────┤");
//        for (var state : states) {
//            String heroName = state.getName();
//            if (heroName.length() > 17) heroName = heroName.substring(0, 17) + "..";
//            log.info("  │ {} │ {:>4} │ {:>4} │ {:>4} │ {:>3} │",
//                    String.format("%-19s", heroName),
//                    state.getCurrentHp(),
//                    state.getHero().getTotalAttack(),
//                    state.getHero().getTotalDefense(),
//                    state.getHero().getTotalSpeed());
//        }
//        log.info("  └─────────────────────┴──────┴──────┴──────┴─────┘");
//    }
//
//    private void printFinalState(MatchRuntimeState state) {
//        log.info("");
//        log.info("📋 Trạng thái cuối cùng:");
//
//        int team1Alive = (int) state.getTeam1States().stream()
//                .filter(org.example.domain.runtime.HeroRuntimeState::isAlive).count();
//        int team2Alive = (int) state.getTeam2States().stream()
//                .filter(org.example.domain.runtime.HeroRuntimeState::isAlive).count();
//
//        log.info("  🔵 Team 1: {}/5 còn sống", team1Alive);
//        for (var h : state.getTeam1States()) {
//            String status = h.isAlive()
//                    ? String.format("💚 HP: %d/%d (%.0f%%)", h.getCurrentHp(), h.getHero().getMaxHp(),
//                            h.getCurrentHp() * 100.0 / h.getHero().getMaxHp())
//                    : "💀 DEFEATED";
//            log.info("     - {}: {}", h.getName(), status);
//        }
//
//        log.info("  🔴 Team 2: {}/5 còn sống", team2Alive);
//        for (var h : state.getTeam2States()) {
//            String status = h.isAlive()
//                    ? String.format("💚 HP: %d/%d (%.0f%%)", h.getCurrentHp(), h.getHero().getMaxHp(),
//                            h.getCurrentHp() * 100.0 / h.getHero().getMaxHp())
//                    : "💀 DEFEATED";
//            log.info("     - {}: {}", h.getName(), status);
//        }
//    }
//}
//
