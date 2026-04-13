# AGENTS.md - Turn-Based Game Battle System

## 🎯 Quick Context

**Mục đích**: Hệ thống chiến đấu RPG turn-based card UI, hiện có cả **PvP** và **Phó bản (PvE)**.

**Stack**: Java 21 + Spring Boot (backend) | React + TypeScript + Vite (frontend)

**Ports**: Backend `:8080` | Frontend `:5173` (proxy `/api` → backend)

---

## 📌 Current Stage (14/04/2026)

### Gameplay Loop
- ✅ PvP quick battle: `start -> simulate-with-history -> replay`
- ✅ PvE dungeon battle: `start run -> start map battle -> play match -> resolve run`
- ✅ Battle replay animation cho chế độ quick match
- ⚠️ PvE đã implement logic, **chưa test end-to-end**

### Backend Status
- ✅ Match APIs (start/get/action/auto/simulate/simulate-with-history)
- ✅ Dungeon APIs (`DungeonController`)
- ✅ Dungeon domain/repository/service (`Dungeon`, `DungeonMap`, `DungeonRun`, `DungeonService`)
- ✅ Match mode metadata (`MatchMode.PVP`, `MatchMode.DUNGEON`)
- ✅ Seed dữ liệu phó bản cơ bản (`DungeonDataInitializer`)

### Frontend Status
- ✅ API layers: `matchApi`, `heroApi`, `playerApi`, `dungeonApi`
- ✅ Lobby có nút `Vào Phó Bản`
- ✅ Store mapping backend `team1/team2` -> frontend `initialHeroStates`
- ✅ `npm run build` pass

---

## 📁 Main Paths

- Backend API controllers: `src/main/java/org/example/controller`
- Backend game services: `src/main/java/org/example/service`
- Dungeon entities: `src/main/java/org/example/domain/entity`
- Frontend pages: `src/main/frontend/src/pages`
- Frontend battle components: `src/main/frontend/src/components/battle`
- Frontend state: `src/main/frontend/src/store`

---

## 🔌 Key APIs

### PvP
- `POST /api/matches/start`
- `GET /api/matches/{id}`
- `POST /api/matches/{id}/action?playerId={pid}`
- `POST /api/matches/{id}/simulate-with-history`

### Dungeon (PvE)
- `GET /api/dungeons`
- `GET /api/dungeons/{dungeonId}/maps`
- `POST /api/dungeons/{dungeonId}/runs?playerId={pid}`
- `POST /api/dungeons/start-battle?playerId={pid}&dungeonId={did}`
- `POST /api/dungeons/runs/{runId}/start-battle?playerId={pid}`
- `POST /api/dungeons/runs/{runId}/resolve?playerId={pid}`
- `GET /api/dungeons/runs/{runId}?playerId={pid}`

---

## 🎮 Flows

### Quick Match (PvP)
1. Lobby gọi `/api/matches/start`
2. Lobby gọi `/api/matches/{id}/simulate-with-history`
3. Store set replay data
4. BattlePage render `BattleReplayArena`

### Dungeon Battle (PvE)
1. Lobby gọi `/api/dungeons/start-battle`
2. Backend tạo run + match map hiện tại
3. Frontend navigate `/battle/{matchId}` và chơi trận
4. Sau khi trận kết thúc, gọi `/api/dungeons/runs/{runId}/resolve`
5. Nếu chưa clear dungeon -> start trận map tiếp theo

---

## ✅ Completed

- [x] Frontend project setup + battle UI cards
- [x] Match simulation/replay system
- [x] Backend endpoint `simulate-with-history`
- [x] Backend endpoint heroes theo player
- [x] Dungeon domain + service + controller
- [x] Frontend `dungeonApi` + Lobby entry button
- [x] Frontend build pass (`npm run build`)

---

## 🚧 Next Implementation Plan

### Phase A - Dungeon E2E Validation (Ưu tiên cao)
1. Gắn `runId` vào frontend state khi start dungeon battle
2. Tự động gọi `resolveRun` sau khi trận `COMPLETED`
3. Điều hướng map tiếp theo hoặc màn `CLEARED/FAILED`
4. Hiển thị tiến trình run: `map hiện tại / tổng map`

### Phase B - Dungeon Rules Hardening
1. Chốt luật thua: retry map hay fail run (hiện đang fail run)
2. Reward logic theo map/clear dungeon
3. Chặn start battle khi run không ở trạng thái `IN_PROGRESS`
4. Chặn resolve nhiều lần cùng một match

### Phase C - UX cho Dungeon
1. Trang chọn phó bản (list/map info)
2. Màn kết quả run (clear/fail + reward)
3. Combat log tóm tắt theo từng map

---

## 🧪 Dungeon Test Plan (Bắt buộc trước khi mở rộng)

### 1) Manual API Test
- [ ] `GET /api/dungeons` trả danh sách phó bản
- [ ] `POST /api/dungeons/start-battle?playerId=...&dungeonId=...` trả `run + match`
- [ ] Chơi match đến `COMPLETED`
- [ ] `POST /api/dungeons/runs/{runId}/resolve?playerId=...`:
  - thắng map giữa: tăng `currentMapIndex`
  - thắng map cuối: `status = CLEARED`
  - thua: `status = FAILED`
- [ ] `GET /api/dungeons/runs/{runId}` đúng trạng thái mới nhất

### 2) Frontend Flow Test
- [ ] Từ Lobby bấm `Vào Phó Bản` vào được battle
- [ ] Battle render đủ 2 team
- [ ] Kết thúc trận có thể resolve run đúng nhánh
- [ ] Không bị mất state khi chuyển map tiếp theo

### 3) Acceptance Checklist
- [ ] Một run chỉ có 1 `activeMatchId` tại một thời điểm
- [ ] Không resolve được khi match chưa kết thúc
- [ ] Không start map battle mới khi run đã `CLEARED/FAILED`
- [ ] Người chơi không thể resolve run của người khác

---

## ⚠️ Known Issues

1. Một số cảnh báo IDE JPA table/column là false-positive khi dùng `ddl-auto=create-drop`
2. Security đang mở cho môi trường dev
3. Chưa có WebSocket realtime
4. PvE chưa có màn tiến trình/resolve tự động trên frontend

---

## 🛠️ Commands

```bash
# Frontend
cd game/src/main/frontend
npm run build
npm run dev
```

```bash
# Backend (môi trường có Maven)
cd game
mvn spring-boot:run
```

---

*Last updated: 14/04/2026*
