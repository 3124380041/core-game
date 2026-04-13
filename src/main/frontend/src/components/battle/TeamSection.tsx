import { motion } from 'framer-motion';
import type { HeroStateInfo } from '../../types';
import { HeroCard } from '../cards';

interface TeamSectionProps {
  heroes: HeroStateInfo[];
  teamName: string;
  isPlayerTeam?: boolean;
  currentTurnHeroId: number | null;
  selectedTargetId: number | null;
  targetableHeroIds?: number[];
  onHeroClick?: (heroId: number) => void;
}

export function TeamSection({
  heroes,
  teamName,
  isPlayerTeam = false,
  currentTurnHeroId,
  selectedTargetId,
  targetableHeroIds = [],
  onHeroClick,
}: TeamSectionProps) {
  // Sort heroes by position
  const sortedHeroes = [...heroes].sort((a, b) => {
    if (a.positionRow !== b.positionRow) return a.positionRow - b.positionRow;
    return a.positionCol - b.positionCol;
  });

  return (
    <div className={`flex-1 ${isPlayerTeam ? '' : ''}`}>
      {/* Team Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className={`
          text-center mb-4 pb-2 border-b-2 
          ${isPlayerTeam ? 'border-blue-500' : 'border-red-500'}
        `}
      >
        <h2 className={`
          text-lg font-bold 
          ${isPlayerTeam ? 'text-blue-400' : 'text-red-400'}
        `}>
          {teamName}
        </h2>
        <p className="text-slate-400 text-sm">
          {heroes.filter(h => !h.isDefeated).length} / {heroes.length} còn sống
        </p>
      </motion.div>

      {/* Heroes Grid */}
      <div className="flex flex-wrap justify-center gap-4">
        {sortedHeroes.map((hero, index) => {
          const isCurrentTurn = hero.odtHeroId === currentTurnHeroId;
          const isSelected = hero.odtHeroId === selectedTargetId;
          const isTargetable = targetableHeroIds.includes(hero.odtHeroId);

          return (
            <motion.div
              key={hero.odtHeroId}
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: index * 0.1 }}
            >
              <HeroCard
                hero={hero}
                isCurrentTurn={isCurrentTurn}
                isSelected={isSelected}
                isTargetable={isTargetable}
                onClick={() => onHeroClick?.(hero.odtHeroId)}
                size="md"
              />
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}

