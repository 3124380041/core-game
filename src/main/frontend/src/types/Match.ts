// Match Types - based on backend DTOs

export type MatchStatus = 'WAITING' | 'IN_PROGRESS' | 'FINISHED';
export type ActionType = 'BASIC_ATTACK' | 'ULTIMATE_SKILL' | 'PASS';

export interface PlayerInfo {
  id: number;
  name: string;
}

// Backend HeroStateInfo (from MatchResponse)
export interface HeroStateInfoBackend {
  heroId: number;
  name: string;
  type: string;
  currentHp: number;
  maxHp: number;
  currentMp: number;
  alive: boolean;
  positionRow: number;
  positionCol: number;
  activeEffects: string[];
}

// Frontend extended HeroStateInfo (with additional fields)
export interface HeroStateInfo {
  odtHeroId: number;
  heroId: number;
  name: string;
  heroType: string;
  playerId: number;
  currentHealth: number;
  maxHealth: number;
  currentMp: number;
  maxMp: number;
  positionRow: number;
  positionCol: number;
  isDefeated: boolean;
  strength: number;
  agility: number;
  vitality: number;
  intelligence: number;
  ultimateSkillName: string;
}

// Backend MatchResponse
export interface MatchResponse {
  id: number;
  status: MatchStatus;
  currentRound: number;
  currentTurnIndex: number;
  player1: PlayerInfo;
  player2: PlayerInfo;
  team1: HeroStateInfoBackend[];
  team2: HeroStateInfoBackend[];
  currentTurnHero: HeroStateInfoBackend | null;
  winnerId: number | null;
  winnerName: string | null;
  createdAt: string;
  endedAt: string | null;
}

export interface CreateMatchRequest {
  player1Id: number;
  player2Id: number;
  hero1Ids: number[];
  hero2Ids: number[];
}

export interface CombatActionRequest {
  actorHeroId: number;
  actionType: ActionType;
  targetHeroId?: number;
}

// Response from simulate-with-history endpoint
import type { TurnResultResponse } from './TurnResult';

export interface SimulateBattleResponse {
  matchResult: MatchResponse;
  turnHistory: TurnResultResponse[];
}

