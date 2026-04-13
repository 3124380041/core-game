import { create } from 'zustand';
import type { MatchResponse, HeroStateInfo, ActionType, TurnResultResponse, SimulateBattleResponse } from '../types';

interface MatchState {
  // Match data
  match: MatchResponse | null;
  isLoading: boolean;
  error: string | null;

  // Battle replay mode
  isReplayMode: boolean;
  turnHistory: TurnResultResponse[];
  initialHeroStates: HeroStateInfo[];

  // Battle state
  selectedAction: ActionType | null;
  selectedTargetId: number | null;
  lastTurnResult: TurnResultResponse | null;
  combatLog: TurnResultResponse[];

  // Actions
  setMatch: (match: MatchResponse) => void;
  setLoading: (loading: boolean) => void;
  setError: (error: string | null) => void;
  setSelectedAction: (action: ActionType | null) => void;
  setSelectedTarget: (targetId: number | null) => void;
  addTurnResult: (result: TurnResultResponse) => void;
  updateHeroStates: (heroStates: HeroStateInfo[]) => void;
  resetSelection: () => void;
  clearMatch: () => void;

  // Replay mode actions
  setSimulateResult: (result: SimulateBattleResponse) => void;
  setInitialHeroStates: (states: HeroStateInfo[]) => void;
}

export const useMatchStore = create<MatchState>((set) => ({
  // Initial state
  match: null,
  isLoading: false,
  error: null,
  isReplayMode: false,
  turnHistory: [],
  initialHeroStates: [],
  selectedAction: null,
  selectedTargetId: null,
  lastTurnResult: null,
  combatLog: [],

  // Actions
  setMatch: (match) => {
    const team1 = (match.team1 || []).map((h) => ({
      odtHeroId: h.heroId,
      heroId: h.heroId,
      name: h.name,
      heroType: h.type,
      playerId: match.player1.id,
      currentHealth: h.currentHp,
      maxHealth: h.maxHp,
      currentMp: h.currentMp,
      maxMp: 100,
      positionRow: h.positionRow,
      positionCol: h.positionCol,
      isDefeated: !h.alive,
      strength: 0,
      agility: 0,
      vitality: 0,
      intelligence: 0,
      ultimateSkillName: '',
    }));

    const team2 = (match.team2 || []).map((h) => ({
      odtHeroId: h.heroId,
      heroId: h.heroId,
      name: h.name,
      heroType: h.type,
      playerId: match.player2.id,
      currentHealth: h.currentHp,
      maxHealth: h.maxHp,
      currentMp: h.currentMp,
      maxMp: 100,
      positionRow: h.positionRow,
      positionCol: h.positionCol,
      isDefeated: !h.alive,
      strength: 0,
      agility: 0,
      vitality: 0,
      intelligence: 0,
      ultimateSkillName: '',
    }));

    set({
      match,
      initialHeroStates: [...team1, ...team2],
      isReplayMode: false,
      turnHistory: [],
      combatLog: [],
      error: null,
    });
  },

  setLoading: (isLoading) => set({ isLoading }),

  setError: (error) => set({ error, isLoading: false }),

  setSelectedAction: (selectedAction) => set({ selectedAction }),

  setSelectedTarget: (selectedTargetId) => set({ selectedTargetId }),

  addTurnResult: (result) => set((state) => ({
    lastTurnResult: result,
    combatLog: [...state.combatLog, result],
  })),

  updateHeroStates: (heroStates) => set({
    initialHeroStates: heroStates,
  }),

  resetSelection: () => set({
    selectedAction: null,
    selectedTargetId: null,
  }),

  clearMatch: () => set({
    match: null,
    isLoading: false,
    error: null,
    isReplayMode: false,
    turnHistory: [],
    initialHeroStates: [],
    selectedAction: null,
    selectedTargetId: null,
    lastTurnResult: null,
    combatLog: [],
  }),

  // Replay mode: set simulate result with turn history
  setSimulateResult: (result) => set({
    match: result.matchResult,
    turnHistory: result.turnHistory,
    isReplayMode: true,
    combatLog: result.turnHistory,
    error: null,
  }),

  setInitialHeroStates: (states) => set({ initialHeroStates: states }),
}));

// Selectors - use initialHeroStates for rendering
export const selectCurrentTurnHero = (state: MatchState): HeroStateInfo | null => {
  if (!state.match || !state.match.currentTurnHero) return null;
  const heroId = state.match.currentTurnHero.heroId;
  return state.initialHeroStates.find(h => h.heroId === heroId) || null;
};

export const selectPlayer1Heroes = (state: MatchState): HeroStateInfo[] => {
  if (state.initialHeroStates.length > 0 && state.match) {
    return state.initialHeroStates.filter(h => h.playerId === state.match!.player1.id);
  }
  return [];
};

export const selectPlayer2Heroes = (state: MatchState): HeroStateInfo[] => {
  if (state.initialHeroStates.length > 0 && state.match) {
    return state.initialHeroStates.filter(h => h.playerId === state.match!.player2.id);
  }
  return [];
};

export const selectAliveHeroes = (state: MatchState): HeroStateInfo[] => {
  if (state.initialHeroStates.length > 0) {
    return state.initialHeroStates.filter(h => !h.isDefeated);
  }
  return [];
};
