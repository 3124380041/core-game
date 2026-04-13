import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import type { ReactNode } from 'react';
import { HomePage, LobbyPage, BattlePage, LoginPage } from './pages';
import { useAuthStore } from './store';

function ProtectedRoute({ children }: { children: ReactNode }) {
  const currentPlayer = useAuthStore((state) => state.currentPlayer);
  if (!currentPlayer) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/lobby" element={<ProtectedRoute><LobbyPage /></ProtectedRoute>} />
        <Route path="/battle/:matchId" element={<ProtectedRoute><BattlePage /></ProtectedRoute>} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
