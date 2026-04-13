// Turn Result Types - based on backend DTOs

import type { ActionType } from './Match';

export interface TargetInfo {
  heroId: number;
  name: string;
  damageTaken: number;
  healingReceived: number;
  wasCritical: boolean;
  wasDodged: boolean;
  wasBlocked: boolean;
  defeated: boolean;
  effectsApplied: string[];
}

export interface TurnResultResponse {
  actorHeroId: number;
  actorName: string;
  actionType: ActionType;
  skillName: string | null;
  turnSkipped: boolean;
  message: string;
  targets: TargetInfo[];
  gameEnded: boolean;
  winnerId: number | null;
}

// Animation state for each action
export type AnimationState = 'idle' | 'attacking' | 'damaged' | 'healing' | 'skill' | 'defeated' | 'victory';

// Playback control
export interface PlaybackState {
  isPlaying: boolean;
  currentIndex: number;
  speed: number; // 0.5x, 1x, 2x
  isPaused: boolean;
}
