// Hero Types - based on backend DTOs

export type HeroType = 'TANK' | 'ATTACK_PHYS' | 'ATTACK_MAGIC' | 'SUPPORT';

export interface HeroResponse {
  id: number;
  name: string;
  heroType: HeroType;
  stars: number;
  strength: number;
  agility: number;
  vitality: number;
  intelligence: number;
  maxHealth: number;
  maxMp: number;
  ultimateSkillName: string;
  ultimateSkillDescription: string;
}

export interface HeroSummary {
  id: number;
  name: string;
  heroType: HeroType;
  stars: number;
}

export interface CreateHeroRequest {
  name: string;
  heroType: HeroType;
  stars: number;
  strength: number;
  agility: number;
  vitality: number;
  intelligence: number;
}

// Hero type color mapping
export const heroTypeColors: Record<HeroType, { primary: string; gradient: string }> = {
  TANK: {
    primary: '#3b82f6',
    gradient: 'from-blue-500 to-blue-700',
  },
  ATTACK_PHYS: {
    primary: '#ef4444',
    gradient: 'from-red-500 to-red-700',
  },
  ATTACK_MAGIC: {
    primary: '#a855f7',
    gradient: 'from-purple-500 to-purple-700',
  },
  SUPPORT: {
    primary: '#22c55e',
    gradient: 'from-green-500 to-green-700',
  },
};

// Hero type labels in Vietnamese
export const heroTypeLabels: Record<HeroType, string> = {
  TANK: 'Đỡ Đòn',
  ATTACK_PHYS: 'Vật Công',
  ATTACK_MAGIC: 'Pháp Công',
  SUPPORT: 'Hỗ Trợ',
};

