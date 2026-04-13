import { create } from 'zustand';
import type { TurnResultResponse, AnimationState } from '../types';

export interface HeroAnimationState {
  heroId: number;
  state: AnimationState;
  currentHp: number;
  maxHp: number;
  previousHp: number;
}

interface BattleAnimationState {
  // Action queue
  actionQueue: TurnResultResponse[];
  currentActionIndex: number;
  
  // Playback control
  isPlaying: boolean;
  isPaused: boolean;
  playbackSpeed: number; // 0.5, 1, 1.5, 2
  
  // Hero animation states
  heroAnimations: Map<number, HeroAnimationState>;
  
  // Current action being displayed
  currentAction: TurnResultResponse | null;
  
  // Game end state
  gameEnded: boolean;
  winnerId: number | null;
  
  // Timing constants (ms)
  timings: {
    attackDelay: number;
    damageDelay: number;
    healDelay: number;
    skillDelay: number;
    deathDelay: number;
    betweenActions: number;
  };

  // Actions
  initializeQueue: (actions: TurnResultResponse[]) => void;
  initializeHeroes: (heroes: { heroId: number; currentHp: number; maxHp: number }[]) => void;
  setHeroAnimationState: (heroId: number, state: AnimationState) => void;
  updateHeroHp: (heroId: number, newHp: number) => void;
  
  playNext: () => TurnResultResponse | null;
  play: () => void;
  pause: () => void;
  resume: () => void;
  skipToEnd: () => void;
  reset: () => void;
  
  setPlaybackSpeed: (speed: number) => void;
  setGameEnded: (ended: boolean, winnerId: number | null) => void;
  
  // Computed
  hasMoreActions: () => boolean;
  getProgress: () => { current: number; total: number };
}

const DEFAULT_TIMINGS = {
  attackDelay: 600,
  damageDelay: 400,
  healDelay: 500,
  skillDelay: 800,
  deathDelay: 1000,
  betweenActions: 300,
};

export const useBattleAnimationStore = create<BattleAnimationState>((set, get) => ({
  // Initial state
  actionQueue: [],
  currentActionIndex: -1,
  isPlaying: false,
  isPaused: false,
  playbackSpeed: 1,
  heroAnimations: new Map(),
  currentAction: null,
  gameEnded: false,
  winnerId: null,
  timings: DEFAULT_TIMINGS,

  // Initialize action queue from API response
  initializeQueue: (actions) => set({
    actionQueue: actions,
    currentActionIndex: -1,
    currentAction: null,
    isPlaying: false,
    isPaused: false,
    gameEnded: false,
    winnerId: null,
  }),

  // Initialize hero states at battle start
  initializeHeroes: (heroes) => {
    const heroAnimations = new Map<number, HeroAnimationState>();
    heroes.forEach(hero => {
      heroAnimations.set(hero.heroId, {
        heroId: hero.heroId,
        state: 'idle',
        currentHp: hero.currentHp,
        maxHp: hero.maxHp,
        previousHp: hero.currentHp,
      });
    });
    set({ heroAnimations });
  },

  // Set animation state for a specific hero
  setHeroAnimationState: (heroId, state) => set((prev) => {
    const newMap = new Map(prev.heroAnimations);
    const hero = newMap.get(heroId);
    if (hero) {
      newMap.set(heroId, { ...hero, state });
    }
    return { heroAnimations: newMap };
  }),

  // Update hero HP with previous value tracking
  updateHeroHp: (heroId, newHp) => set((prev) => {
    const newMap = new Map(prev.heroAnimations);
    const hero = newMap.get(heroId);
    if (hero) {
      newMap.set(heroId, {
        ...hero,
        previousHp: hero.currentHp,
        currentHp: Math.max(0, newHp),
      });
    }
    return { heroAnimations: newMap };
  }),

  // Get next action and advance index
  playNext: () => {
    const state = get();
    const nextIndex = state.currentActionIndex + 1;
    
    if (nextIndex >= state.actionQueue.length) {
      set({ isPlaying: false });
      return null;
    }
    
    const action = state.actionQueue[nextIndex];
    set({
      currentActionIndex: nextIndex,
      currentAction: action,
    });
    
    // Check for game end
    if (action.gameEnded) {
      set({
        gameEnded: true,
        winnerId: action.winnerId,
      });
    }
    
    return action;
  },

  play: () => set({ isPlaying: true, isPaused: false }),
  
  pause: () => set({ isPaused: true }),
  
  resume: () => set({ isPaused: false }),
  
  skipToEnd: () => {
    const state = get();
    const lastIndex = state.actionQueue.length - 1;
    if (lastIndex < 0) return;
    
    const lastAction = state.actionQueue[lastIndex];
    set({
      currentActionIndex: lastIndex,
      currentAction: lastAction,
      isPlaying: false,
      isPaused: false,
      gameEnded: lastAction.gameEnded,
      winnerId: lastAction.winnerId,
    });
    
    // Apply all HP changes
    state.actionQueue.forEach(action => {
      action.targets?.forEach(target => {
        const hero = state.heroAnimations.get(target.heroId);
        if (hero) {
          const newHp = target.defeated 
            ? 0 
            : Math.max(0, hero.currentHp - target.damageTaken + target.healingReceived);
          get().updateHeroHp(target.heroId, newHp);
          if (target.defeated) {
            get().setHeroAnimationState(target.heroId, 'defeated');
          }
        }
      });
    });
  },

  reset: () => set({
    currentActionIndex: -1,
    currentAction: null,
    isPlaying: false,
    isPaused: false,
    gameEnded: false,
    winnerId: null,
    // Reset all hero animation states to idle
    heroAnimations: new Map(
      Array.from(get().heroAnimations.entries()).map(([id, hero]) => [
        id,
        { ...hero, state: 'idle' as AnimationState, currentHp: hero.maxHp, previousHp: hero.maxHp }
      ])
    ),
  }),

  setPlaybackSpeed: (speed) => set({ playbackSpeed: speed }),
  
  setGameEnded: (ended, winnerId) => set({ gameEnded: ended, winnerId }),

  // Check if more actions available
  hasMoreActions: () => {
    const state = get();
    return state.currentActionIndex < state.actionQueue.length - 1;
  },

  // Get current progress
  getProgress: () => {
    const state = get();
    return {
      current: state.currentActionIndex + 1,
      total: state.actionQueue.length,
    };
  },
}));

// Selectors
export const selectHeroAnimation = (heroId: number) => (state: BattleAnimationState) => 
  state.heroAnimations.get(heroId);

export const selectIsHeroAttacking = (heroId: number) => (state: BattleAnimationState) =>
  state.currentAction?.actorHeroId === heroId && state.isPlaying;

export const selectIsHeroTargeted = (heroId: number) => (state: BattleAnimationState) =>
  state.currentAction?.targets?.some(t => t.heroId === heroId) && state.isPlaying;

