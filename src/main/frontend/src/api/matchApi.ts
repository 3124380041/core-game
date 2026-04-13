import axiosClient from './axiosClient';
import type {
  MatchResponse,
  CreateMatchRequest,
  CombatActionRequest,
  TurnResultResponse,
  SimulateBattleResponse
} from '../types';

export const matchApi = {
  // Create and start new match
  // POST /api/matches/start
  create: (data: CreateMatchRequest) =>
    axiosClient.post<MatchResponse>('/matches/start', data),

  // Get match by ID
  // GET /api/matches/{id}
  getById: (id: number) =>
    axiosClient.get<MatchResponse>(`/matches/${id}`),

  // Submit combat action
  // POST /api/matches/{id}/action?playerId={playerId}
  submitAction: (matchId: number, playerId: number, action: CombatActionRequest) =>
    axiosClient.post<TurnResultResponse>(`/matches/${matchId}/action?playerId=${playerId}`, action),

  // Auto-play turn (AI)
  // POST /api/matches/{id}/auto
  autoPlay: (matchId: number) =>
    axiosClient.post<TurnResultResponse>(`/matches/${matchId}/auto`),

  // Simulate full battle
  // POST /api/matches/{id}/simulate
  simulate: (matchId: number) =>
    axiosClient.post<MatchResponse>(`/matches/${matchId}/simulate`),

  // Simulate full battle with turn history (for animation replay)
  // POST /api/matches/{id}/simulate-with-history
  simulateWithHistory: (matchId: number) =>
    axiosClient.post<SimulateBattleResponse>(`/matches/${matchId}/simulate-with-history`),

  // Get combat logs
  // GET /api/matches/{id}/logs
  getLogs: (matchId: number) =>
    axiosClient.get(`/matches/${matchId}/logs`),

  // Get matches by player
  // GET /api/matches?playerId={playerId}
  getByPlayer: (playerId: number, activeOnly: boolean = false) =>
    axiosClient.get<MatchResponse[]>(`/matches?playerId=${playerId}&activeOnly=${activeOnly}`),
};

