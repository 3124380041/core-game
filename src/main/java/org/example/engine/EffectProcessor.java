package org.example.engine;

import org.example.domain.entity.EffectTemplate;
import org.example.domain.enums.EffectTrigger;
import org.example.domain.enums.EffectType;
import org.example.domain.runtime.ActiveEffect;
import org.example.domain.runtime.HeroRuntimeState;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Component xử lý effects.
 */
@Component
public class EffectProcessor {

    private final Random random = new Random();

    /**
     * Apply effect template lên target.
     * @return Tên effect nếu apply thành công, null nếu không
     */
    public String applyEffect(HeroRuntimeState target, EffectTemplate template, Long sourceHeroId) {
        // Check chance
        if (random.nextDouble() > template.getChance()) {
            return null;
        }

        // Skip immediate effects - they are handled separately
        if (template.getTrigger() == EffectTrigger.IMMEDIATE) {
            return null;
        }

        ActiveEffect effect = ActiveEffect.builder()
                .effectTemplateId(template.getId())
                .name(template.getName())
                .effectType(template.getEffectType())
                .trigger(template.getTrigger())
                .value(template.getValue())
                .isPercentage(template.isPercentage())
                .targetStat(template.getTargetStat())
                .remainingDuration(template.getDuration())
                .sourceHeroId(sourceHeroId)
                .build();

        target.addEffect(effect);
        return template.getName();
    }

    /**
     * Xử lý effects ở đầu turn.
     * @return Danh sách damage/heal từ DOT effects
     */
    public List<EffectResult> processTurnStartEffects(HeroRuntimeState hero) {
        List<EffectResult> results = new ArrayList<>();

        for (ActiveEffect effect : hero.getActiveEffects()) {
            if (effect.getTrigger() == EffectTrigger.TURN_START && !effect.isExpired()) {
                int value = effect.apply(hero);
                if (value != 0) {
                    results.add(new EffectResult(effect.getName(), effect.getEffectType(), value));
                }
            }
        }

        return results;
    }

    /**
     * Xử lý effects ở cuối turn.
     */
    public List<EffectResult> processTurnEndEffects(HeroRuntimeState hero) {
        List<EffectResult> results = new ArrayList<>();

        for (ActiveEffect effect : hero.getActiveEffects()) {
            if (effect.getTrigger() == EffectTrigger.TURN_END && !effect.isExpired()) {
                int value = effect.apply(hero);
                if (value != 0) {
                    results.add(new EffectResult(effect.getName(), effect.getEffectType(), value));
                }
            }
        }

        // Tick effects và xóa expired
        hero.tickEffects();
        hero.removeExpiredEffects();

        return results;
    }

    /**
     * Tính tổng buff/debuff cho một stat.
     */
    public int calculateStatModifier(HeroRuntimeState hero, String statName) {
        int modifier = 0;

        for (ActiveEffect effect : hero.getActiveEffects()) {
            if ((effect.getEffectType() == EffectType.BUFF || effect.getEffectType() == EffectType.DEBUFF)
                    && effect.getTargetStat() != null
                    && effect.getTargetStat().name().equals(statName)) {
                modifier += effect.getStatModifier();
            }
        }

        return modifier;
    }

    /**
     * Kết quả của effect processing.
     */
    public record EffectResult(String effectName, EffectType effectType, int value) {}
}

