import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { Button, Loading } from '../components/common';
import { HeroCard } from '../components/cards';
import { heroApi, matchApi, playerApi, dungeonApi } from '../api';
import type { HeroResponse, HeroStateInfo, HeroStateInfoBackend } from '../types';
import { useAuthStore, useMatchStore } from '../store';

// Helper to convert backend hero state to frontend format
function convertHeroState(hero: HeroStateInfoBackend, playerId: number): HeroStateInfo {
  return {
    odtHeroId: hero.heroId,
    heroId: hero.heroId,
    name: hero.name,
    heroType: hero.type,
    playerId,
    currentHealth: hero.currentHp,
    maxHealth: hero.maxHp,
    currentMp: hero.currentMp,
    maxMp: 100,
    positionRow: hero.positionRow,
    positionCol: hero.positionCol,
    isDefeated: !hero.alive,
    strength: 0,
    agility: 0,
    vitality: 0,
    intelligence: 0,
    ultimateSkillName: '',
  };
}

export function LobbyPage() {
  const navigate = useNavigate();
  const { setMatch, setSimulateResult, setInitialHeroStates } = useMatchStore();
  const currentPlayer = useAuthStore((state) => state.currentPlayer);

  const [heroes, setHeroes] = useState<HeroResponse[]>([]);
  const [loading, setLocalLoading] = useState(true);
  const [creating, setCreating] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Load available heroes
  useEffect(() => {
    const loadHeroes = async () => {
      try {
        const response = await heroApi.getAll();
        setHeroes(response.data);
      } catch (err) {
        setError('Không thể tải danh sách hero');
        console.error(err);
      } finally {
        setLocalLoading(false);
      }
    };
    loadHeroes();
  }, []);

  // Create a quick match
  const handleQuickMatch = async () => {
    setCreating(true);
    setError(null);

    try {
      // Get players
      const playersResponse = await playerApi.getAll();
      const players = playersResponse.data;

      if (players.length < 2) {
        setError('Cần ít nhất 2 người chơi');
        return;
      }

      // Get heroes for each player
      const player1 = players[0];
      const player2 = players[1];

      const player1Heroes = await heroApi.getByPlayerId(player1.id);
      const player2Heroes = await heroApi.getByPlayerId(player2.id);

      if (player1Heroes.data.length < 3 || player2Heroes.data.length < 3) {
        setError('Mỗi người chơi cần ít nhất 3 hero');
        return;
      }

      // Step 1: Create match
      const matchResponse = await matchApi.create({
        player1Id: player1.id,
        player2Id: player2.id,
        hero1Ids: player1Heroes.data.slice(0, 3).map(h => h.id),
        hero2Ids: player2Heroes.data.slice(0, 3).map(h => h.id),
      });

      const matchId = matchResponse.data.id;
      const matchData = matchResponse.data;

      // Convert backend hero states to frontend format
      const team1States = (matchData.team1 || []).map(h =>
        convertHeroState(h, matchData.player1.id)
      );
      const team2States = (matchData.team2 || []).map(h =>
        convertHeroState(h, matchData.player2.id)
      );
      const initialStates = [...team1States, ...team2States];
      setInitialHeroStates(initialStates);

      // Step 2: Simulate battle with history
      const simulateResponse = await matchApi.simulateWithHistory(matchId);

      // Step 3: Save to store and navigate
      setSimulateResult(simulateResponse.data);
      navigate(`/battle/${matchId}`);
    } catch (err) {
      setError('Không thể tạo trận đấu');
      console.error(err);
    } finally {
      setCreating(false);
    }
  };

  // Start a dungeon battle
  const handleDungeonBattle = async () => {
    setCreating(true);
    setError(null);

    try {
      if (!currentPlayer) {
        setError('Bạn chưa đăng nhập');
        return;
      }

      const currentPlayerResponse = await playerApi.getById(currentPlayer.id);
      const player = currentPlayerResponse.data;
      if (!player.activeTeam) {
        setError('Người chơi chưa có active team để vào phó bản');
        return;
      }

      const dungeonsResponse = await dungeonApi.getAll();
      const dungeons = dungeonsResponse.data;
      if (dungeons.length < 1) {
        setError('Chưa có phó bản trong hệ thống');
        return;
      }

      const dungeon = dungeons[0];

      const battleResponse = await dungeonApi.startBattle(player.id, dungeon.id);
      setMatch(battleResponse.data.match);
      navigate(`/battle/${battleResponse.data.match.id}`);
    } catch (err) {
      setError('Không thể bắt đầu trận phó bản');
      console.error(err);
    } finally {
      setCreating(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loading size="lg" text="Đang tải..." />
      </div>
    );
  }

  // Convert HeroResponse to HeroStateInfo for display
  const heroToState = (hero: HeroResponse): HeroStateInfo => ({
    odtHeroId: hero.id,
    heroId: hero.id,
    name: hero.name,
    heroType: hero.heroType,
    playerId: 0,
    currentHealth: hero.maxHealth,
    maxHealth: hero.maxHealth,
    currentMp: 50,
    maxMp: hero.maxMp,
    positionRow: 0,
    positionCol: 0,
    isDefeated: false,
    strength: hero.strength,
    agility: hero.agility,
    vitality: hero.vitality,
    intelligence: hero.intelligence,
    ultimateSkillName: hero.ultimateSkillName,
  });

  return (
    <div className="min-h-screen p-8">
      {/* Header */}
      <motion.div
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-center mb-8"
      >
        <h1 className="text-4xl font-bold text-white mb-2">🎯 Sảnh Chờ</h1>
        <p className="text-slate-400">Chọn đội hình và bắt đầu trận đấu</p>
      </motion.div>

      {/* Error Display */}
      {error && (
        <div className="max-w-md mx-auto mb-6 p-4 bg-red-500/20 border border-red-500 rounded-lg text-red-400 text-center">
          {error}
        </div>
      )}

      {/* Quick Match Button */}
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        className="flex justify-center gap-4 mb-8"
      >
        <Button
          variant="primary"
          size="lg"
          onClick={handleQuickMatch}
          isLoading={creating}
          disabled={creating}
        >
          ⚡ Trận Đấu Nhanh
        </Button>
        <Button
          variant="secondary"
          size="lg"
          onClick={handleDungeonBattle}
          isLoading={creating}
          disabled={creating}
        >
          🗺️ Vào Phó Bản
        </Button>
        <Button
          variant="ghost"
          size="lg"
          onClick={() => navigate('/')}
        >
          ← Quay lại
        </Button>
      </motion.div>

      {/* Available Heroes */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.2 }}
      >
        <h2 className="text-xl font-bold text-white mb-4 text-center">
          📜 Danh sách Hero ({heroes.length})
        </h2>

        <div className="flex flex-wrap justify-center gap-4">
          {heroes.map((hero, index) => (
            <motion.div
              key={hero.id}
              initial={{ opacity: 0, scale: 0.8 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: index * 0.05 }}
            >
              <HeroCard
                hero={heroToState(hero)}
                size="md"
                showStats={true}
              />
            </motion.div>
          ))}
        </div>

        {heroes.length === 0 && (
          <div className="text-center py-12">
            <p className="text-slate-400 text-lg">Không có hero nào</p>
            <p className="text-slate-500 text-sm mt-2">
              Hãy đảm bảo backend đang chạy và có dữ liệu
            </p>
          </div>
        )}
      </motion.div>
    </div>
  );
}
