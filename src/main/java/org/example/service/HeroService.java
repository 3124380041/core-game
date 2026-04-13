package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.*;
import org.example.domain.enums.HeroType;
import org.example.dto.CreateHeroRequest;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.HeroRepository;
import org.example.repository.PlayerRepository;
import org.example.repository.SkillTemplateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service quản lý Hero.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class HeroService {

    private final HeroRepository heroRepository;
    private final PlayerRepository playerRepository;
    private final SkillTemplateRepository skillTemplateRepository;

    /**
     * Tạo hero mới cho player.
     */
    public Hero createHero(Long playerId, CreateHeroRequest request) {
        Player owner = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player không tồn tại: " + playerId));

        Hero hero = Hero.builder()
                .name(request.getName())
                .type(request.getType())
                .level(1)
                .stars(request.getStars())
                .baseHp(request.getBaseHp())
                .baseAttack(request.getBaseAttack())
                .baseDefense(request.getBaseDefense())
                .baseIntelligence(request.getBaseIntelligence())
                .baseSpeed(request.getBaseSpeed())
                .critRate(request.getCritRate())
                .dodgeRate(request.getDodgeRate())
                .owner(owner)
                .build();

        Hero saved = heroRepository.save(hero);
        owner.addHero(saved);
        playerRepository.save(owner);

        log.info("Tạo hero mới: {} ({}) cho player {}", saved.getName(), saved.getId(), owner.getName());
        return saved;
    }

    /**
     * Lấy hero theo ID.
     */
    @Transactional(readOnly = true)
    public Hero getHero(Long id) {
        return heroRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hero không tồn tại: " + id));
    }

    /**
     * Lấy tất cả heroes của player.
     */
    @Transactional(readOnly = true)
    public List<Hero> getHeroesByPlayer(Long playerId) {
        return heroRepository.findByOwnerId(playerId);
    }

    /**
     * Lấy tất cả heroes trong hệ thống.
     */
    @Transactional(readOnly = true)
    public List<Hero> getAllHeroes() {
        return heroRepository.findAll();
    }

    /**
     * Lấy heroes theo type.
     */
    @Transactional(readOnly = true)
    public List<Hero> getHeroesByType(HeroType type) {
        return heroRepository.findByType(type);
    }

    /**
     * Tăng level hero.
     */
    public Hero levelUp(Long heroId) {
        Hero hero = getHero(heroId);
        hero.setLevel(hero.getLevel() + 1);

        // Tăng stats theo level
        hero.setBaseHp((int) (hero.getBaseHp() * 1.05));
        hero.setBaseAttack((int) (hero.getBaseAttack() * 1.03));
        hero.setBaseDefense((int) (hero.getBaseDefense() * 1.03));
        hero.setBaseIntelligence((int) (hero.getBaseIntelligence() * 1.03));
        hero.setBaseSpeed((int) (hero.getBaseSpeed() * 1.01));

        log.info("Hero {} tăng lên level {}", hero.getName(), hero.getLevel());
        return heroRepository.save(hero);
    }

    /**
     * Tăng sao cho hero (rarity upgrade).
     */
    public Hero starUp(Long heroId) {
        Hero hero = getHero(heroId);
        if (hero.getStars() >= 6) {
            throw new IllegalStateException("Hero đã đạt max sao (6★)");
        }

        hero.setStars(hero.getStars() + 1);

        // Tăng stats theo sao
        hero.setBaseHp((int) (hero.getBaseHp() * 1.15));
        hero.setBaseAttack((int) (hero.getBaseAttack() * 1.1));
        hero.setBaseDefense((int) (hero.getBaseDefense() * 1.1));
        hero.setBaseIntelligence((int) (hero.getBaseIntelligence() * 1.1));
        hero.setCritRate(Math.min(1.0, hero.getCritRate() + 0.02));

        log.info("Hero {} tăng lên {}★", hero.getName(), hero.getStars());
        return heroRepository.save(hero);
    }

    /**
     * Gán skill cho hero.
     */
    public Hero assignSkill(Long heroId, Long skillTemplateId, int slotIndex) {
        Hero hero = getHero(heroId);

        if (slotIndex < 0 || slotIndex > 3) {
            throw new IllegalArgumentException("Slot index phải từ 0-3");
        }

        SkillTemplate skillTemplate = skillTemplateRepository.findById(skillTemplateId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill không tồn tại: " + skillTemplateId));

        // Remove existing skill at slot
        hero.getSkills().removeIf(s -> s.getSlotIndex() == slotIndex);

        // Add new skill
        HeroSkill heroSkill = HeroSkill.builder()
                .hero(hero)
                .skillTemplate(skillTemplate)
                .skillLevel(1)
                .slotIndex(slotIndex)
                .build();

        hero.getSkills().add(heroSkill);

        log.info("Gán skill {} cho hero {} tại slot {}", skillTemplate.getName(), hero.getName(), slotIndex);
        return heroRepository.save(hero);
    }

    /**
     * Nâng cấp skill.
     */
    public Hero upgradeSkill(Long heroId, int slotIndex) {
        Hero hero = getHero(heroId);

        HeroSkill skill = hero.getSkills().stream()
                .filter(s -> s.getSlotIndex() == slotIndex)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không có skill tại slot " + slotIndex));

        if (skill.getSkillLevel() >= 10) {
            throw new IllegalStateException("Skill đã đạt max level (10)");
        }

        skill.setSkillLevel(skill.getSkillLevel() + 1);
        log.info("Nâng cấp skill {} lên level {}", skill.getSkillTemplate().getName(), skill.getSkillLevel());

        return heroRepository.save(hero);
    }
}

