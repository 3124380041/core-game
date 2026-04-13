import { useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import type { TurnResultResponse } from '../../types';
import { GiBroadsword, GiMagicSwirl, GiHealing, GiSkullCrossedBones } from 'react-icons/gi';

interface CombatLogProps {
  logs: TurnResultResponse[];
  maxDisplay?: number;
}

export function CombatLog({ logs, maxDisplay = 10 }: CombatLogProps) {
  const containerRef = useRef<HTMLDivElement>(null);

  // Auto-scroll to bottom when new logs added
  useEffect(() => {
    if (containerRef.current) {
      containerRef.current.scrollTop = containerRef.current.scrollHeight;
    }
  }, [logs]);

  const displayLogs = logs.slice(-maxDisplay);

  const getIcon = (log: TurnResultResponse) => {
    // Check if any target was defeated
    const hasDefeated = log.targets?.some(t => t.defeated);
    const hasHealing = log.targets?.some(t => t.healingReceived > 0);

    if (hasDefeated) return <GiSkullCrossedBones className="text-red-500" />;
    if (hasHealing) return <GiHealing className="text-green-400" />;
    if (log.actionType === 'ULTIMATE_SKILL') return <GiMagicSwirl className="text-yellow-400" />;
    return <GiBroadsword className="text-red-400" />;
  };

  const formatLog = (log: TurnResultResponse) => {
    if (log.actionType === 'PASS' || log.turnSkipped) {
      return <span className="text-slate-400">bỏ lượt</span>;
    }

    // Process targets
    const targets = log.targets || [];
    if (targets.length === 0) {
      return <span className="text-slate-400">{log.message || 'thực hiện hành động'}</span>;
    }

    const target = targets[0]; // Primary target

    if (target.wasDodged) {
      return <span className="text-blue-400">{target.name} né được đòn tấn công!</span>;
    }

    if (target.healingReceived > 0) {
      return (
        <>
          hồi phục cho {target.name}{' '}
          <span className="text-green-400 font-bold">+{target.healingReceived} HP</span>
        </>
      );
    }

    if (target.damageTaken > 0) {
      return (
        <>
          gây{' '}
          <span className={`font-bold ${target.wasCritical ? 'text-yellow-400' : 'text-red-400'}`}>
            {target.damageTaken} sát thương
            {target.wasCritical && ' (CHÍ MẠNG!)'}
          </span>
          {' '}cho {target.name}
          {target.defeated && (
            <span className="text-red-500 ml-1">- HẠ GỤC!</span>
          )}
        </>
      );
    }

    return <span className="text-slate-400">{log.message || 'thực hiện hành động'}</span>;
  };

  return (
    <div className="bg-slate-800/80 rounded-xl border border-slate-700 overflow-hidden">
      <div className="px-4 py-2 bg-slate-700/50 border-b border-slate-600">
        <h3 className="text-white font-bold text-sm">📜 Nhật ký chiến đấu</h3>
      </div>

      <div
        ref={containerRef}
        className="h-40 overflow-y-auto p-3 space-y-2 scrollbar-thin"
      >
        <AnimatePresence mode="popLayout">
          {displayLogs.length === 0 ? (
            <p className="text-slate-500 text-sm text-center py-4">
              Trận đấu bắt đầu...
            </p>
          ) : (
            displayLogs.map((log, index) => (
              <motion.div
                key={`${log.actorHeroId}-${index}`}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: 20 }}
                className="flex items-start gap-2 text-sm"
              >
                <span className="mt-0.5">{getIcon(log)}</span>
                <p className="text-slate-300">
                  <span className="text-white font-medium">
                    {log.actorName}
                  </span>
                  {log.skillName && (
                    <span className="text-purple-400"> [{log.skillName}]</span>
                  )}
                  {' - '}
                  {formatLog(log)}
                </p>
              </motion.div>
            ))
          )}
        </AnimatePresence>
      </div>
    </div>
  );
}
