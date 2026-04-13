import { create } from 'zustand';
import type { PlayerResponse } from '../types';

const AUTH_STORAGE_KEY = 'currentPlayer';

interface AuthState {
  currentPlayer: PlayerResponse | null;
  setCurrentPlayer: (player: PlayerResponse) => void;
  logout: () => void;
}

function loadPlayerFromStorage(): PlayerResponse | null {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY);
    return raw ? (JSON.parse(raw) as PlayerResponse) : null;
  } catch {
    return null;
  }
}

export const useAuthStore = create<AuthState>((set) => ({
  currentPlayer: loadPlayerFromStorage(),
  setCurrentPlayer: (player) => {
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(player));
    set({ currentPlayer: player });
  },
  logout: () => {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    set({ currentPlayer: null });
  },
}));

