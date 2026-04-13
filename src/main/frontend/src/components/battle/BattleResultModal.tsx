import { motion, AnimatePresence } from 'framer-motion';

interface BattleResultModalProps {
  isOpen: boolean;
  isVictory: boolean;
  winnerName: string;
  onPlayAgain: () => void;
  onExit: () => void;
}

export function BattleResultModal({
  isOpen,
  isVictory,
  winnerName,
  onPlayAgain,
  onExit,
}: BattleResultModalProps) {
  return (
    <AnimatePresence>
      {isOpen && (
        <motion.div
          initial={{ opacity: 0 }}
          animate={{ opacity: 1 }}
          exit={{ opacity: 0 }}
          className="fixed inset-0 z-50 flex items-center justify-center"
        >
          {/* Backdrop */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 0.8 }}
            exit={{ opacity: 0 }}
            className="absolute inset-0 bg-black"
          />

          {/* Modal Content */}
          <motion.div
            initial={{ scale: 0.5, y: 100, rotateX: 45 }}
            animate={{ scale: 1, y: 0, rotateX: 0 }}
            exit={{ scale: 0.5, y: -100, opacity: 0 }}
            transition={{
              type: 'spring',
              stiffness: 200,
              damping: 20,
            }}
            className="relative z-10"
          >
            {/* Background glow */}
            <motion.div
              animate={{
                scale: [1, 1.2, 1],
                opacity: [0.3, 0.6, 0.3],
              }}
              transition={{ duration: 2, repeat: Infinity }}
              className={`
                absolute -inset-20 rounded-full blur-3xl
                ${isVictory ? 'bg-yellow-500' : 'bg-red-500'}
              `}
            />

            {/* Main card */}
            <div className={`
              relative px-12 py-10 rounded-2xl
              ${isVictory 
                ? 'bg-gradient-to-b from-yellow-900/90 to-orange-900/90 border-2 border-yellow-500' 
                : 'bg-gradient-to-b from-red-900/90 to-slate-900/90 border-2 border-red-500'
              }
              shadow-2xl min-w-[400px]
            `}>
              {/* Trophy/Skull icon */}
              <motion.div
                initial={{ scale: 0, rotate: -180 }}
                animate={{ scale: 1, rotate: 0 }}
                transition={{ delay: 0.3, type: 'spring', stiffness: 200 }}
                className="text-8xl text-center mb-6"
              >
                {isVictory ? '🏆' : '💀'}
              </motion.div>

              {/* Title */}
              <motion.h2
                initial={{ y: 20, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ delay: 0.4 }}
                className={`
                  text-4xl font-bold text-center mb-4
                  ${isVictory ? 'text-yellow-400' : 'text-red-400'}
                `}
              >
                {isVictory ? 'CHIẾN THẮNG!' : 'THẤT BẠI!'}
              </motion.h2>

              {/* Winner name */}
              <motion.p
                initial={{ y: 20, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ delay: 0.5 }}
                className="text-xl text-center text-white mb-8"
              >
                {winnerName} đã chiến thắng!
              </motion.p>

              {/* Decorative stars */}
              {isVictory && (
                <motion.div
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                  transition={{ delay: 0.6 }}
                  className="flex justify-center gap-2 mb-8"
                >
                  {[0, 1, 2].map((i) => (
                    <motion.span
                      key={i}
                      animate={{
                        y: [0, -10, 0],
                        rotate: [0, 15, -15, 0],
                      }}
                      transition={{
                        duration: 1,
                        delay: i * 0.1,
                        repeat: Infinity,
                      }}
                      className="text-3xl"
                    >
                      ⭐
                    </motion.span>
                  ))}
                </motion.div>
              )}

              {/* Buttons */}
              <motion.div
                initial={{ y: 20, opacity: 0 }}
                animate={{ y: 0, opacity: 1 }}
                transition={{ delay: 0.7 }}
                className="flex gap-4 justify-center"
              >
                <button
                  onClick={onPlayAgain}
                  className={`
                    px-8 py-3 rounded-lg font-bold text-lg
                    transition-all duration-200 hover:scale-105
                    ${isVictory 
                      ? 'bg-yellow-500 hover:bg-yellow-400 text-black' 
                      : 'bg-red-500 hover:bg-red-400 text-white'
                    }
                  `}
                >
                  Chơi Lại
                </button>
                <button
                  onClick={onExit}
                  className="px-8 py-3 bg-slate-700 hover:bg-slate-600 text-white rounded-lg font-bold text-lg transition-all duration-200 hover:scale-105"
                >
                  Thoát
                </button>
              </motion.div>
            </div>

            {/* Confetti effect for victory */}
            {isVictory && (
              <div className="absolute inset-0 overflow-visible pointer-events-none">
                {[...Array(20)].map((_, i) => (
                  <motion.div
                    key={i}
                    className="absolute w-3 h-3 rounded-sm"
                    style={{
                      backgroundColor: ['#fbbf24', '#f97316', '#ef4444', '#22c55e', '#3b82f6'][i % 5],
                      left: `${10 + (i % 5) * 20}%`,
                    }}
                    initial={{ y: -50, opacity: 0, rotate: 0 }}
                    animate={{
                      y: 400,
                      opacity: [0, 1, 1, 0],
                      rotate: Math.random() * 720,
                      x: (Math.random() - 0.5) * 200,
                    }}
                    transition={{
                      duration: 2 + Math.random(),
                      delay: Math.random() * 0.5,
                      repeat: Infinity,
                      repeatDelay: Math.random() * 2,
                    }}
                  />
                ))}
              </div>
            )}
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}

