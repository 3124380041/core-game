import { motion, AnimatePresence } from 'framer-motion';
import { useState, useEffect } from 'react';

interface DamagePopupProps {
  damage?: number;
  healing?: number;
  isCritical?: boolean;
  isDodged?: boolean;
  isBlocked?: boolean;
  position?: { x: number; y: number };
  onComplete?: () => void;
}

export function DamagePopup({
  damage,
  healing,
  isCritical = false,
  isDodged = false,
  isBlocked = false,
  position = { x: 0, y: 0 },
  onComplete,
}: DamagePopupProps) {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsVisible(false);
      onComplete?.();
    }, 1200);
    return () => clearTimeout(timer);
  }, [onComplete]);

  // Determine display content
  let content = '';
  let colorClass = '';
  let sizeClass = 'text-2xl';

  if (isDodged) {
    content = 'MISS';
    colorClass = 'text-gray-400';
  } else if (isBlocked) {
    content = 'BLOCK';
    colorClass = 'text-blue-400';
  } else if (healing && healing > 0) {
    content = `+${healing}`;
    colorClass = 'text-green-400';
  } else if (damage && damage > 0) {
    content = `-${damage}`;
    colorClass = isCritical ? 'text-yellow-400' : 'text-red-400';
    sizeClass = isCritical ? 'text-4xl' : 'text-2xl';
  }

  if (!content) return null;

  return (
    <AnimatePresence>
      {isVisible && (
        <motion.div
          initial={{ opacity: 0, y: 0, scale: 0.5 }}
          animate={{ opacity: 1, y: -50, scale: isCritical ? 1.5 : 1 }}
          exit={{ opacity: 0, y: -80 }}
          transition={{
            duration: 0.8,
            ease: [0.25, 0.46, 0.45, 0.94],
          }}
          style={{
            left: position.x,
            top: position.y,
          }}
          className={`
            absolute pointer-events-none z-50
            font-bold ${sizeClass} ${colorClass}
            drop-shadow-lg
          `}
        >
          {isCritical && damage && (
            <motion.span
              animate={{ scale: [1, 1.2, 1] }}
              transition={{ duration: 0.3, repeat: 2 }}
            >
              💥
            </motion.span>
          )}
          {content}
          {isCritical && damage && (
            <span className="text-sm ml-1">CRIT!</span>
          )}
        </motion.div>
      )}
    </AnimatePresence>
  );
}

// Container for multiple popups
interface DamagePopupContainerProps {
  popups: Array<{
    id: string;
    damage?: number;
    healing?: number;
    isCritical?: boolean;
    isDodged?: boolean;
    isBlocked?: boolean;
  }>;
  onPopupComplete?: (id: string) => void;
}

export function DamagePopupContainer({ popups, onPopupComplete }: DamagePopupContainerProps) {
  return (
    <div className="absolute inset-0 overflow-visible pointer-events-none">
      <AnimatePresence>
        {popups.map((popup, index) => (
          <DamagePopup
            key={popup.id}
            damage={popup.damage}
            healing={popup.healing}
            isCritical={popup.isCritical}
            isDodged={popup.isDodged}
            isBlocked={popup.isBlocked}
            position={{ x: 50 + (index % 3) * 20, y: 30 }}
            onComplete={() => onPopupComplete?.(popup.id)}
          />
        ))}
      </AnimatePresence>
    </div>
  );
}

