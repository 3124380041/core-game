import { motion } from 'framer-motion';
import type { IconType } from 'react-icons';
import { GiBroadsword, GiMagicSwirl, GiArrowDunk } from 'react-icons/gi';
import type { ActionType } from '../../types';

interface ActionCardProps {
  actionType: ActionType;
  isSelected?: boolean;
  isDisabled?: boolean;
  onClick?: () => void;
  mpCurrent?: number;
  skillName?: string;
}

interface ActionConfig {
  icon: IconType;
  label: string;
  description: string;
  color: string;
  bgGradient: string;
}

const actionConfigs: Record<ActionType, ActionConfig> = {
  BASIC_ATTACK: {
    icon: GiBroadsword,
    label: 'Tấn Công',
    description: 'Đòn đánh thường',
    color: 'text-red-400',
    bgGradient: 'from-red-600/20 to-red-800/20',
  },
  ULTIMATE_SKILL: {
    icon: GiMagicSwirl,
    label: 'Tuyệt Chiêu',
    description: 'Cần 100 MP',
    color: 'text-yellow-400',
    bgGradient: 'from-yellow-600/20 to-orange-800/20',
  },
  PASS: {
    icon: GiArrowDunk,
    label: 'Bỏ Lượt',
    description: 'Nhường lượt đi',
    color: 'text-slate-400',
    bgGradient: 'from-slate-600/20 to-slate-800/20',
  },
};

export function ActionCard({
  actionType,
  isSelected = false,
  isDisabled = false,
  onClick,
  mpCurrent = 0,
  skillName,
}: ActionCardProps) {
  const config = actionConfigs[actionType];
  const Icon = config.icon;

  // Ultimate requires 100 MP
  const canUseUltimate = actionType === 'ULTIMATE_SKILL' && mpCurrent >= 100;
  const isUltimateDisabled = actionType === 'ULTIMATE_SKILL' && !canUseUltimate;
  const finalDisabled = isDisabled || isUltimateDisabled;

  return (
    <motion.button
      whileHover={!finalDisabled ? { scale: 1.05, y: -5 } : {}}
      whileTap={!finalDisabled ? { scale: 0.95 } : {}}
      onClick={!finalDisabled ? onClick : undefined}
      disabled={finalDisabled}
      className={`
        relative w-32 p-4 rounded-xl border-2 
        bg-gradient-to-b ${config.bgGradient}
        ${isSelected 
          ? 'border-yellow-400 shadow-lg shadow-yellow-400/20' 
          : 'border-slate-600 hover:border-slate-500'
        }
        ${finalDisabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}
        transition-all duration-200
        flex flex-col items-center gap-2
      `}
    >
      {/* Icon */}
      <div className={`${config.color} text-3xl`}>
        <Icon />
      </div>

      {/* Label */}
      <span className="text-white font-bold text-sm">
        {config.label}
      </span>

      {/* Description / Skill Name */}
      <span className="text-slate-400 text-xs text-center">
        {actionType === 'ULTIMATE_SKILL' && skillName
          ? skillName
          : config.description
        }
      </span>

      {/* MP Indicator for Ultimate */}
      {actionType === 'ULTIMATE_SKILL' && (
        <div className={`
          mt-1 px-2 py-0.5 rounded text-xs font-bold
          ${canUseUltimate 
            ? 'bg-yellow-500/30 text-yellow-300' 
            : 'bg-slate-700 text-slate-400'
          }
        `}>
          MP: {mpCurrent}/100
        </div>
      )}

      {/* Selected Indicator */}
      {isSelected && (
        <motion.div
          layoutId="action-selected"
          className="absolute -bottom-1 left-1/2 -translate-x-1/2 w-8 h-1 bg-yellow-400 rounded-full"
        />
      )}

      {/* Ready glow for Ultimate */}
      {actionType === 'ULTIMATE_SKILL' && canUseUltimate && (
        <motion.div
          animate={{
            boxShadow: [
              '0 0 10px rgba(251, 191, 36, 0.3)',
              '0 0 20px rgba(251, 191, 36, 0.6)',
              '0 0 10px rgba(251, 191, 36, 0.3)',
            ]
          }}
          transition={{ duration: 1.5, repeat: Infinity }}
          className="absolute inset-0 rounded-xl pointer-events-none"
        />
      )}
    </motion.button>
  );
}

