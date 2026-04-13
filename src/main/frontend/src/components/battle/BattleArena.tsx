import { useMemo, useCallback } from 'react';
import { motion } from 'framer-motion';
import { useMatchStore, selectPlayer1Heroes, selectPlayer2Heroes } from '../../store';
import { TurnOrderBar } from './TurnOrderBar';
import { TeamSection } from './TeamSection';
import { ActionPanel } from './ActionPanel';
import { CombatLog } from './CombatLog';
import type { ActionType, HeroStateInfo } from '../../types';
import { matchApi } from '../../api';

export function BattleArena() {
  const {
    match,
    initialHeroStates,
    selectedAction,
    selectedTargetId,
    combatLog,
    setSelectedAction,
    setSelectedTarget,
    addTurnResult,
    resetSelection,
  } = useMatchStore();

  const player1Heroes = useMatchStore(selectPlayer1Heroes);
  const player2Heroes = useMatchStore(selectPlayer2Heroes);

  // Get all heroes from initialHeroStates or construct from match
  const allHeroes: HeroStateInfo[] = useMemo(() => {
    if (initialHeroStates.length > 0) return initialHeroStates;
    return [];
  }, [initialHeroStates]);

  // Get current turn hero
  const currentTurnHero = useMemo(() => {
    if (!match?.currentTurnHero) return null;
    const heroId = match.currentTurnHero.heroId;
    return allHeroes.find(h => h.heroId === heroId) || null;
  }, [match, allHeroes]);

  // Get current turn hero ID
  const currentTurnHeroId = match?.currentTurnHero?.heroId ?? null;

  // Determine targetable heroes based on selected action
  const targetableHeroIds = useMemo(() => {
    if (!selectedAction || selectedAction === 'PASS' || !currentTurnHero) {
      return [];
    }

    // For attack: target enemies (opposite team)
    const isPlayer1Hero = player1Heroes.some(h => h.odtHeroId === currentTurnHero.odtHeroId);
    const enemies = isPlayer1Hero ? player2Heroes : player1Heroes;

    return enemies
      .filter(h => !h.isDefeated)
      .map(h => h.odtHeroId);
  }, [selectedAction, currentTurnHero, player1Heroes, player2Heroes]);

  // Check if action needs target selection
  const needsTarget = selectedAction === 'BASIC_ATTACK' || selectedAction === 'ULTIMATE_SKILL';

  // Handle action selection
  const handleActionSelect = useCallback((action: ActionType) => {
    setSelectedAction(action);
    setSelectedTarget(null);
  }, [setSelectedAction, setSelectedTarget]);

  // Handle target selection
  const handleTargetSelect = useCallback((heroId: number) => {
    if (targetableHeroIds.includes(heroId)) {
      setSelectedTarget(heroId);
    }
  }, [targetableHeroIds, setSelectedTarget]);

  // Handle action confirmation
  const handleConfirm = useCallback(async () => {
    if (!match || !currentTurnHero || !selectedAction) return;

    // PASS doesn't need target
    const targetId = selectedAction === 'PASS' ? undefined : selectedTargetId ?? undefined;

    // Find which player owns the current turn hero
    const playerId = currentTurnHero.playerId;

    try {
      const response = await matchApi.submitAction(match.id, playerId, {
        actorHeroId: currentTurnHero.odtHeroId,
        actionType: selectedAction,
        targetHeroId: targetId,
      });

      addTurnResult(response.data);
      resetSelection();
    } catch (error) {
      console.error('Failed to submit action:', error);
    }
  }, [match, currentTurnHero, selectedAction, selectedTargetId, addTurnResult, resetSelection]);

  // Handle cancel
  const handleCancel = useCallback(() => {
    resetSelection();
  }, [resetSelection]);

  if (!match) {
    return (
      <div className="flex items-center justify-center h-96">
        <p className="text-slate-400">Không có trận đấu</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen p-4 space-y-4">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="flex items-center justify-between bg-slate-800/80 rounded-xl p-4 border border-slate-700"
      >
        <div>
          <h1 className="text-2xl font-bold text-white">⚔️ Trận Đấu</h1>
          <p className="text-slate-400">Round {match.currentRound}</p>
        </div>
        <div className={`
          px-4 py-2 rounded-lg font-bold
          ${match.status === 'IN_PROGRESS' 
            ? 'bg-green-600/30 text-green-400' 
            : match.status === 'FINISHED'
              ? 'bg-yellow-600/30 text-yellow-400'
              : 'bg-slate-600/30 text-slate-400'
          }
        `}>
          {match.status === 'IN_PROGRESS' && 'Đang diễn ra'}
          {match.status === 'FINISHED' && 'Kết thúc'}
          {match.status === 'WAITING' && 'Chờ bắt đầu'}
        </div>
      </motion.div>

      {/* Turn Order */}
      <TurnOrderBar
        heroes={allHeroes}
        currentTurnHeroId={currentTurnHeroId}
      />

      {/* Battle Field */}
      <div className="flex gap-8">
        {/* Team 1 */}
        <TeamSection
          heroes={player1Heroes}
          teamName={match.player1.name}
          isPlayerTeam={true}
          currentTurnHeroId={currentTurnHeroId}
          selectedTargetId={selectedTargetId}
          targetableHeroIds={targetableHeroIds}
          onHeroClick={handleTargetSelect}
        />

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
        <TeamSection
          heroes={player2Heroes}
          teamName={match.player2.name}
          isPlayerTeam={false}
          currentTurnHeroId={currentTurnHeroId}
          selectedTargetId={selectedTargetId}
          targetableHeroIds={targetableHeroIds}
          onHeroClick={handleTargetSelect}
        />
      </div>

      {/* Bottom Panel */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {/* Action Panel */}
        <ActionPanel
          currentHero={currentTurnHero}
          selectedAction={selectedAction}
          onActionSelect={handleActionSelect}
          onConfirm={handleConfirm}
          onCancel={handleCancel}
          isSubmitting={false}
          needsTarget={needsTarget}
          hasSelectedTarget={selectedTargetId !== null}
        />

        {/* Combat Log */}
        <CombatLog logs={combatLog} />
      </div>

      {/* Victory/Defeat Modal would go here */}
      {match.status === 'FINISHED' && (
        <motion.div
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          className="fixed inset-0 bg-black/70 flex items-center justify-center z-50"
        >
          <div className="bg-slate-800 rounded-2xl p-8 text-center border-2 border-yellow-500">
            <h2 className="text-4xl font-bold text-yellow-400 mb-4">
              🏆 Chiến Thắng!
            </h2>
            <p className="text-white text-xl mb-6">
              {match.winnerId === match.player1.id
                ? match.player1.name
                : match.player2.name
              } đã thắng!
            </p>
            <button
              onClick={() => window.location.reload()}
              className="px-6 py-3 bg-yellow-500 text-black font-bold rounded-lg hover:bg-yellow-400 transition-colors"
            >
              Chơi lại
            </button>
          </div>
        </motion.div>
      )}
    </div>
  );
}

