# Turn-Based Battle Game API

Game chiến thuật theo lượt với Spring Boot REST API.

## 🚀 Yêu Cầu

- Java 21+
- Maven 3.8+

## 📦 Cài Đặt & Chạy

### 1. Build project
```bash
mvn clean install
```

### 2. Chạy ứng dụng
```bash
mvn spring-boot:run
```

Hoặc chạy JAR:
```bash
java -jar target/game-1.0-SNAPSHOT.jar
```

Server sẽ chạy tại: `http://localhost:8080`

### 3. Truy cập H2 Console (Database)
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:gamedb`
- Username: `sa`
- Password: (để trống)

## 🎮 Dữ Liệu Test

Khi khởi động, hệ thống tự động tạo:
- **Player 1** (ID: 1) với team 5 heroes
- **Player 2** (ID: 2) với team 5 heroes
- 8 skill templates

Tài khoản dev mặc định:
- `username`: `player1`
- `password`: `player1`

## 📡 API Endpoints

### Player APIs
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/players` | Tạo player mới |
| GET | `/api/players/{id}` | Lấy thông tin player |
| GET | `/api/players` | Lấy tất cả players |

### Auth APIs
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/auth/login` | Đăng nhập theo username/password |

### Hero APIs
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/heroes?playerId={id}` | Tạo hero cho player |
| GET | `/api/heroes/{id}` | Lấy thông tin hero |
| GET | `/api/heroes?playerId={id}` | Lấy heroes của player |
| POST | `/api/heroes/{id}/levelup` | Tăng level hero |
| POST | `/api/heroes/{id}/starup` | Tăng sao hero |

### Team APIs
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/teams?playerId={id}&name={name}` | Tạo team |
| GET | `/api/teams/{id}` | Lấy thông tin team |
| POST | `/api/teams/{id}/heroes?heroId={id}&row={r}&col={c}` | Thêm hero vào team |
| POST | `/api/teams/{id}/activate?playerId={id}` | Set active team |

### Match APIs
| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/api/matches/start` | Tạo match mới |
| GET | `/api/matches/{id}` | Lấy trạng thái match |
| POST | `/api/matches/{id}/action?playerId={id}` | Submit action |
| POST | `/api/matches/{id}/auto` | Auto-play turn |
| POST | `/api/matches/{id}/simulate` | Simulate full battle |
| GET | `/api/matches/{id}/logs` | Lấy combat logs |

## 🎯 Ví Dụ API Calls

### Tạo Match
```bash
curl -X POST http://localhost:8080/api/matches/start \
  -H "Content-Type: application/json" \
  -d '{"player1Id": 1, "player2Id": 2}'
```

### Submit Action (Basic Attack)
```bash
curl -X POST "http://localhost:8080/api/matches/1/action?playerId=1" \
  -H "Content-Type: application/json" \
  -d '{"heroId": 1, "actionType": "ATTACK"}'
```

### Submit Action (Use Skill)
```bash
curl -X POST "http://localhost:8080/api/matches/1/action?playerId=1" \
  -H "Content-Type: application/json" \
  -d '{"heroId": 1, "skillId": 1, "targetIds": [6]}'
```

### Simulate Full Battle
```bash
curl -X POST http://localhost:8080/api/matches/1/simulate
```

## 🏗️ Cấu Trúc Project

```
src/main/java/org/example/
├── GameApplication.java       # Spring Boot entry point
├── controller/                # REST Controllers
│   ├── PlayerController.java
│   ├── HeroController.java
│   ├── TeamController.java
│   └── MatchController.java
├── service/                   # Business Logic
│   ├── PlayerService.java
│   ├── HeroService.java
│   ├── TeamService.java
│   └── MatchService.java
├── engine/                    # Combat Engine
│   ├── CombatEngine.java
│   ├── DamageCalculator.java
│   ├── EffectProcessor.java
│   └── TargetSelector.java
├── domain/
│   ├── entity/               # JPA Entities
│   ├── enums/                # Enumerations
│   └── runtime/              # Runtime States
├── dto/                      # Data Transfer Objects
├── repository/               # JPA Repositories
├── config/                   # Configuration
└── exception/                # Exception Handlers
```

## ⚔️ Combat System

### Turn Order
- Dựa trên Speed stat (cao hơn đi trước)
- Tính lại mỗi round

### Damage Formula
- **Physical**: `(ATK × Scaling) - DEF`
- **Magic**: `(INT × Scaling) - DEF/2`
- **True**: `ATK × Scaling` (không bị giảm)

### Combat Rates
- **Crit Rate**: Tỷ lệ gây chí mạng (×1.5 damage)
- **Dodge Rate**: Tỷ lệ né đòn
- **Block Rate**: Tỷ lệ đỡ (giảm 50% damage)

### MP System (Nộ Khí)
- Bắt đầu: 50/100
- +10 khi tấn công
- +15 khi bị đánh
- +25 mỗi round
- Ultimate skill cần 100 MP

## 📊 Hero Types

| Type | Vai trò |
|------|---------|
| TANK | Đỡ đòn, HP cao, DEF cao |
| ATTACK_PHYS | Sát thương vật lý |
| ATTACK_MAGIC | Sát thương phép |
| SUPPORT | Hỗ trợ, hồi máu |

## 🎭 Effect Types

| Effect | Mô tả |
|--------|-------|
| DAMAGE | Gây sát thương |
| HEAL | Hồi máu |
| BUFF | Tăng stat |
| DEBUFF | Giảm stat |
| POISON | DOT mỗi turn |
| BURN | DOT + giảm DEF |
| STUN | Bỏ qua lượt |
| REVIVE | Hồi sinh |

## 🔧 Configuration

File: `src/main/resources/application.properties`

```properties
# Server
server.port=8080

# Database
spring.datasource.url=jdbc:h2:mem:gamedb
spring.jpa.hibernate.ddl-auto=create-drop

# Logging
logging.level.org.example=DEBUG
```

## 📝 License

MIT License

