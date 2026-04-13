import type { HeroType } from '../types';
import { heroTypeColors } from '../types';

// Calculate HP percentage
export const getHealthPercentage = (current: number, max: number): number => {
  return Math.max(0, Math.min(100, (current / max) * 100));
};

// Get HP bar color based on percentage
export const getHealthBarColor = (percentage: number): string => {
  if (percentage > 60) return 'bg-green-500';
  if (percentage > 30) return 'bg-yellow-500';
  return 'bg-red-500';
};

// Get MP bar color
export const getMpBarColor = (percentage: number): string => {
  if (percentage >= 100) return 'bg-yellow-400'; // Ready for ultimate
  return 'bg-blue-500';
};

// Get hero type gradient class
export const getHeroTypeGradient = (heroType: HeroType): string => {
  return heroTypeColors[heroType]?.gradient || 'from-gray-500 to-gray-700';
};

// Format number with commas
export const formatNumber = (num: number): string => {
  return num.toLocaleString();
};

// Generate star display
export const generateStars = (count: number, maxStars: number = 5): string => {
  return '★'.repeat(count) + '☆'.repeat(maxStars - count);
};

// Calculate damage with defense
export const calculateDamageAfterDefense = (damage: number, vitality: number): number => {
  return Math.max(1, damage - Math.floor(vitality / 2));
};

// Sort heroes by agility (turn order)
export const sortBySpeed = <T extends { agility: number }>(heroes: T[]): T[] => {
  return [...heroes].sort((a, b) => b.agility - a.agility);
};

// Check if hero can use ultimate
export const canUseUltimate = (currentMp: number): boolean => {
  return currentMp >= 100;
};

// Truncate text
export const truncateText = (text: string, maxLength: number): string => {
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength - 3) + '...';
};

// Delay helper for animations
export const delay = (ms: number): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms));
};

