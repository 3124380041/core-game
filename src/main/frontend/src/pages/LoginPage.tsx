import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Button } from '../components/common';
import { authApi } from '../api/authApi';
import { useAuthStore } from '../store';

export function LoginPage() {
  const navigate = useNavigate();
  const { setCurrentPlayer } = useAuthStore();

  const [username, setUsername] = useState('player1');
  const [password, setPassword] = useState('player1');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async (event: React.FormEvent) => {
    event.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      const response = await authApi.login({ username, password });
      setCurrentPlayer(response.data.player);
      navigate('/lobby');
    } catch (err: any) {
      setError(err?.response?.data?.message || 'Đăng nhập thất bại');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-6">
      <form
        onSubmit={handleLogin}
        className="w-full max-w-md bg-slate-900/70 border border-slate-700 rounded-xl p-6 space-y-4"
      >
        <h1 className="text-2xl font-bold text-white text-center">Đăng nhập</h1>
        <p className="text-slate-400 text-sm text-center">Dùng tài khoản mẫu: player1 / player1</p>

        {error && (
          <div className="p-3 bg-red-500/20 border border-red-500 rounded-lg text-red-300 text-sm">
            {error}
          </div>
        )}

        <label className="block">
          <span className="text-slate-300 text-sm">Username</span>
          <input
            className="mt-1 w-full rounded-lg bg-slate-800 border border-slate-600 px-3 py-2 text-white"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
            required
          />
        </label>

        <label className="block">
          <span className="text-slate-300 text-sm">Password</span>
          <input
            className="mt-1 w-full rounded-lg bg-slate-800 border border-slate-600 px-3 py-2 text-white"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
            required
          />
        </label>

        <Button type="submit" className="w-full" isLoading={isLoading}>
          Vào game
        </Button>
      </form>
    </div>
  );
}

