import type { PlayerResponse } from './Player';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  player: PlayerResponse;
}

