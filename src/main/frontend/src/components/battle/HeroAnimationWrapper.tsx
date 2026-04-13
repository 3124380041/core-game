import { motion, type Variants } from 'framer-motion';
import type { ReactNode } from 'react';
import type { AnimationState } from '../../types';

interface HeroAnimationWrapperProps {
  children: ReactNode;
  animationState: AnimationState;
  isCurrentActor?: boolean;
}

// Animation variants for different states
const animationVariants: Variants = {
  idle: {
    x: 0,
    y: 0,
    scale: 1,
    rotate: 0,
    filter: 'brightness(1) grayscale(0)',
  },
  attacking: {
    x: [0, 30, -10, 0],
    scale: [1, 1.1, 1],
    transition: {
      duration: 0.4,
      ease: 'easeInOut' as const,
    },
  },
  damaged: {
    x: [-5, 5, -5, 5, 0],
    filter: ['brightness(1)', 'brightness(1.5)', 'brightness(0.8)', 'brightness(1)'],
    transition: {
      duration: 0.3,
      ease: 'easeInOut' as const,
    },
  },
  healing: {
    y: [0, -5, 0],
    scale: [1, 1.05, 1],
    filter: 'brightness(1.2)',
    transition: {
      duration: 0.6,
      ease: 'easeOut' as const,
    },
  },
  skill: {
    scale: [1, 1.15, 1.05, 1],
    rotate: [0, -2, 2, 0],
    transition: {
      duration: 0.8,
      ease: 'easeInOut' as const,
    },
  },
  defeated: {
    scale: 0.9,
    rotate: -5,
    filter: 'brightness(0.5) grayscale(1)',
    opacity: 0.6,
    transition: {
      duration: 0.8,
      ease: 'easeOut' as const,
    },
  },
  victory: {
    y: [0, -10, 0],
    scale: [1, 1.1, 1],
    transition: {
      duration: 0.5,
      repeat: 3,
      ease: 'easeInOut' as const,
    },
  },
};

// Glow colors for different states
const glowColors = {
  idle: 'none',
  attacking: '0 0 30px rgba(239, 68, 68, 0.6)', // Red glow
  damaged: '0 0 20px rgba(255, 100, 100, 0.5)', // Red flash
  healing: '0 0 30px rgba(34, 197, 94, 0.6)', // Green glow
  skill: '0 0 40px rgba(234, 179, 8, 0.8)', // Golden glow
  defeated: 'none',
  victory: '0 0 40px rgba(234, 179, 8, 0.6)', // Golden glow
};

export function HeroAnimationWrapper({
  children,
  animationState,
  isCurrentActor = false,
}: HeroAnimationWrapperProps) {
  return (
    <motion.div
      className="relative"
      initial="idle"
      animate={animationState}
      variants={animationVariants}
      style={{
        boxShadow: glowColors[animationState],
        borderRadius: '0.75rem',
      }}
    >
      {/* Healing particles effect */}
      {animationState === 'healing' && (
        <div className="absolute inset-0 pointer-events-none overflow-hidden rounded-xl">
          {[...Array(6)].map((_, i) => (
            <motion.div
              key={i}
              className="absolute w-2 h-2 bg-green-400 rounded-full"
              initial={{
                x: Math.random() * 100,
                y: 100,
                opacity: 0,
              }}
              animate={{
                y: -20,
                opacity: [0, 1, 0],
              }}
              transition={{
                duration: 1,
                delay: i * 0.1,
                ease: 'easeOut',
              }}
            />
          ))}
        </div>
      )}

      {/* Ultimate skill aura */}
      {animationState === 'skill' && (
        <motion.div
          className="absolute inset-0 rounded-xl pointer-events-none"
          initial={{ opacity: 0 }}
          animate={{
            opacity: [0, 0.8, 0],
            scale: [1, 1.3, 1.5],
          }}
          transition={{ duration: 0.8 }}
          style={{
            background: 'radial-gradient(circle, rgba(234,179,8,0.4) 0%, transparent 70%)',
          }}
        />
      )}

      {/* Attack slash effect */}
      {animationState === 'attacking' && (
        <motion.div
          className="absolute -right-8 top-1/2 -translate-y-1/2 text-4xl pointer-events-none"
          initial={{ opacity: 0, x: -20, rotate: -45 }}
          animate={{ opacity: [0, 1, 0], x: 20, rotate: 45 }}
          transition={{ duration: 0.3 }}
        >
          ⚔️
        </motion.div>
      )}

      {/* Current actor indicator pulse */}
      {isCurrentActor && animationState === 'idle' && (
        <motion.div
          className="absolute -inset-1 rounded-xl border-2 border-indigo-400 pointer-events-none"
          animate={{ opacity: [0.3, 1, 0.3] }}
          transition={{ duration: 1.5, repeat: Infinity }}
        />
      )}

      {children}
    </motion.div>
  );
}

