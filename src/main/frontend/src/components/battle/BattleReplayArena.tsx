import { useEffect, useState, useCallback } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useBattleAnimationStore, useMatchStore } from '../../store';
import { useActionSequencer, useHeroAnimation } from '../../hooks';
import { HeroAnimationWrapper } from './HeroAnimationWrapper';
import { DamagePopupContainer } from './DamagePopup';
import { SkillEffect, ActionEffect } from './SkillEffect';
import { BattleResultModal } from './BattleResultModal';
import { PlaybackControls } from './PlaybackControls';
import { CombatLog } from './CombatLog';
import { HeroCard } from '../cards';
import type { TurnResultResponse, HeroStateInfo, TargetInfo } from '../../types';

interface BattleReplayArenaPropsExternal {
  turnResults: TurnResultResponse[];
  initialHeroStates: HeroStateInfo[];
  player1Name: string;
  player2Name: string;
  player1Id: number;
  onExit?: () => void;
  onPlayAgain?: () => void;
}

// Hero with animation state wrapper
function AnimatedHeroCard({
  hero,
  isTargetable = false,
  onClick,
}: {
  hero: HeroStateInfo;
  isTargetable?: boolean;
  onClick?: () => void;
}) {
  const { animationState, currentHp } = useHeroAnimation(hero.odtHeroId);

  // Create hero with updated HP from animation state
  const animatedHero: HeroStateInfo = {
    ...hero,
    currentHealth: currentHp ?? hero.currentHealth,
    isDefeated: animationState === 'defeated' || (currentHp !== undefined && currentHp <= 0),
  };

  return (
    <HeroAnimationWrapper
      animationState={animationState}
      isCurrentActor={false}
    >
      <HeroCard
        hero={animatedHero}
        isTargetable={isTargetable}
        onClick={onClick}
        showStats={true}
        size="md"
      />
    </HeroAnimationWrapper>
  );
}

// Standalone version that uses store data
export function BattleReplayArena() {
  const navigate = useNavigate();
  const { match, turnHistory, initialHeroStates, combatLog, clearMatch } = useMatchStore();

  if (!match) {
    return (
      <div className="flex items-center justify-center h-96">
        <p className="text-slate-400">Không có dữ liệu trận đấu</p>
      </div>
    );
  }

  // Use team1/team2 from match if initialHeroStates is empty
  const heroStates: HeroStateInfo[] = initialHeroStates.length > 0
    ? initialHeroStates
    : [...(match.team1 || []), ...(match.team2 || [])].map(h => ({
        odtHeroId: h.heroId,
        heroId: h.heroId,
        name: h.name,
        heroType: h.type,
        playerId: 0, // will be set below
        currentHealth: h.currentHp,
        maxHealth: h.maxHp,
        currentMp: h.currentMp,
        maxMp: 100,
        positionRow: h.positionRow,
        positionCol: h.positionCol,
        isDefeated: !h.alive,
        strength: 0,
        agility: 0,
        vitality: 0,
        intelligence: 0,
        ultimateSkillName: '',
      }));

  // Assign playerIds based on team
  const team1Ids = (match.team1 || []).map(h => h.heroId);
  heroStates.forEach(h => {
    h.playerId = team1Ids.includes(h.heroId) ? match.player1.id : match.player2.id;
  });

  const handleExit = () => {
    clearMatch();
    navigate('/lobby');
  };

  const handlePlayAgain = () => {
    clearMatch();
    navigate('/lobby');
  };

  return (
    <BattleReplayArenaInner
      turnResults={turnHistory}
      initialHeroStates={heroStates}
      player1Name={match.player1.name}
      player2Name={match.player2.name}
      player1Id={match.player1.id}
      combatLog={combatLog}
      onExit={handleExit}
      onPlayAgain={handlePlayAgain}
    />
  );
}

// Inner component with props
function BattleReplayArenaInner({
  turnResults,
  initialHeroStates,
  player1Name,
  player2Name,
  player1Id,
  combatLog,
  onExit,
  onPlayAgain,
}: BattleReplayArenaPropsExternal & { combatLog: TurnResultResponse[] }) {
  const [activePopups, setActivePopups] = useState<Array<{
    id: string;
    heroId: number;
    damage?: number;
    healing?: number;
    isCritical?: boolean;
    isDodged?: boolean;
    isBlocked?: boolean;
  }>>([]);

  const [activeSkill, setActiveSkill] = useState<{ name: string; isActive: boolean }>({
    name: '',
    isActive: false
  });

  const [currentActionDisplay, setCurrentActionDisplay] = useState<{
    actionType: 'BASIC_ATTACK' | 'ULTIMATE_SKILL' | 'SKIP';
    actorName: string;
    isActive: boolean;
  } | null>(null);

  const {
    initializeQueue,
    initializeHeroes,
    setPlaybackSpeed,
    playbackSpeed,
    skipToEnd,
    reset,
  } = useBattleAnimationStore();

  // Handle action start - show effects
  const handleActionStart = useCallback((action: TurnResultResponse) => {
    // Show action indicator
    setCurrentActionDisplay({
      actionType: action.actionType as 'BASIC_ATTACK' | 'ULTIMATE_SKILL' | 'SKIP',
      actorName: action.actorName,
      isActive: true,
    });

    // Show skill effect for ultimate
    if (action.actionType === 'ULTIMATE_SKILL' && action.skillName) {
      setActiveSkill({ name: action.skillName, isActive: true });
    }

    // Show damage popups after a short delay
    setTimeout(() => {
      if (action.targets) {
        const newPopups = action.targets.map((target: TargetInfo, index: number) => ({
          id: `${action.actorHeroId}-${target.heroId}-${Date.now()}-${index}`,
          heroId: target.heroId,
          damage: target.damageTaken,
          healing: target.healingReceived,
          isCritical: target.wasCritical,
          isDodged: target.wasDodged,
          isBlocked: target.wasBlocked,
        }));
        setActivePopups(prev => [...prev, ...newPopups]);
      }
    }, 400);
  }, []);

  // Handle action complete - cleanup effects
  const handleActionComplete = useCallback(() => {
    setCurrentActionDisplay(null);
    setActiveSkill({ name: '', isActive: false });
  }, []);

  // Remove popup when animation completes
  const handlePopupComplete = useCallback((id: string) => {
    setActivePopups(prev => prev.filter(p => p.id !== id));
  }, []);

  // Initialize on mount
  useEffect(() => {
    if (initialHeroStates.length === 0 || turnResults.length === 0) return;

    // Initialize hero states
    initializeHeroes(
      initialHeroStates.map(hero => ({
        heroId: hero.odtHeroId,
        currentHp: hero.currentHealth,
        maxHp: hero.maxHealth,
      }))
    );

    // Initialize action queue
    initializeQueue(turnResults);
  }, [turnResults, initialHeroStates, initializeQueue, initializeHeroes]);

  // Use action sequencer
  const {
    isPlaying,
    isPaused,
    progress,
    gameEnded,
    winnerId,
    play,
    pause,
    resume,
  } = useActionSequencer({
    autoPlay: true,
    onActionStart: handleActionStart,
    onActionComplete: handleActionComplete,
  });

  // Separate heroes by player
  const player1Heroes = initialHeroStates.filter(h => h.playerId === player1Id);
  const player2Heroes = initialHeroStates.filter(h => h.playerId !== player1Id);

  // Determine if current player won
  const isVictory = winnerId === player1Id;

  return (
    <div className="min-h-screen p-4 space-y-4 relative">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between bg-slate-800/80 rounded-xl p-4 border border-slate-700"
      >
        <div>
          <h1 className="text-2xl font-bold text-white">⚔️ Trận Đấu Nhanh</h1>
          <p className="text-slate-400">
            Turn {progress.current} / {progress.total}
          </p>
        </div>
        <div className={`
          px-4 py-2 rounded-lg font-bold
          ${gameEnded 
            ? 'bg-yellow-600/30 text-yellow-400' 
            : isPlaying
              ? 'bg-green-600/30 text-green-400'
              : 'bg-slate-600/30 text-slate-400'
          }
        `}>
          {gameEnded && 'Kết thúc'}
          {!gameEnded && isPlaying && 'Đang chiến đấu...'}
          {!gameEnded && !isPlaying && isPaused && 'Tạm dừng'}
          {!gameEnded && !isPlaying && !isPaused && progress.current === 0 && 'Sẵn sàng'}
        </div>
      </motion.div>

      {/* Battle Field */}
      <div className="flex gap-8 items-center justify-center">
        {/* Team 1 */}
        <div className="flex flex-col items-center gap-4">
          <h2 className="text-xl font-bold text-blue-400">{player1Name}</h2>
          <div className="flex flex-wrap gap-4 justify-center">
            {player1Heroes.map(hero => (
              <div key={hero.odtHeroId} className="relative">
                <AnimatedHeroCard hero={hero} />
                {/* Damage popups for this hero */}
                <DamagePopupContainer
                  popups={activePopups.filter(p => p.heroId === hero.odtHeroId)}
                  onPopupComplete={handlePopupComplete}
                />
              </div>
            ))}
          </div>
        </div>

        {/* VS Divider */}
        <div className="flex items-center">
          <motion.div
            animate={{ scale: [1, 1.1, 1] }}
            transition={{ duration: 2, repeat: Infinity }}
            className="text-4xl font-bold text-red-500"
          >
            VS
          </motion.div>
        </div>

        {/* Team 2 */}
        <div className="flex flex-col items-center gap-4">
          <h2 className="text-xl font-bold text-red-400">{player2Name}</h2>
          <div className="flex flex-wrap gap-4 justify-center">
            {player2Heroes.map(hero => (
              <div key={hero.odtHeroId} className="relative">
                <AnimatedHeroCard hero={hero} />
                {/* Damage popups for this hero */}
                <DamagePopupContainer
                  popups={activePopups.filter(p => p.heroId === hero.odtHeroId)}
                  onPopupComplete={handlePopupComplete}
                />
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Bottom Panel */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* Playback Controls */}
        <div className="bg-slate-800/50 rounded-xl p-4 border border-slate-700">
          <h3 className="text-white font-bold mb-3">🎮 Điều khiển</h3>
          <PlaybackControls
            isPlaying={isPlaying}
            isPaused={isPaused}
            progress={progress}
            playbackSpeed={playbackSpeed}
            onPlay={play}
            onPause={pause}
            onResume={resume}
            onSkipToEnd={skipToEnd}
            onReset={reset}
            onSpeedChange={setPlaybackSpeed}
          />
        </div>

        {/* Combat Log */}
        <CombatLog logs={combatLog.slice(0, progress.current)} />
      </div>

      {/* Action Effect Overlay */}
      <AnimatePresence>
        {currentActionDisplay?.isActive && (
          <ActionEffect
            actionType={currentActionDisplay.actionType}
            isActive={currentActionDisplay.isActive}
            actorName={currentActionDisplay.actorName}
          />
        )}
      </AnimatePresence>

      {/* Skill Effect Overlay */}
      <SkillEffect
        skillName={activeSkill.name}
        isActive={activeSkill.isActive}
        onComplete={() => setActiveSkill({ name: '', isActive: false })}
      />

      {/* Battle Result Modal */}
      <BattleResultModal
        isOpen={gameEnded && !isPlaying}
        isVictory={isVictory}
        winnerName={isVictory ? player1Name : player2Name}
        onPlayAgain={onPlayAgain ?? (() => window.location.reload())}
        onExit={onExit ?? (() => window.history.back())}
      />
    </div>
  );
}

