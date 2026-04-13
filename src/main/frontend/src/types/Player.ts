// Player Types

export interface PlayerResponse {
  id: number;
  username: string;
  name: string;
  level: number;
  experience: number;
  activeTeam?: {
    id: number;
    name: string;
  } | null;
}

export interface CreatePlayerRequest {
  username: string;
  name: string;
  password?: string;
}

