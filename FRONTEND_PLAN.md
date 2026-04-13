# Plan: Card-Based UI cho Turn-Based RPG Battle System

## 📋 Tổng quan

Xây dựng giao diện người dùng dạng thẻ bài (card-based) cho hệ thống chiến đấu RPG turn-based, sử dụng **React + TypeScript** làm frontend, tích hợp với backend Spring Boot hiện có qua REST API. Thiết kế **modular** để dễ dàng upgrade lên 2D sau này.

---

## 🛠️ Tech Stack

| Mục | Công nghệ | Lý do |
|-----|-----------|-------|
| Build Tool | **Vite** | Nhanh, HMR tốt, easy setup |
| Framework | **React 18** | Component-based, ecosystem lớn |
| Language | **TypeScript** | Type safety, intellisense |
| Styling | **TailwindCSS** | Utility-first, responsive nhanh |
| Animation | **Framer Motion** | Declarative animations cho React |
| State | **Zustand** | Lightweight, simple API |
| HTTP Client | **Axios** | Interceptors, error handling |
| Icons | **react-icons** | Lucide/Heroicons cho game UI |

---

## 📁 Cấu trúc thư mục Frontend

```
src/main/frontend/
├── index.html
├── package.json
├── vite.config.ts
├── tailwind.config.js
├── tsconfig.json
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── index.css
│   │
│   ├── api/                      # API layer
│   │   ├── axiosClient.ts        # Base axios config
│   │   ├── matchApi.ts           # Match endpoints
│   │   ├── heroApi.ts            # Hero endpoints
│   │   └── playerApi.ts          # Player endpoints
│   │
│   ├── types/                    # TypeScript interfaces
│   │   ├── Hero.ts
│   │   ├── Match.ts
│   │   ├── TurnResult.ts
│   │   └── index.ts
│   │
│   ├── store/                    # State management
│   │   ├── useMatchStore.ts
│   │   ├── useGameStore.ts
│   │   └── useUIStore.ts
│   │
│   ├── components/
│   │   ├── cards/                # Thẻ bài components
│   │   │   ├── HeroCard.tsx      # Card nhân vật
│   │   │   ├── SkillCard.tsx     # Card skill
│   │   │   ├── ActionCard.tsx    # Card hành động
│   │   │   └── StatBar.tsx       # HP/MP bar component
│   │   │
│   │   ├── battle/               # Battle UI
│   │   │   ├── BattleArena.tsx   # Main battle layout
│   │   │   ├── TeamSection.tsx   # Hiển thị 1 team
│   │   │   ├── TurnOrderBar.tsx  # Thanh thứ tự lượt
│   │   │   ├── ActionPanel.tsx   # Panel chọn action
│   │   │   ├── TargetSelector.tsx# Chọn mục tiêu
│   │   │   └── CombatLog.tsx     # Log chiến đấu
│   │   │
│   │   ├── lobby/                # Pre-battle UI
│   │   │   ├── MatchLobby.tsx    # Chờ trận
│   │   │   ├── TeamBuilder.tsx   # Xếp đội hình
│   │   │   └── HeroSelector.tsx  # Chọn hero
│   │   │
│   │   └── common/               # Shared components
│   │       ├── Button.tsx
│   │       ├── Modal.tsx
│   │       └── Loading.tsx
│   │
│   ├── pages/
│   │   ├── HomePage.tsx
│   │   ├── LobbyPage.tsx
│   │   ├── BattlePage.tsx
│   │   └── ResultPage.tsx
│   │
│   ├── hooks/                    # Custom hooks
│   │   ├── useMatch.ts
│   │   ├── useBattlePolling.ts
│   │   └── useAnimation.ts
│   │
│   └── utils/
│       ├── constants.ts
│       └── helpers.ts
```

---

## 📐 Thiết kế UI/UX

### 1. Hero Card Design

```
┌─────────────────────────┐
│  ★★★★☆  [TANK]         │  <- Stars + Type badge
│  ┌─────────────────┐    │
│  │                 │    │
│  │   Hero Avatar   │    │  <- Placeholder/Image
│  │   (Gradient BG) │    │
│  │                 │    │
│  └─────────────────┘    │
│  「 Tên Nhân Vật 」     │  <- Name
│  ─────────────────────  │
│  HP ████████░░ 80/100   │  <- HP bar (green->red)
│  MP ██████░░░░ 60/100   │  <- MP/Nộ Khí (blue)
│  ─────────────────────  │
│  STR: 25  AGI: 18       │  <- Stats
│  VIT: 30  ESS: 15       │
└─────────────────────────┘
```

### 2. Battle Arena Layout

```
┌──────────────────────────────────────────────────────────────────┐
│  ROUND 3                              ⏱️ Turn: Hero A            │
├──────────────────────────────────────────────────────────────────┤
│  Turn Order: [Hero1] → Hero2 → Hero3 → Hero4 → Hero5 → Hero6    │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│   TEAM 1 (Player)              VS              TEAM 2 (Enemy)    │
│  ┌─────┐ ┌─────┐ ┌─────┐          ┌─────┐ ┌─────┐ ┌─────┐      │
│  │Card1│ │Card2│ │Card3│          │Card4│ │Card5│ │Card6│      │
│  │ HP  │ │ HP  │ │ HP  │          │ HP  │ │ HP  │ │ HP  │      │
│  │ MP  │ │ MP  │ │ MP  │          │ MP  │ │ MP  │ │ MP  │      │
│  └─────┘ └─────┘ └─────┘          └─────┘ └─────┘ └─────┘      │
│                                                                  │
├──────────────────────────────────────────────────────────────────┤
│  ┌────────────┐  ┌────────────┐  ┌────────────┐                 │
│  │  ⚔️ ATTACK │  │  ✨ SKILL  │  │  ⏭️ PASS   │  <- Actions    │
│  │ Basic Hit  │  │ (MP: 100)  │  │ Skip Turn  │                 │
│  └────────────┘  └────────────┘  └────────────┘                 │
├──────────────────────────────────────────────────────────────────┤
│  Combat Log:                                                     │
│  > Hero A attacks Enemy B for 45 damage!                        │
│  > Enemy B takes 45 damage (HP: 55/100)                         │
│  > Hero A gains 10 MP (MP: 60/100)                              │
└──────────────────────────────────────────────────────────────────┘
```

### 3. Màu sắc theo Hero Type

| HeroType | Primary Color | Gradient |
|----------|---------------|----------|
| TANK | `#3B82F6` Blue | blue-500 → blue-700 |
| ATTACK_PHYS | `#EF4444` Red | red-500 → red-700 |
| ATTACK_MAGIC | `#A855F7` Purple | purple-500 → purple-700 |
| SUPPORT | `#22C55E` Green | green-500 → green-700 |

---

## 🔄 Luồng hoạt động

### Game Flow

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  HomePage   │ --> │  LobbyPage  │ --> │ BattlePage  │ --> │ ResultPage  │
│  - Start    │     │  - Create   │     │  - Fight!   │     │  - Victory  │
│    Game     │     │    Match    │     │  - Actions  │     │  - Rewards  │
└─────────────┘     │  - Select   │     │  - Watch    │     │  - Rematch  │
                    │    Heroes   │     │    turns    │     └─────────────┘
                    └─────────────┘     └─────────────┘
```

### Battle Turn Flow

```
1. GET /api/matches/{id} -> Load match state
2. Check: Is it my hero's turn?
   ├─ YES: Show ActionPanel
   │       User selects: Attack/Skill/Pass
   │       User selects: Target (if needed)
   │       POST /api/matches/{id}/action -> Submit action
   │       Receive TurnResultResponse
   │       Play animation based on result
   │       Update state
   │       Loop to step 1
   │
   └─ NO: Wait/Poll for updates
         (or WebSocket notification)
```

---

## 📋 Các bước triển khai

### Phase 1: Setup & Foundation (1-2 ngày) ✅ HOÀN THÀNH

- [x] **Step 1.1**: Khởi tạo project frontend
  ```bash
  cd src/main
  npm create vite@latest frontend -- --template react-ts
  cd frontend
  npm install
  ```

- [x] **Step 1.2**: Cài đặt dependencies
  ```bash
  npm install axios zustand framer-motion react-router-dom react-icons
  npm install -D tailwindcss @tailwindcss/vite
  ```

- [x] **Step 1.3**: Cấu hình Vite proxy (`vite.config.ts`)
  ```typescript
  export default defineConfig({
    server: {
      proxy: {
        '/api': 'http://localhost:8080'
      }
    }
  })
  ```

- [x] **Step 1.4**: Thêm CORS config cho Spring Boot
  ```java
  @Configuration
  public class CorsConfig implements WebMvcConfigurer {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
          registry.addMapping("/api/**")
              .allowedOrigins("http://localhost:5173")
              .allowedMethods("*");
      }
  }
  ```

### Phase 2: TypeScript Types & API Layer (1 ngày) ✅ HOÀN THÀNH

- [x] **Step 2.1**: Tạo TypeScript interfaces từ các DTO

  ```typescript
  // types/Hero.ts
  export interface HeroResponse {
    id: number;
    name: string;
    heroType: 'TANK' | 'ATTACK_PHYS' | 'ATTACK_MAGIC' | 'SUPPORT';
    stars: number;
    strength: number;
    agility: number;
    vitality: number;
    intelligence: number;
    maxHealth: number;
    maxMp: number;
    ultimateSkillName: string;
    ultimateSkillDescription: string;
  }

  // types/Match.ts
  export interface MatchResponse {
    id: number;
    status: 'WAITING' | 'IN_PROGRESS' | 'FINISHED';
    currentRound: number;
    currentTurnHeroId: number;
    player1: PlayerInfo;
    player2: PlayerInfo;
    heroStates: HeroStateInfo[];
    winnerPlayerId: number | null;
  }

  export interface HeroStateInfo {
    odtHeroId: number;
    heroId: number;
    name: string;
    playerId: number;
    currentHealth: number;
    maxHealth: number;
    currentMp: number;
    maxMp: number;
    positionRow: number;
    positionCol: number;
    isDefeated: boolean;
  }

  // types/TurnResult.ts
  export interface TurnResultResponse {
    actorHeroId: number;
    targetHeroId: number;
    actionType: 'BASIC_ATTACK' | 'ULTIMATE_SKILL' | 'PASS';
    skillName: string | null;
    damageDealt: number;
    healingDone: number;
    isCritical: boolean;
    isDodged: boolean;
    targetDefeated: boolean;
    description: string;
    updatedHeroStates: HeroStateInfo[];
    matchStatus: string;
    nextTurnHeroId: number;
  }
  ```

- [x] **Step 2.2**: Tạo API service layer
  ```typescript
  // api/matchApi.ts
  export const matchApi = {
    create: (data: CreateMatchRequest) => 
      axios.post<MatchResponse>('/api/matches', data),
    
    getById: (id: number) => 
      axios.get<MatchResponse>(`/api/matches/${id}`),
    
    submitAction: (matchId: number, action: CombatActionRequest) =>
      axios.post<TurnResultResponse>(`/api/matches/${matchId}/action`, action),
    
    start: (matchId: number) =>
      axios.post<MatchResponse>(`/api/matches/${matchId}/start`),
  };
  ```

### Phase 3: Core Components (2-3 ngày) ✅ HOÀN THÀNH

- [x] **Step 3.1**: `StatBar.tsx` - HP/MP bar với animation
- [x] **Step 3.2**: `HeroCard.tsx` - Card hiển thị nhân vật
- [x] **Step 3.3**: `ActionCard.tsx` - Card hành động (Attack/Skill/Pass)
- [x] **Step 3.4**: `TurnOrderBar.tsx` - Thanh thứ tự lượt đánh

### Phase 4: Battle Screen (2-3 ngày) ✅ HOÀN THÀNH

- [x] **Step 4.1**: `TeamSection.tsx` - Hiển thị team heroes
- [x] **Step 4.2**: `BattleArena.tsx` - Main battle layout
- [x] **Step 4.3**: `ActionPanel.tsx` - Chọn hành động + target
- [x] **Step 4.4**: `CombatLog.tsx` - Hiển thị combat history

### Phase 5: Game State & Logic (2 ngày) ✅ HOÀN THÀNH

- [x] **Step 5.1**: `useMatchStore.ts` - Zustand store cho match state
- [x] **Step 5.2**: `useBattlePolling.ts` - Hook polling match updates
- [x] **Step 5.3**: Integrate state với components

### Phase 6: Animations & Polish (2-3 ngày)

- [ ] **Step 6.1**: Card flip animation khi reveal
- [ ] **Step 6.2**: Attack/damage animation (shake, flash)
- [ ] **Step 6.3**: Damage number popup
- [ ] **Step 6.4**: HP/MP bar smooth transitions
- [ ] **Step 6.5**: Victory/Defeat screens
- [ ] **Step 6.6**: Sound effects (optional)

### Phase 7: Lobby & Flow (1-2 ngày)

- [ ] **Step 7.1**: `HeroSelector.tsx` - Grid chọn heroes
- [ ] **Step 7.2**: `TeamBuilder.tsx` - Drag/drop xếp đội hình
- [ ] **Step 7.3**: `MatchLobby.tsx` - Waiting room
- [ ] **Step 7.4**: Route navigation setup

---

## 🔌 API Endpoints cần sử dụng

### Từ Backend hiện có:

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/heroes` | Lấy danh sách heroes |
| GET | `/api/heroes/{id}` | Chi tiết 1 hero |
| POST | `/api/matches` | Tạo match mới |
| GET | `/api/matches/{id}` | Lấy state match |
| POST | `/api/matches/{id}/start` | Bắt đầu match |
| POST | `/api/matches/{id}/action` | Submit combat action |
| GET | `/api/players/{id}` | Thông tin player |
| GET | `/api/players/{id}/heroes` | Heroes của player |

### Request/Response đã có:

- `CreateMatchRequest`: `{ player1Id, player2Id, hero1Ids[], hero2Ids[] }`
- `CombatActionRequest`: `{ actorHeroId, actionType, targetHeroId? }`
- `TurnResultResponse`: Chi tiết kết quả lượt đánh

---

## 🎯 Milestones

| Milestone | Mục tiêu | Timeline |
|-----------|----------|----------|
| **M1** | Setup xong, có thể gọi API | Day 2 |
| **M2** | Hiển thị được HeroCard với data thật | Day 4 |
| **M3** | Battle Arena layout hoàn chỉnh | Day 7 |
| **M4** | Có thể chơi 1 trận đầy đủ | Day 10 |
| **M5** | Animations + Polish | Day 14 |
| **M6** | Lobby flow + Production ready | Day 17 |

---

## 🚀 Chuyển đổi sang 2D (Future)

Khi chuyển từ Card-based sang 2D:

1. **Giữ nguyên**: API layer, State management, Game logic
2. **Thay thế**: 
   - `HeroCard` → `HeroSprite` với sprite sheets
   - `BattleArena` → Canvas/Pixi.js scene
   - CSS animations → Sprite animations
3. **Thêm mới**:
   - Phaser.js hoặc Pixi.js cho 2D rendering
   - Sprite asset management
   - Parallax backgrounds
   - Particle effects

Thiết kế modular hiện tại giúp việc chuyển đổi dễ dàng hơn vì:
- Business logic tách biệt khỏi UI
- API calls reusable
- State management independent

---

## ⚠️ Lưu ý quan trọng

1. **Polling vs WebSocket**: Bắt đầu với polling (đơn giản), thêm WebSocket sau nếu cần real-time tốt hơn

2. **Backend cần kiểm tra**:
   - CORS đã được cấu hình chưa?
   - Endpoint `/api/matches/{id}/action` trả về đúng `TurnResultResponse`?
   - Turn order logic hoạt động đúng không?

3. **Error Handling**: Cần xử lý:
   - Network errors
   - Invalid actions (không phải lượt của mình)
   - Match đã kết thúc

4. **Testing**: 
   - Mock API cho development
   - E2E test với Playwright/Cypress

---

## 📝 Checklist trước khi bắt đầu

- [ ] Backend chạy được ở `localhost:8080`
- [ ] Có thể tạo match qua API (Postman/curl test)
- [ ] Database có sẵn hero data
- [ ] Node.js 18+ đã cài đặt

---

*Tạo ngày: 10/04/2026*
*Version: 1.0*

