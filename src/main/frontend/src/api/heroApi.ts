import axiosClient from './axiosClient';
import type { HeroResponse, HeroSummary, CreateHeroRequest } from '../types';

export const heroApi = {
  // Get all heroes
  getAll: () =>
    axiosClient.get<HeroResponse[]>('/heroes'),

  // Get hero by ID
  getById: (id: number) =>
    axiosClient.get<HeroResponse>(`/heroes/${id}`),

  // Create new hero
  create: (data: CreateHeroRequest) =>
    axiosClient.post<HeroResponse>('/heroes', data),

  // Get heroes by player ID
  getByPlayerId: (playerId: number) =>
    axiosClient.get<HeroSummary[]>(`/players/${playerId}/heroes`),
};

