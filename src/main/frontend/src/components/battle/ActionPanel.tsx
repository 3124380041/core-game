import { ActionCard } from '../cards';
import type { ActionType, HeroStateInfo } from '../../types';
import { Button } from '../common';

interface ActionPanelProps {
  currentHero: HeroStateInfo | null;
  selectedAction: ActionType | null;
  onActionSelect: (action: ActionType) => void;
  onConfirm: () => void;
  onCancel: () => void;
  isSubmitting: boolean;
  needsTarget: boolean;
  hasSelectedTarget: boolean;
}

export function ActionPanel({
  currentHero,
  selectedAction,
  onActionSelect,
  onConfirm,
  onCancel,
  isSubmitting,
  needsTarget,
  hasSelectedTarget,
}: ActionPanelProps) {
  if (!currentHero) {
    return (
      <div className="bg-slate-800/80 rounded-xl p-6 border border-slate-700 text-center">
        <p className="text-slate-400">Đang chờ lượt...</p>
      </div>
    );
  }

  const canConfirm = selectedAction && (!needsTarget || hasSelectedTarget);

  return (
    <div className="bg-slate-800/80 rounded-xl p-4 border border-slate-700">
      {/* Current Hero Info */}
      <div className="flex items-center justify-between mb-4 pb-3 border-b border-slate-700">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-indigo-600 flex items-center justify-center text-white font-bold">
            {currentHero.name.charAt(0)}
          </div>
          <div>
            <h3 className="text-white font-bold">{currentHero.name}</h3>
            <p className="text-slate-400 text-sm">Chọn hành động</p>
          </div>
        </div>

        {needsTarget && selectedAction && (
          <div className="text-yellow-400 text-sm animate-pulse">
            👆 Chọn mục tiêu
          </div>
        )}
      </div>

      {/* Action Cards */}
      <div className="flex justify-center gap-4 mb-4">
        <ActionCard
          actionType="BASIC_ATTACK"
          isSelected={selectedAction === 'BASIC_ATTACK'}
          onClick={() => onActionSelect('BASIC_ATTACK')}
          isDisabled={isSubmitting}
        />
        <ActionCard
          actionType="ULTIMATE_SKILL"
          isSelected={selectedAction === 'ULTIMATE_SKILL'}
          onClick={() => onActionSelect('ULTIMATE_SKILL')}
          mpCurrent={currentHero.currentMp}
          skillName={currentHero.ultimateSkillName}
          isDisabled={isSubmitting}
        />
        <ActionCard
          actionType="PASS"
          isSelected={selectedAction === 'PASS'}
          onClick={() => onActionSelect('PASS')}
          isDisabled={isSubmitting}
        />
      </div>

      {/* Confirm/Cancel Buttons */}
      <div className="flex justify-center gap-4">
        <Button
          variant="ghost"
          onClick={onCancel}
          disabled={isSubmitting || !selectedAction}
        >
          Hủy
        </Button>
        <Button
          variant="primary"
          onClick={onConfirm}
          disabled={!canConfirm}
          isLoading={isSubmitting}
        >
          {needsTarget && !hasSelectedTarget
            ? 'Chọn mục tiêu'
            : 'Xác nhận'
          }
        </Button>
      </div>
    </div>
  );
}

