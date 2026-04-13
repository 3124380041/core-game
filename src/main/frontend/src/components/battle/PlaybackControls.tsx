import { motion } from 'framer-motion';
import {
  GiPauseButton,
  GiPlayButton,
} from 'react-icons/gi';
import { FaForward, FaBackward } from 'react-icons/fa';

interface PlaybackControlsProps {
  isPlaying: boolean;
  isPaused: boolean;
  progress: { current: number; total: number };
  playbackSpeed: number;
  onPlay: () => void;
  onPause: () => void;
  onResume: () => void;
  onSkipToEnd: () => void;
  onReset: () => void;
  onSpeedChange: (speed: number) => void;
}

const speedOptions = [0.5, 1, 1.5, 2];

export function PlaybackControls({
  isPlaying,
  isPaused,
  progress,
  playbackSpeed,
  onPlay,
  onPause,
  onResume,
  onSkipToEnd,
  onReset,
  onSpeedChange,
}: PlaybackControlsProps) {
  const progressPercent = progress.total > 0
    ? (progress.current / progress.total) * 100
    : 0;

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="bg-slate-800/90 rounded-xl p-4 border border-slate-700"
    >
      {/* Progress bar */}
      <div className="mb-4">
        <div className="flex justify-between text-sm text-slate-400 mb-2">
          <span>Tiến trình</span>
          <span>{progress.current} / {progress.total}</span>
        </div>
        <div className="w-full h-2 bg-slate-700 rounded-full overflow-hidden">
          <motion.div
            className="h-full bg-gradient-to-r from-indigo-500 to-purple-500"
            initial={{ width: 0 }}
            animate={{ width: `${progressPercent}%` }}
            transition={{ duration: 0.3 }}
          />
        </div>
      </div>

      {/* Controls */}
      <div className="flex items-center justify-center gap-4">
        {/* Reset */}
        <button
          onClick={onReset}
          className="p-2 text-slate-400 hover:text-white transition-colors"
          title="Phát lại từ đầu"
        >
          <FaBackward className="w-5 h-5" />
        </button>

        {/* Play/Pause */}
        <button
          onClick={isPlaying && !isPaused ? onPause : (isPaused ? onResume : onPlay)}
          className="p-4 bg-indigo-600 hover:bg-indigo-500 rounded-full text-white transition-colors"
        >
          {isPlaying && !isPaused ? (
            <GiPauseButton className="w-6 h-6" />
          ) : (
            <GiPlayButton className="w-6 h-6" />
          )}
        </button>

        {/* Skip to end */}
        <button
          onClick={onSkipToEnd}
          className="p-2 text-slate-400 hover:text-white transition-colors"
          title="Bỏ qua đến kết quả"
        >
          <FaForward className="w-5 h-5" />
        </button>
      </div>

      {/* Speed controls */}
      <div className="flex items-center justify-center gap-2 mt-4">
        <span className="text-sm text-slate-400 mr-2">Tốc độ:</span>
        {speedOptions.map((speed) => (
          <button
            key={speed}
            onClick={() => onSpeedChange(speed)}
            className={`
              px-3 py-1 rounded text-sm font-medium transition-colors
              ${playbackSpeed === speed 
                ? 'bg-indigo-600 text-white' 
                : 'bg-slate-700 text-slate-400 hover:text-white hover:bg-slate-600'
              }
            `}
          >
            {speed}x
          </button>
        ))}
      </div>

      {/* Status indicator */}
      <div className="flex items-center justify-center gap-2 mt-3">
        <div className={`
          w-2 h-2 rounded-full
          ${isPlaying && !isPaused ? 'bg-green-400 animate-pulse' : 
            isPaused ? 'bg-yellow-400' : 'bg-slate-500'}
        `} />
        <span className="text-xs text-slate-400">
          {isPlaying && !isPaused ? 'Đang phát' :
            isPaused ? 'Tạm dừng' :
            progress.current >= progress.total ? 'Hoàn thành' : 'Sẵn sàng'}
        </span>
      </div>
    </motion.div>
  );
}

