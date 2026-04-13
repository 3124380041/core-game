import axiosClient from './axiosClient';
import type { PlayerResponse, CreatePlayerRequest } from '../types';

export const playerApi = {
  // Get all players
  getAll: () =>
    axiosClient.get<PlayerResponse[]>('/players'),

  // Get player by ID
  getById: (id: number) =>
    axiosClient.get<PlayerResponse>(`/players/${id}`),

  // Create new player
  create: (data: CreatePlayerRequest) =>
    axiosClient.post<PlayerResponse>('/players', data),
};

