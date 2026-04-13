import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { Button } from '../components/common';
import { GiCrossedSwords, GiTrophy, GiBookCover } from 'react-icons/gi';

export function HomePage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex flex-col items-center justify-center p-8">
      {/* Logo/Title */}
      <motion.div
        initial={{ opacity: 0, y: -50 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
        className="text-center mb-12"
      >
        <h1 className="text-5xl md:text-7xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 via-purple-500 to-pink-500 mb-4">
          ⚔️ Battle Arena
        </h1>
        <p className="text-slate-400 text-xl">
          Hệ thống chiến đấu theo lượt
        </p>
      </motion.div>

      {/* Menu Cards */}
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ delay: 0.3, duration: 0.5 }}
        className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-4xl w-full"
      >
        {/* Quick Battle */}
        <motion.div
          whileHover={{ scale: 1.05, y: -5 }}
          className="bg-gradient-to-br from-indigo-600/30 to-purple-600/30 rounded-2xl p-6 border border-indigo-500/50 cursor-pointer"
          onClick={() => navigate('/login')}
        >
          <div className="text-indigo-400 text-4xl mb-4">
            <GiCrossedSwords />
          </div>
          <h2 className="text-white text-xl font-bold mb-2">Chiến Đấu Nhanh</h2>
          <p className="text-slate-400 text-sm">
            Bắt đầu trận đấu ngay lập tức với đội hình ngẫu nhiên
          </p>
        </motion.div>

        {/* Custom Match */}
        <motion.div
          whileHover={{ scale: 1.05, y: -5 }}
          className="bg-gradient-to-br from-yellow-600/30 to-orange-600/30 rounded-2xl p-6 border border-yellow-500/50 cursor-pointer"
          onClick={() => navigate('/login')}
        >
          <div className="text-yellow-400 text-4xl mb-4">
            <GiTrophy />
          </div>
          <h2 className="text-white text-xl font-bold mb-2">Tạo Trận</h2>
          <p className="text-slate-400 text-sm">
            Tùy chọn đội hình và cài đặt trận đấu
          </p>
        </motion.div>

        {/* Guide */}
        <motion.div
          whileHover={{ scale: 1.05, y: -5 }}
          className="bg-gradient-to-br from-green-600/30 to-teal-600/30 rounded-2xl p-6 border border-green-500/50 cursor-pointer"
        >
          <div className="text-green-400 text-4xl mb-4">
            <GiBookCover />
          </div>
          <h2 className="text-white text-xl font-bold mb-2">Hướng Dẫn</h2>
          <p className="text-slate-400 text-sm">
            Tìm hiểu cách chơi và chiến thuật
          </p>
        </motion.div>
      </motion.div>

      {/* Quick Start Button */}
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.6 }}
        className="mt-12"
      >
        <Button
          variant="primary"
          size="lg"
          onClick={() => navigate('/login')}
          className="px-12"
        >
          🎮 Bắt Đầu Chơi
        </Button>
      </motion.div>

      {/* Footer */}
      <motion.footer
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ delay: 1 }}
        className="absolute bottom-4 text-slate-500 text-sm"
      >
        Turn-Based Battle System v1.0
      </motion.footer>
    </div>
  );
}

