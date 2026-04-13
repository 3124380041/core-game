import axiosClient from './axiosClient';
import type {
  DungeonResponse,
  DungeonMapResponse,
  DungeonRunResponse,
  DungeonBattleStartResult,
  MatchResponse,
} from '../types';

export const dungeonApi = {
  getAll: () => axiosClient.get<DungeonResponse[]>('/dungeons'),

  getMaps: (dungeonId: number) =>
    axiosClient.get<DungeonMapResponse[]>(`/dungeons/${dungeonId}/maps`),

  startRun: (dungeonId: number, playerId: number) =>
    axiosClient.post<DungeonRunResponse>(`/dungeons/${dungeonId}/runs?playerId=${playerId}`),

  startBattle: (playerId: number, dungeonId: number) =>
    axiosClient.post<DungeonBattleStartResult>(`/dungeons/start-battle?playerId=${playerId}&dungeonId=${dungeonId}`),

  startCurrentMapBattle: (runId: number, playerId: number) =>
    axiosClient.post<MatchResponse>(`/dungeons/runs/${runId}/start-battle?playerId=${playerId}`),

  resolveRun: (runId: number, playerId: number) =>
    axiosClient.post<DungeonRunResponse>(`/dungeons/runs/${runId}/resolve?playerId=${playerId}`),

  getRun: (runId: number, playerId: number) =>
    axiosClient.get<DungeonRunResponse>(`/dungeons/runs/${runId}?playerId=${playerId}`),
};

