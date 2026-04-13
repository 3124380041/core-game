import type { MatchResponse } from './Match';

export interface DungeonResponse {
  id: number;
  code: string;
  name: string;
  description: string;
  recommendedPower: number;
  mapCount: number;
}

export interface DungeonMapResponse {
  id: number;
  mapIndex: number;
  name: string;
  enemyTeamName: string;
  enemyCount: number;
  firstClearGoldReward: number;
}

export interface DungeonRunResponse {
  runId: number;
  playerId: number;
  dungeonId: number;
  dungeonName: string;
  currentMapIndex: number;
  totalMaps: number;
  status: 'IN_PROGRESS' | 'CLEARED' | 'FAILED';
  activeMatchId: number | null;
}

export interface DungeonBattleStartResult {
  run: DungeonRunResponse;
  match: MatchResponse;
}

