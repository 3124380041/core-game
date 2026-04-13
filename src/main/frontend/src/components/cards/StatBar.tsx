import { motion } from 'framer-motion';
import { getHealthPercentage, getHealthBarColor, getMpBarColor } from '../../utils';

interface StatBarProps {
  current: number;
  max: number;
  type: 'hp' | 'mp';
  showText?: boolean;
  size?: 'sm' | 'md' | 'lg';
}

const sizeStyles = {
  sm: { height: 'h-2', text: 'text-xs' },
  md: { height: 'h-3', text: 'text-sm' },
  lg: { height: 'h-4', text: 'text-base' },
};

export function StatBar({
  current,
  max,
  type,
  showText = true,
  size = 'md'
}: StatBarProps) {
  const percentage = getHealthPercentage(current, max);
  const { height, text } = sizeStyles[size];

  const barColor = type === 'hp'
    ? getHealthBarColor(percentage)
    : getMpBarColor(percentage);

  const bgColor = type === 'hp' ? 'bg-red-900/50' : 'bg-blue-900/50';
  const label = type === 'hp' ? 'HP' : 'MP';

  return (
    <div className="w-full">
      {showText && (
        <div className={`flex justify-between ${text} mb-1`}>
          <span className="text-slate-400 font-medium">{label}</span>
          <span className="text-white font-bold">
            {current}/{max}
          </span>
        </div>
      )}
      <div className={`w-full ${height} ${bgColor} rounded-full overflow-hidden`}>
        <motion.div
          className={`h-full ${barColor} rounded-full`}
          initial={{ width: 0 }}
          animate={{ width: `${percentage}%` }}
          transition={{ duration: 0.5, ease: 'easeOut' }}
        />
      </div>
    </div>
  );
}

