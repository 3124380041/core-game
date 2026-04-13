import { motion, AnimatePresence } from 'framer-motion';

interface SkillEffectProps {
  skillName: string;
  isActive: boolean;
  position?: 'center' | 'actor' | 'target';
  onComplete?: () => void;
}

export function SkillEffect({
  skillName,
  isActive,
  position = 'center',
  onComplete,
}: SkillEffectProps) {
  return (
    <AnimatePresence>
      {isActive && (
        <motion.div
          initial={{ opacity: 0, scale: 0.5 }}
          animate={{ opacity: 1, scale: 1 }}
          exit={{ opacity: 0, scale: 1.5 }}
          onAnimationComplete={() => onComplete?.()}
          transition={{ duration: 0.6 }}
          className={`
            fixed inset-0 z-50 flex items-center justify-center pointer-events-none
            ${position === 'center' ? '' : ''}
          `}
        >
          {/* Dark overlay */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 0.5 }}
            exit={{ opacity: 0 }}
            className="absolute inset-0 bg-black"
          />

          {/* Skill name display */}
          <motion.div
            initial={{ y: 50, opacity: 0, scale: 0.8 }}
            animate={{ y: 0, opacity: 1, scale: 1 }}
            exit={{ y: -50, opacity: 0 }}
            transition={{
              duration: 0.4,
              type: 'spring',
              stiffness: 200,
            }}
            className="relative z-10"
          >
            {/* Glow effect */}
            <motion.div
              animate={{
                scale: [1, 1.2, 1],
                opacity: [0.5, 1, 0.5],
              }}
              transition={{ duration: 1, repeat: 1 }}
              className="absolute inset-0 blur-xl"
              style={{
                background: 'radial-gradient(circle, rgba(234,179,8,0.6) 0%, transparent 70%)',
              }}
            />

            {/* Skill name text */}
            <div className="relative px-8 py-4 bg-gradient-to-r from-yellow-600/80 via-orange-500/80 to-yellow-600/80 rounded-lg border-2 border-yellow-400">
              <motion.h2
                animate={{
                  textShadow: [
                    '0 0 10px rgba(234,179,8,0.5)',
                    '0 0 30px rgba(234,179,8,1)',
                    '0 0 10px rgba(234,179,8,0.5)',
                  ],
                }}
                transition={{ duration: 0.5, repeat: 2 }}
                className="text-3xl font-bold text-white text-center tracking-wider"
              >
                ✨ {skillName} ✨
              </motion.h2>
            </div>

            {/* Particle effects */}
            <div className="absolute inset-0 overflow-visible">
              {[...Array(12)].map((_, i) => (
                <motion.div
                  key={i}
                  className="absolute w-3 h-3 bg-yellow-400 rounded-full"
                  initial={{
                    x: 0,
                    y: 0,
                    opacity: 0,
                  }}
                  animate={{
                    x: Math.cos((i * 30) * Math.PI / 180) * 100,
                    y: Math.sin((i * 30) * Math.PI / 180) * 100,
                    opacity: [0, 1, 0],
                    scale: [0, 1, 0],
                  }}
                  transition={{
                    duration: 0.8,
                    delay: 0.2,
                    ease: 'easeOut',
                  }}
                  style={{
                    left: '50%',
                    top: '50%',
                    marginLeft: '-6px',
                    marginTop: '-6px',
                  }}
                />
              ))}
            </div>
          </motion.div>
        </motion.div>
      )}
    </AnimatePresence>
  );
}

// Quick attack/action effect (simpler, faster)
interface ActionEffectProps {
  actionType: 'BASIC_ATTACK' | 'ULTIMATE_SKILL' | 'SKIP';
  isActive: boolean;
  actorName: string;
}

export function ActionEffect({ actionType, isActive, actorName }: ActionEffectProps) {
  if (!isActive) return null;

  const effectConfig = {
    BASIC_ATTACK: {
      icon: '⚔️',
      color: 'from-red-600 to-orange-600',
      text: 'Tấn Công!',
    },
    ULTIMATE_SKILL: {
      icon: '💫',
      color: 'from-yellow-500 to-orange-500',
      text: 'Ultimate!',
    },
    SKIP: {
      icon: '💤',
      color: 'from-gray-600 to-slate-600',
      text: 'Bỏ Lượt',
    },
  };

  const config = effectConfig[actionType];

  return (
    <motion.div
      initial={{ opacity: 0, y: -20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: 20 }}
      className="fixed top-20 left-1/2 -translate-x-1/2 z-40 pointer-events-none"
    >
      <div className={`
        px-6 py-3 rounded-full bg-gradient-to-r ${config.color}
        flex items-center gap-3 shadow-lg
      `}>
        <span className="text-2xl">{config.icon}</span>
        <span className="text-white font-bold">
          {actorName} - {config.text}
        </span>
      </div>
    </motion.div>
  );
}

