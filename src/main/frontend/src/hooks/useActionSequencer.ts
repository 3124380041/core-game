import { useCallback, useEffect, useRef } from 'react';
import { useBattleAnimationStore } from '../store/useBattleAnimationStore';
import type { TurnResultResponse, AnimationState } from '../types';

interface UseActionSequencerOptions {
  autoPlay?: boolean;
  onActionStart?: (action: TurnResultResponse) => void;
  onActionComplete?: (action: TurnResultResponse) => void;
  onSequenceComplete?: () => void;
}

export function useActionSequencer(options: UseActionSequencerOptions = {}) {
  const { autoPlay = true, onActionStart, onActionComplete, onSequenceComplete } = options;

  const {
    actionQueue,
    currentActionIndex,
    isPlaying,
    isPaused,
    playbackSpeed,
    timings,
    playNext,
    play,
    pause,
    resume,
    reset,
    setHeroAnimationState,
    updateHeroHp,
    hasMoreActions,
    getProgress,
    gameEnded,
    winnerId,
  } = useBattleAnimationStore();

  const timeoutRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const isProcessingRef = useRef(false);

  // Calculate delay based on playback speed
  const getDelay = useCallback((baseDelay: number) => {
    return baseDelay / playbackSpeed;
  }, [playbackSpeed]);

  // Process a single action with animations
  const processAction = useCallback(async (action: TurnResultResponse) => {
    if (isProcessingRef.current) return;
    isProcessingRef.current = true;

    onActionStart?.(action);

    const actorId = action.actorHeroId;

    // 1. Show actor animation (attacking or using skill)
    const actorAnimation: AnimationState =
      action.actionType === 'ULTIMATE_SKILL' ? 'skill' :
      action.actionType === 'BASIC_ATTACK' ? 'attacking' : 'idle';

    if (!action.turnSkipped) {
      setHeroAnimationState(actorId, actorAnimation);
    }

    // Wait for attack/skill animation
    await new Promise(resolve => {
      timeoutRef.current = setTimeout(resolve, getDelay(
        action.actionType === 'ULTIMATE_SKILL' ? timings.skillDelay : timings.attackDelay
      ));
    });

    // 2. Process each target
    if (action.targets && action.targets.length > 0) {
      for (const target of action.targets) {
        // Determine target animation
        let targetAnimation: AnimationState = 'idle';

        if (target.wasDodged) {
          // Dodged - no damage animation
          targetAnimation = 'idle';
        } else if (target.healingReceived > 0) {
          targetAnimation = 'healing';
        } else if (target.damageTaken > 0) {
          targetAnimation = 'damaged';
        }

        // Apply target animation
        setHeroAnimationState(target.heroId, targetAnimation);

        // Wait for damage/heal animation
        await new Promise(resolve => {
          timeoutRef.current = setTimeout(resolve, getDelay(
            target.healingReceived > 0 ? timings.healDelay : timings.damageDelay
          ));
        });

        // Update HP
        if (target.damageTaken > 0 || target.healingReceived > 0) {
          const heroAnim = useBattleAnimationStore.getState().heroAnimations.get(target.heroId);
          if (heroAnim) {
            const newHp = Math.max(0, heroAnim.currentHp - target.damageTaken + target.healingReceived);
            updateHeroHp(target.heroId, newHp);
          }
        }

        // Handle defeat
        if (target.defeated) {
          setHeroAnimationState(target.heroId, 'defeated');
          await new Promise(resolve => {
            timeoutRef.current = setTimeout(resolve, getDelay(timings.deathDelay));
          });
        }

        // Reset target to idle (unless defeated)
        if (!target.defeated) {
          setHeroAnimationState(target.heroId, 'idle');
        }
      }
    }

    // 3. Reset actor to idle
    setHeroAnimationState(actorId, 'idle');

    // Wait between actions
    await new Promise(resolve => {
      timeoutRef.current = setTimeout(resolve, getDelay(timings.betweenActions));
    });

    isProcessingRef.current = false;
    onActionComplete?.(action);
  }, [
    getDelay,
    timings,
    setHeroAnimationState,
    updateHeroHp,
    onActionStart,
    onActionComplete
  ]);

  // Main playback loop
  useEffect(() => {
    if (!isPlaying || isPaused || !hasMoreActions()) {
      return;
    }

    const runNextAction = async () => {
      const action = playNext();
      if (action) {
        await processAction(action);

        // Check if more actions and still playing
        const state = useBattleAnimationStore.getState();
        if (state.isPlaying && !state.isPaused && state.hasMoreActions()) {
          // Continue to next action
        } else if (!state.hasMoreActions()) {
          onSequenceComplete?.();
        }
      }
    };

    runNextAction();
  }, [isPlaying, isPaused, currentActionIndex, playNext, processAction, hasMoreActions, onSequenceComplete]);

  // Cleanup timeouts on unmount
  useEffect(() => {
    return () => {
      if (timeoutRef.current) {
        clearTimeout(timeoutRef.current);
      }
    };
  }, []);

  // Auto-play when queue is initialized
  useEffect(() => {
    if (autoPlay && actionQueue.length > 0 && currentActionIndex === -1) {
      play();
    }
  }, [autoPlay, actionQueue.length, currentActionIndex, play]);

  return {
    // State
    isPlaying,
    isPaused,
    progress: getProgress(),
    gameEnded,
    winnerId,

    // Controls
    play,
    pause,
    resume,
    reset,

    // Actions
    playNext,
    processAction,
  };
}

// Helper hook for individual hero animation state
export function useHeroAnimation(heroId: number) {
  const heroAnimation = useBattleAnimationStore(
    (state) => state.heroAnimations.get(heroId)
  );

  return {
    animationState: heroAnimation?.state ?? 'idle',
    currentHp: heroAnimation?.currentHp ?? 0,
    maxHp: heroAnimation?.maxHp ?? 0,
    previousHp: heroAnimation?.previousHp ?? 0,
  };
}

