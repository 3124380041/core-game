package org.example.engine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.*;
import org.example.domain.enums.DamageType;
import org.example.domain.enums.EffectType;
import org.example.domain.enums.TargetType;
import org.example.domain.runtime.HeroRuntimeState;
import org.example.domain.runtime.MatchRuntimeState;
import org.example.dto.CombatActionRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Combat Engine - Điều phối combat logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CombatEngine {

    private final DamageCalculator damageCalculator;
    private final EffectProcessor effectProcessor;
    private final TargetSelector targetSelector;

    /**
     * Thực hiện một turn.
     */
    public TurnResult executeTurn(MatchRuntimeState matchState, CombatActionRequest action) {
        HeroRuntimeState actor = matchState.getCurrentTurnHero();

        if (actor == null || !actor.isAlive()) {
            return TurnResult.builder()
                    .turnSkipped(true)
                    .message("Không có hero nào để hành động!")
                    .build();
        }

        log.info("=== Turn của {} ===", actor.getName());

        // === PHASE 1: TURN START - Process DOT effects ===
        List<EffectProcessor.EffectResult> startEffects = effectProcessor.processTurnStartEffects(actor);
        for (EffectProcessor.EffectResult effect : startEffects) {
            log.info("{} chịu {} từ {}", actor.getName(), effect.value(), effect.effectName());
        }

        // Kiểm tra nếu hero chết do DOT
        if (!actor.isAlive()) {
            return TurnResult.builder()
                    .actorHeroId(actor.getHeroId())
                    .actorName(actor.getName())
                    .turnSkipped(true)
                    .message(actor.getName() + " đã bị đánh bại bởi hiệu ứng!")
                    .build();
        }

        // Kiểm tra stun
        if (actor.isStunned()) {
            log.info("{} bị choáng, bỏ qua lượt!", actor.getName());
            return TurnResult.builder()
                    .actorHeroId(actor.getHeroId())
                    .actorName(actor.getName())
                    .turnSkipped(true)
                    .message(actor.getName() + " bị choáng và không thể hành động!")
                    .build();
        }

        // === PHASE 2: ACTION ===
        TurnResult result;

        if (action != null && action.getSkillId() != null) {
            // Sử dụng skill
            result = executeSkill(matchState, actor, action);
        } else {
            // Basic attack
            result = executeBasicAttack(matchState, actor, action);
        }

        // === PHASE 3: TURN END ===
        List<EffectProcessor.EffectResult> endEffects = effectProcessor.processTurnEndEffects(actor);
        actor.reduceCooldowns();

        // Check win condition
        matchState.checkGameEnd();

        return result;
    }

    /**
     * Thực hiện basic attack.
     */
    private TurnResult executeBasicAttack(MatchRuntimeState matchState,
                                           HeroRuntimeState actor,
                                           CombatActionRequest action) {
        // Select target
        List<Long> targetIds = action != null ? action.getTargetIds() : null;
        List<HeroRuntimeState> targets = targetSelector.selectTargets(
                matchState, actor, TargetType.SINGLE, targetIds);

        if (targets.isEmpty()) {
            return TurnResult.builder()
                    .actorHeroId(actor.getHeroId())
                    .actorName(actor.getName())
                    .actionType("ATTACK")
                    .message(actor.getName() + " không tìm thấy mục tiêu!")
                    .build();
        }

        HeroRuntimeState target = targets.get(0);

        // Calculate damage
        DamageResult damageResult = damageCalculator.calculateBasicAttack(actor, target);

        // Apply damage
        if (!damageResult.isDodged()) {
            target.takeDamage(damageResult.getFinalDamage());
        }

        // Increase MP on attack
        actor.onAttack();

        // Build result
        TurnResult.TargetResult targetResult = TurnResult.TargetResult.builder()
                .targetHeroId(target.getHeroId())
                .targetName(target.getName())
                .damageResult(damageResult)
                .targetDefeated(!target.isAlive())
                .build();

        String message = buildAttackMessage(actor, target, damageResult);
        log.info(message);

        return TurnResult.builder()
                .actorHeroId(actor.getHeroId())
                .actorName(actor.getName())
                .actionType("ATTACK")
                .targetResults(List.of(targetResult))
                .message(message)
                .build();
    }

    /**
     * Thực hiện skill.
     */
    private TurnResult executeSkill(MatchRuntimeState matchState,
                                     HeroRuntimeState actor,
                                     CombatActionRequest action) {
        // Find skill
        HeroSkill heroSkill = actor.getHero().getSkills().stream()
                .filter(s -> s.getSkillTemplate().getId().equals(action.getSkillId()))
                .findFirst()
                .orElse(null);

        if (heroSkill == null) {
            log.warn("Skill không tồn tại: {}", action.getSkillId());
            return executeBasicAttack(matchState, actor, action);
        }

        SkillTemplate skill = heroSkill.getSkillTemplate();

        // Check cooldown
        if (!actor.isSkillReady(skill.getId())) {
            log.warn("Skill {} đang cooldown", skill.getName());
            return executeBasicAttack(matchState, actor, action);
        }

        // Check MP cost
        if (skill.getMpCost() > 0 && actor.getCurrentMp() < skill.getMpCost()) {
            log.warn("Không đủ MP để sử dụng {}", skill.getName());
            return executeBasicAttack(matchState, actor, action);
        }

        log.info("⚡ {} sử dụng [{}]", actor.getName(), skill.getName());

        // Consume MP
        if (skill.getMpCost() > 0) {
            actor.setCurrentMp(actor.getCurrentMp() - skill.getMpCost());
        }

        // Set cooldown
        actor.setSkillCooldown(skill.getId(), heroSkill.getEffectiveCooldown());

        // Determine if healing skill
        boolean isHealingSkill = skill.getEffects().stream()
                .anyMatch(e -> e.getEffectType() == EffectType.HEAL);

        // Select targets
        List<HeroRuntimeState> targets;
        if (isHealingSkill && skill.getTargetType() == TargetType.SINGLE) {
            // Healing skill targets weakest ally
            targets = targetSelector.selectWeakestAlly(matchState, actor.getTeamIndex());
            if (targets.isEmpty()) {
                targets = List.of(actor); // Self heal if no ally
            }
        } else {
            targets = targetSelector.selectTargets(
                    matchState, actor, skill.getTargetType(), action.getTargetIds());
        }

        // Execute skill on each target
        List<TurnResult.TargetResult> targetResults = new ArrayList<>();
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("⚡ ").append(actor.getName()).append(" sử dụng [")
                .append(skill.getName()).append("]");

        for (HeroRuntimeState target : targets) {
            TurnResult.TargetResult targetResult = executeSkillOnTarget(
                    actor, target, heroSkill, skill);
            targetResults.add(targetResult);

            messageBuilder.append("\n  → ").append(target.getName());
            if (targetResult.getDamageResult() != null && targetResult.getDamageResult().getFinalDamage() > 0) {
                messageBuilder.append(": ").append(targetResult.getDamageResult().getFinalDamage()).append(" damage");
                if (targetResult.getDamageResult().isCritical()) messageBuilder.append(" (CRIT)");
                if (targetResult.getDamageResult().isBlocked()) messageBuilder.append(" (BLOCKED)");
            }
            if (targetResult.getHealingDone() > 0) {
                messageBuilder.append(": +").append(targetResult.getHealingDone()).append(" HP");
            }
            if (targetResult.isTargetDefeated()) {
                messageBuilder.append(" [DEFEATED]");
            }
        }

        String message = messageBuilder.toString();
        log.info(message);

        return TurnResult.builder()
                .actorHeroId(actor.getHeroId())
                .actorName(actor.getName())
                .actionType("SKILL")
                .skillName(skill.getName())
                .targetResults(targetResults)
                .message(message)
                .build();
    }

    /**
     * Thực hiện skill trên một target.
     */
    private TurnResult.TargetResult executeSkillOnTarget(HeroRuntimeState actor,
                                                          HeroRuntimeState target,
                                                          HeroSkill heroSkill,
                                                          SkillTemplate skill) {
        DamageResult damageResult = null;
        int healingDone = 0;
        List<String> effectsApplied = new ArrayList<>();

        // Process each effect
        for (EffectTemplate effectTemplate : skill.getEffects()) {
            switch (effectTemplate.getEffectType()) {
                case DAMAGE:
                    damageResult = damageCalculator.calculateSkillDamage(
                            actor, target,
                            heroSkill.getEffectiveScaling(),
                            skill.getScalingStat(),
                            skill.getDamageType()
                    );
                    if (!damageResult.isDodged()) {
                        switch (skill.getDamageType()) {
                            case PHYSICAL -> target.takeDamage(damageResult.getFinalDamage());
                            case MAGIC -> target.takeMagicDamage(damageResult.getFinalDamage());
                            case TRUE -> target.takeTrueDamage(damageResult.getFinalDamage());
                        }
                    }
                    break;

                case HEAL:
                    int healValue;
                    if (effectTemplate.isPercentage()) {
                        healValue = (int) (target.getHero().getMaxHp() * effectTemplate.getValue() / 100.0);
                    } else {
                        healValue = damageCalculator.calculateHealing(
                                actor, heroSkill.getEffectiveScaling(), skill.getScalingStat());
                    }
                    healingDone = target.heal(healValue);
                    break;

                case REVIVE:
                    if (!target.isAlive()) {
                        double hpPercent = effectTemplate.getValue() / 100.0;
                        healingDone = target.revive(Math.max(0.3, hpPercent));
                    }
                    break;

                default:
                    // Apply as ongoing effect (buff, debuff, poison, etc.)
                    String effectName = effectProcessor.applyEffect(target, effectTemplate, actor.getHeroId());
                    if (effectName != null) {
                        effectsApplied.add(effectName);
                    }
                    break;
            }
        }

        return TurnResult.TargetResult.builder()
                .targetHeroId(target.getHeroId())
                .targetName(target.getName())
                .damageResult(damageResult)
                .healingDone(healingDone)
                .effectsApplied(effectsApplied)
                .targetDefeated(!target.isAlive())
                .build();
    }

    /**
     * Build message cho basic attack.
     */
    private String buildAttackMessage(HeroRuntimeState actor, HeroRuntimeState target,
                                       DamageResult damageResult) {
        if (damageResult.isDodged()) {
            return target.getName() + " né được đòn tấn công của " + actor.getName() + "!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(actor.getName()).append(" tấn công ").append(target.getName());

        if (damageResult.isBlocked()) {
            sb.append(" (BLOCKED)");
        }
        if (damageResult.isCritical()) {
            sb.append(" (CRITICAL)");
        }

        sb.append(" gây ").append(damageResult.getFinalDamage()).append(" sát thương!");

        if (!target.isAlive()) {
            sb.append(" ").append(target.getName()).append(" đã bị đánh bại!");
        }

        return sb.toString();
    }

    /**
     * Xử lý logic khi bắt đầu round mới.
     */
    public void processRoundStart(MatchRuntimeState matchState) {
        log.info("=== Round {} ===", matchState.getCurrentRound());

        // Recalculate turn order
        matchState.calculateTurnOrder();
    }

    /**
     * Xử lý logic khi kết thúc round.
     */
    public void processRoundEnd(MatchRuntimeState matchState) {
        // Increase MP for all alive heroes
        for (HeroRuntimeState state : matchState.getAllStates()) {
            if (state.isAlive()) {
                state.onRoundEnd();
                log.debug("{} tăng {} MP", state.getName(), HeroRuntimeState.MP_PER_ROUND);
            }
        }
    }

    /**
     * Kiểm tra game đã kết thúc chưa.
     */
    public boolean checkGameEnd(MatchRuntimeState matchState) {
        return matchState.checkGameEnd();
    }
}

