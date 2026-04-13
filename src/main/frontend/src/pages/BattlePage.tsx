import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { BattleArena, BattleReplayArena } from '../components/battle';
import { Loading } from '../components/common';
import { matchApi } from '../api';
import { useMatchStore } from '../store';

export function BattlePage() {
  const { matchId } = useParams<{ matchId: string }>();
  const navigate = useNavigate();
  const { match, isReplayMode, turnHistory, setMatch, setLoading, setError } = useMatchStore();
  const [loading, setLocalLoading] = useState(true);

  useEffect(() => {
    const loadMatch = async () => {
      if (!matchId) {
        navigate('/lobby');
        return;
      }

      // If we already have replay data from LobbyPage, no need to fetch
      if (isReplayMode && turnHistory.length > 0 && match) {
        setLocalLoading(false);
        return;
      }

      try {
        setLoading(true);
        const response = await matchApi.getById(parseInt(matchId));
        setMatch(response.data);
      } catch (err) {
        setError('Không thể tải trận đấu');
        console.error(err);
        navigate('/lobby');
      } finally {
        setLoading(false);
        setLocalLoading(false);
      }
    };

    loadMatch();
  }, [matchId, navigate, setMatch, setLoading, setError, isReplayMode, turnHistory, match]);

  if (loading || !match) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Loading size="lg" text="Đang tải trận đấu..." />
      </div>
    );
  }

  // Use replay arena if we have turn history
  if (isReplayMode && turnHistory.length > 0) {
    return <BattleReplayArena />;
  }

  return <BattleArena />;
}

