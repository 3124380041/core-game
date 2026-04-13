import { motion } from 'framer-motion';
import {
  GiShield,
  GiBroadsword,
  GiMagicSwirl,
  GiHealing
} from 'react-icons/gi';
import { StatBar } from './StatBar';
import type { HeroStateInfo, HeroType } from '../../types';
import { heroTypeColors, heroTypeLabels } from '../../types';
import { generateStars, canUseUltimate } from '../../utils';

interface HeroCardProps {
  hero: HeroStateInfo;
  isCurrentTurn?: boolean;
  isSelected?: boolean;
  isTargetable?: boolean;
  onClick?: () => void;
  showStats?: boolean;
  size?: 'sm' | 'md' | 'lg';
}

const heroTypeIcons: Record<HeroType, React.ReactNode> = {
  TANK: <GiShield className="w-5 h-5" />,
  ATTACK_PHYS: <GiBroadsword className="w-5 h-5" />,
  ATTACK_MAGIC: <GiMagicSwirl className="w-5 h-5" />,
  SUPPORT: <GiHealing className="w-5 h-5" />,
};

const sizeStyles = {
  sm: { card: 'w-32 p-2', avatar: 'h-16', name: 'text-xs' },
  md: { card: 'w-44 p-3', avatar: 'h-24', name: 'text-sm' },
  lg: { card: 'w-56 p-4', avatar: 'h-32', name: 'text-base' },
};

export function HeroCard({
  hero,
  isCurrentTurn = false,
  isSelected = false,
  isTargetable = false,
  onClick,
  showStats = true,
  size = 'md',
}: HeroCardProps) {
  const heroType = hero.heroType as HeroType;
  const colors = heroTypeColors[heroType] || heroTypeColors.TANK;
  const styles = sizeStyles[size];
  const isUltReady = canUseUltimate(hero.currentMp);

  return (
    <motion.div
      whileHover={isTargetable ? { scale: 1.05, y: -5 } : {}}
      whileTap={isTargetable ? { scale: 0.98 } : {}}
      animate={{
        boxShadow: isCurrentTurn
          ? '0 0 20px rgba(99, 102, 241, 0.6)'
          : isSelected
            ? '0 0 15px rgba(251, 191, 36, 0.5)'
            : 'none',
      }}
      onClick={isTargetable ? onClick : undefined}
      className={`
        ${styles.card}
        rounded-xl border-2 
        ${hero.isDefeated ? 'opacity-50 grayscale' : ''}
        ${isCurrentTurn ? 'border-indigo-400' : isSelected ? 'border-yellow-400' : 'border-slate-600'}
        ${isTargetable ? 'cursor-pointer hover:border-red-400' : ''}
        bg-gradient-to-b from-slate-800 to-slate-900
        transition-all duration-200
        relative overflow-hidden
      `}
    >
      {/* Ultimate Ready Indicator */}
      {isUltReady && !hero.isDefeated && (
        <motion.div
          animate={{ opacity: [0.5, 1, 0.5] }}
          transition={{ duration: 1.5, repeat: Infinity }}
          className="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-yellow-400 via-orange-500 to-yellow-400"
        />
      )}

      {/* Stars & Type Badge */}
      <div className="flex items-center justify-between mb-2">
        <span className="text-yellow-400 text-xs tracking-wider">
          {generateStars(3, 5)}
        </span>
        <div
          className={`flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-medium text-white bg-gradient-to-r ${colors.gradient}`}
        >
          {heroTypeIcons[heroType]}
          <span className="hidden sm:inline">{heroTypeLabels[heroType]}</span>
        </div>
      </div>

      {/* Avatar */}
      <div
        className={`
          ${styles.avatar} w-full rounded-lg mb-2
          bg-gradient-to-br ${colors.gradient}
          flex items-center justify-center
          text-white text-3xl font-bold
          shadow-inner
        `}
      >
        {hero.name.charAt(0)}
      </div>

      {/* Name */}
      <h3 className={`${styles.name} font-bold text-white text-center mb-2 truncate`}>
        「{hero.name}」
      </h3>

      {/* HP/MP Bars */}
      <div className="space-y-1">
        <StatBar
          current={hero.currentHealth}
          max={hero.maxHealth}
          type="hp"
          size={size === 'lg' ? 'md' : 'sm'}
        />
        <StatBar
          current={hero.currentMp}
          max={hero.maxMp}
          type="mp"
          size={size === 'lg' ? 'md' : 'sm'}
        />
      </div>

      {/* Stats */}
      {showStats && size !== 'sm' && (
        <div className="grid grid-cols-2 gap-1 mt-2 text-xs">
          <div className="text-slate-400">
            STR: <span className="text-red-400 font-bold">{hero.strength}</span>
          </div>
          <div className="text-slate-400">
            AGI: <span className="text-green-400 font-bold">{hero.agility}</span>
          </div>
          <div className="text-slate-400">
            VIT: <span className="text-blue-400 font-bold">{hero.vitality}</span>
          </div>
          <div className="text-slate-400">
            INT: <span className="text-purple-400 font-bold">{hero.intelligence}</span>
          </div>
        </div>
      )}

      {/* Defeated Overlay */}
      {hero.isDefeated && (
        <div className="absolute inset-0 bg-black/60 flex items-center justify-center">
          <span className="text-red-500 font-bold text-lg transform -rotate-12">
            DEFEATED
          </span>
        </div>
      )}

      {/* Current Turn Indicator */}
      {isCurrentTurn && !hero.isDefeated && (
        <motion.div
          animate={{ y: [0, -3, 0] }}
          transition={{ duration: 1, repeat: Infinity }}
          className="absolute -top-2 left-1/2 -translate-x-1/2"
        >
          <div className="px-2 py-0.5 bg-indigo-500 rounded text-xs font-bold text-white">
            ĐANG ĐÁNH
          </div>
        </motion.div>
      )}
    </motion.div>
  );
}

