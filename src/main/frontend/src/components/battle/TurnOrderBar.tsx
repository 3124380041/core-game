import { motion } from 'framer-motion';
import type { HeroStateInfo } from '../../types';
import { sortBySpeed } from '../../utils';

interface TurnOrderBarProps {
  heroes: HeroStateInfo[];
  currentTurnHeroId: number | null;
}

export function TurnOrderBar({ heroes, currentTurnHeroId }: TurnOrderBarProps) {
  // Sort by agility (speed) and filter out defeated
  const sortedHeroes = sortBySpeed(heroes.filter(h => !h.isDefeated));

  return (
    <div className="bg-slate-800/80 rounded-xl p-4 border border-slate-700">
      <div className="flex items-center gap-2 mb-3">
        <span className="text-slate-400 text-sm font-medium">Thứ tự lượt:</span>
      </div>

      <div className="flex items-center gap-2 overflow-x-auto pb-2">
        {sortedHeroes.map((hero, index) => {
          const isCurrent = hero.odtHeroId === currentTurnHeroId;

          return (
            <div key={hero.odtHeroId} className="flex items-center">
              <motion.div
                animate={isCurrent ? { scale: [1, 1.1, 1] } : {}}
                transition={{ duration: 1, repeat: Infinity }}
                className={`
                  flex items-center gap-2 px-3 py-2 rounded-lg
                  ${isCurrent 
                    ? 'bg-indigo-600 border-2 border-indigo-400' 
                    : 'bg-slate-700/50 border border-slate-600'
                  }
                  transition-all duration-200
                `}
              >
                {/* Avatar placeholder */}
                <div
                  className={`
                    w-8 h-8 rounded-full flex items-center justify-center
                    text-white font-bold text-sm
                    ${isCurrent ? 'bg-indigo-400' : 'bg-slate-600'}
                  `}
                >
                  {hero.name.charAt(0)}
                </div>

                {/* Name */}
                <span className={`
                  text-sm font-medium whitespace-nowrap
                  ${isCurrent ? 'text-white' : 'text-slate-300'}
                `}>
                  {hero.name}
                </span>

                {/* Speed indicator */}
                <span className="text-xs text-green-400">
                  ({hero.agility})
                </span>
              </motion.div>

              {/* Arrow between heroes */}
              {index < sortedHeroes.length - 1 && (
                <span className="text-slate-500 mx-1">→</span>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}

