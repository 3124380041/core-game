import { create } from 'zustand';

interface UIState {
  // Animation states
  isAnimating: boolean;
  showDamagePopup: boolean;
  damagePopupData: { heroId: number; damage: number; isCritical: boolean } | null;

  // Modal states
  showVictoryModal: boolean;
  showDefeatModal: boolean;

  // Loading states
  isSubmittingAction: boolean;

  // Actions
  setAnimating: (isAnimating: boolean) => void;
  showDamage: (heroId: number, damage: number, isCritical: boolean) => void;
  hideDamagePopup: () => void;
  setVictoryModal: (show: boolean) => void;
  setDefeatModal: (show: boolean) => void;
  setSubmittingAction: (submitting: boolean) => void;
  resetUI: () => void;
}

export const useUIStore = create<UIState>((set) => ({
  // Initial state
  isAnimating: false,
  showDamagePopup: false,
  damagePopupData: null,
  showVictoryModal: false,
  showDefeatModal: false,
  isSubmittingAction: false,

  // Actions
  setAnimating: (isAnimating) => set({ isAnimating }),

  showDamage: (heroId, damage, isCritical) => set({
    showDamagePopup: true,
    damagePopupData: { heroId, damage, isCritical },
  }),

  hideDamagePopup: () => set({
    showDamagePopup: false,
    damagePopupData: null,
  }),

  setVictoryModal: (showVictoryModal) => set({ showVictoryModal }),

  setDefeatModal: (showDefeatModal) => set({ showDefeatModal }),

  setSubmittingAction: (isSubmittingAction) => set({ isSubmittingAction }),

  resetUI: () => set({
    isAnimating: false,
    showDamagePopup: false,
    damagePopupData: null,
    showVictoryModal: false,
    showDefeatModal: false,
    isSubmittingAction: false,
  }),
}));

