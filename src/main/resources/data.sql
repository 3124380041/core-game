-- =====================================================
-- TURN-BASED BATTLE GAME - DATA INITIALIZATION
-- =====================================================

-- =====================================================
-- EQUIPMENT SETS
-- =====================================================
INSERT INTO equipment_sets (id, name, description, bonus2_hp, bonus2_attack, bonus2_defense, bonus2_speed, bonus2_crit_rate, bonus4_hp, bonus4_attack, bonus4_defense, bonus4_speed, bonus4_crit_rate, bonus4_crit_damage, bonus6_hp, bonus6_attack, bonus6_defense, bonus6_speed, bonus6_crit_rate, bonus6_crit_damage, bonus6_special_effect) VALUES
(1, 'Chiến Binh Set', 'Bộ trang bị dành cho chiến binh', 0, 50, 0, 0, 0, 0, 100, 0, 0, 0.1, 0, 0, 200, 0, 0, 0, 0.3, NULL),
(2, 'Pháp Sư Set', 'Bộ trang bị dành cho pháp sư', 100, 0, 0, 0, 0, 200, 0, 0, 20, 0, 0, 400, 0, 0, 40, 0, 0, NULL),
(3, 'Thủ Vệ Set', 'Bộ trang bị dành cho tank', 0, 0, 30, 0, 0, 300, 0, 60, 0, 0, 0, 600, 0, 120, 0, 0, 0, NULL);

-- =====================================================
-- ITEM TEMPLATES
-- =====================================================
INSERT INTO item_templates (id, name, description, slot, rarity, equipment_set_id, bonus_hp, bonus_attack, bonus_defense, bonus_intelligence, bonus_speed, bonus_crit_rate, bonus_crit_damage, bonus_dodge_rate, bonus_block_rate) VALUES
-- Weapons
(1, 'Kiếm Thép', 'Kiếm cơ bản cho chiến binh', 'WEAPON_1', 3, 1, 0, 50, 0, 0, 10, 0.05, 0, 0, 0),
(2, 'Kiếm Lửa', 'Kiếm mạnh với sức mạnh lửa', 'WEAPON_1', 4, 1, 0, 80, 0, 0, 15, 0.08, 0.1, 0, 0),
(3, 'Gậy Phép', 'Gậy cơ bản cho pháp sư', 'WEAPON_1', 3, 2, 50, 20, 0, 60, 5, 0, 0, 0, 0),
(4, 'Gậy Băng', 'Gậy mạnh với sức mạnh băng', 'WEAPON_1', 4, 2, 80, 30, 0, 90, 10, 0.05, 0, 0, 0),
(5, 'Khiên Sắt', 'Khiên cơ bản', 'WEAPON_2', 3, 3, 100, 0, 40, 0, 0, 0, 0, 0, 0),
(6, 'Khiên Rồng', 'Khiên huyền thoại', 'WEAPON_2', 5, 3, 200, 20, 80, 0, 5, 0, 0, 0, 0),
-- Armor
(7, 'Giáp Chiến Binh', 'Giáp cho chiến binh', 'ARMOR', 4, 1, 150, 30, 30, 0, 5, 0.03, 0, 0, 0),
(8, 'Áo Choàng Pháp Sư', 'Áo choàng cho pháp sư', 'ARMOR', 4, 2, 100, 10, 20, 50, 10, 0, 0, 0, 0),
(9, 'Giáp Thủ Vệ', 'Giáp nặng cho tank', 'ARMOR', 5, 3, 300, 0, 60, 0, 0, 0, 0, 0, 0),
-- Rings
(10, 'Nhẫn Sức Mạnh', 'Nhẫn tăng sức mạnh', 'RING_1', 3, 1, 0, 25, 0, 0, 5, 0.02, 0.05, 0, 0),
(11, 'Nhẫn Trí Tuệ', 'Nhẫn tăng trí tuệ', 'RING_1', 3, 2, 30, 0, 0, 30, 8, 0, 0, 0, 0),
(12, 'Nhẫn Bảo Vệ', 'Nhẫn tăng phòng thủ', 'RING_1', 3, 3, 80, 0, 20, 0, 0, 0, 0, 0, 0),
-- Boots
(13, 'Giày Tốc Độ', 'Giày tăng tốc độ', 'BOOT_1', 3, NULL, 0, 0, 10, 0, 25, 0.02, 0, 0, 0),
(14, 'Giày Chiến Đấu', 'Giày cho chiến binh', 'BOOT_1', 4, 1, 50, 20, 15, 0, 20, 0.03, 0, 0, 0);

-- =====================================================
-- SKILL TEMPLATES
-- =====================================================
INSERT INTO skill_templates (id, name, description, skill_type, target_type, damage_type, scaling, scaling_stat, cooldown, mp_cost) VALUES
(1, 'Chém Mạnh', 'Tung một nhát chém mạnh gây sát thương vật lý', 'ACTIVE', 'SINGLE', 'PHYSICAL', 3.0, 'ATTACK', 2, 0),
(2, 'Cầu Lửa', 'Phóng quả cầu lửa gây sát thương phép thuật diện rộng', 'ACTIVE', 'AOE', 'MAGIC', 2.0, 'INTELLIGENCE', 3, 0),
(3, 'Sấm Sét', 'Triệu hồi sấm sét đánh vào kẻ địch', 'ACTIVE', 'SINGLE', 'MAGIC', 4.0, 'INTELLIGENCE', 4, 50),
(4, 'Độc Kích', 'Đâm gây sát thương và nhiễm độc', 'ACTIVE', 'SINGLE', 'PHYSICAL', 2.0, 'ATTACK', 2, 0),
(5, 'Hồi Xuân Thuật', 'Hồi phục máu cho đồng minh', 'ACTIVE', 'SINGLE', 'MAGIC', 2.5, 'INTELLIGENCE', 2, 0),
(6, 'Hồi Máu Nhóm', 'Hồi phục máu cho toàn đội', 'ACTIVE', 'ALL_ALLIES', 'MAGIC', 1.5, 'INTELLIGENCE', 4, 100),
(7, 'Tăng Lực', 'Tăng sức tấn công cho bản thân', 'ACTIVE', 'SELF', 'MAGIC', 0, 'ATTACK', 3, 0),
(8, 'Khiên Bảo Vệ', 'Tăng phòng thủ cho bản thân', 'ACTIVE', 'SELF', 'MAGIC', 0, 'ATTACK', 3, 0);

-- =====================================================
-- EFFECT TEMPLATES
-- =====================================================
INSERT INTO effect_templates (id, name, effect_type, effect_trigger, effect_value, is_percentage, target_stat, duration, chance, skill_template_id) VALUES
-- Chém Mạnh effects
(1, 'Sát thương Chém', 'DAMAGE', 'IMMEDIATE', 0, false, NULL, 0, 1.0, 1),
-- Cầu Lửa effects
(2, 'Sát thương Lửa', 'DAMAGE', 'IMMEDIATE', 0, false, NULL, 0, 1.0, 2),
(3, 'Bỏng', 'BURN', 'TURN_START', 10, false, NULL, 2, 0.3, 2),
-- Sấm Sét effects
(4, 'Sát thương Sấm', 'DAMAGE', 'IMMEDIATE', 0, false, NULL, 0, 1.0, 3),
(5, 'Choáng', 'STUN', 'IMMEDIATE', 0, false, NULL, 1, 0.2, 3),
-- Độc Kích effects
(6, 'Sát thương Độc', 'DAMAGE', 'IMMEDIATE', 0, false, NULL, 0, 1.0, 4),
(7, 'Trúng Độc', 'POISON', 'TURN_START', 15, false, NULL, 3, 0.5, 4),
-- Hồi Xuân Thuật effects
(8, 'Hồi máu', 'HEAL', 'IMMEDIATE', 30, true, NULL, 0, 1.0, 5),
-- Hồi Máu Nhóm effects
(9, 'Hồi máu nhóm', 'HEAL', 'IMMEDIATE', 20, true, NULL, 0, 1.0, 6),
-- Tăng Lực effects
(10, 'Tăng Sức Mạnh', 'BUFF', 'IMMEDIATE', 30, true, 'ATTACK', 3, 1.0, 7),
-- Khiên Bảo Vệ effects
(11, 'Tăng Phòng Thủ', 'BUFF', 'IMMEDIATE', 50, true, 'DEFENSE', 3, 1.0, 8);

-- =====================================================
-- PLAYERS
-- =====================================================
INSERT INTO players (id, username, name, password_hash, level, experience, active_team_id) VALUES
(1, 'player1', 'Người Chơi 1', 'player1', 10, 0, NULL),
(2, 'player2', 'Người Chơi 2', 'player2', 10, 0, NULL);

-- =====================================================
-- HEROES - PLAYER 1 (10 heroes)
-- =====================================================
INSERT INTO heroes (id, name, type, level, stars, base_hp, base_attack, base_defense, base_intelligence, base_speed, crit_rate, crit_damage, dodge_rate, block_rate, counter_rate, stun_resist, crit_resist, owner_id) VALUES
-- Player 1 heroes
(1, 'Chiến Binh', 'ATTACK_PHYS', 30, 5, 500, 80, 40, 20, 50, 0.20, 1.5, 0.10, 0.05, 0, 0, 0, 1),
(2, 'Pháp Sư', 'ATTACK_MAGIC', 30, 4, 300, 30, 20, 90, 60, 0.18, 1.5, 0.05, 0.05, 0, 0, 0, 1),
(3, 'Hiệp Sĩ', 'TANK', 30, 5, 800, 50, 80, 30, 30, 0.20, 1.5, 0.05, 0.20, 0, 0, 0, 1),
(4, 'Thầy Thuốc', 'SUPPORT', 30, 4, 350, 25, 30, 70, 55, 0.18, 1.5, 0.05, 0.05, 0, 0, 0, 1),
(5, 'Sát Thủ', 'ATTACK_PHYS', 30, 4, 350, 100, 25, 25, 90, 0.18, 1.5, 0.10, 0.05, 0, 0, 0, 1),
(6, 'Cung Thủ Lửa', 'ATTACK_PHYS', 30, 3, 320, 75, 25, 30, 70, 0.16, 1.5, 0.10, 0.05, 0, 0, 0, 1),
(7, 'Pháp Sư Băng', 'ATTACK_MAGIC', 30, 4, 280, 25, 18, 85, 65, 0.18, 1.5, 0.05, 0.05, 0, 0, 0, 1),
(8, 'Thủ Hộ', 'TANK', 30, 4, 700, 45, 70, 25, 25, 0.18, 1.5, 0.05, 0.20, 0, 0, 0, 1),
(9, 'Tu Sĩ', 'SUPPORT', 30, 3, 320, 20, 25, 60, 50, 0.16, 1.5, 0.05, 0.05, 0, 0, 0, 1),
(10, 'Kiếm Khách', 'ATTACK_PHYS', 30, 5, 400, 95, 35, 20, 85, 0.20, 1.5, 0.10, 0.05, 0, 0, 0, 1),
-- Player 2 heroes
(11, 'Kỵ Sĩ', 'TANK', 30, 5, 750, 55, 75, 25, 35, 0.20, 1.5, 0.05, 0.20, 0, 0, 0, 2),
(12, 'Cung Thủ', 'ATTACK_PHYS', 30, 4, 320, 85, 30, 35, 80, 0.18, 1.5, 0.10, 0.05, 0, 0, 0, 2),
(13, 'Phù Thủy', 'ATTACK_MAGIC', 30, 5, 280, 25, 25, 95, 55, 0.20, 1.5, 0.05, 0.05, 0, 0, 0, 2),
(14, 'Linh Mục', 'SUPPORT', 30, 4, 380, 20, 35, 65, 50, 0.18, 1.5, 0.05, 0.05, 0, 0, 0, 2),
(15, 'Ninja', 'ATTACK_PHYS', 30, 4, 300, 95, 20, 30, 95, 0.18, 1.5, 0.10, 0.05, 0, 0, 0, 2),
(16, 'Đấu Sĩ', 'ATTACK_PHYS', 30, 4, 450, 70, 45, 25, 55, 0.18, 1.5, 0.10, 0.05, 0, 0, 0, 2),
(17, 'Thần Bí', 'ATTACK_MAGIC', 30, 3, 260, 20, 20, 80, 60, 0.16, 1.5, 0.05, 0.05, 0, 0, 0, 2),
(18, 'Vệ Binh', 'TANK', 30, 3, 650, 40, 65, 20, 30, 0.16, 1.5, 0.05, 0.20, 0, 0, 0, 2),
(19, 'Đạo Sĩ', 'SUPPORT', 30, 4, 340, 25, 30, 70, 55, 0.18, 1.5, 0.05, 0.05, 0, 0, 0, 2),
(20, 'Sát Thủ Bóng Đêm', 'ATTACK_PHYS', 30, 5, 320, 105, 22, 25, 100, 0.20, 1.5, 0.10, 0.05, 0, 0, 0, 2);

-- =====================================================
-- HERO SKILLS
-- =====================================================
INSERT INTO hero_skills (id, hero_id, skill_template_id, skill_level, slot_index) VALUES
-- Player 1 hero skills
(1, 1, 1, 3, 0),   -- Chiến Binh: Chém Mạnh
(2, 1, 7, 3, 1),   -- Chiến Binh: Tăng Lực
(3, 2, 2, 3, 0),   -- Pháp Sư: Cầu Lửa
(4, 2, 3, 3, 1),   -- Pháp Sư: Sấm Sét
(5, 3, 8, 3, 0),   -- Hiệp Sĩ: Khiên Bảo Vệ
(6, 4, 5, 3, 0),   -- Thầy Thuốc: Hồi Xuân Thuật
(7, 4, 6, 3, 1),   -- Thầy Thuốc: Hồi Máu Nhóm
(8, 5, 4, 3, 0),   -- Sát Thủ: Độc Kích
(9, 5, 1, 3, 1),   -- Sát Thủ: Chém Mạnh
(10, 6, 4, 3, 0),  -- Cung Thủ Lửa: Độc Kích
(11, 7, 3, 3, 0),  -- Pháp Sư Băng: Sấm Sét
(12, 7, 2, 3, 1),  -- Pháp Sư Băng: Cầu Lửa
(13, 8, 8, 3, 0),  -- Thủ Hộ: Khiên Bảo Vệ
(14, 8, 7, 3, 1),  -- Thủ Hộ: Tăng Lực
(15, 9, 5, 3, 0),  -- Tu Sĩ: Hồi Xuân Thuật
(16, 10, 1, 3, 0), -- Kiếm Khách: Chém Mạnh
(17, 10, 4, 3, 1), -- Kiếm Khách: Độc Kích
(18, 10, 7, 3, 2), -- Kiếm Khách: Tăng Lực
-- Player 2 hero skills
(19, 11, 8, 3, 0), -- Kỵ Sĩ: Khiên Bảo Vệ
(20, 11, 7, 3, 1), -- Kỵ Sĩ: Tăng Lực
(21, 12, 4, 3, 0), -- Cung Thủ: Độc Kích
(22, 12, 1, 3, 1), -- Cung Thủ: Chém Mạnh
(23, 13, 3, 3, 0), -- Phù Thủy: Sấm Sét
(24, 13, 2, 3, 1), -- Phù Thủy: Cầu Lửa
(25, 14, 5, 3, 0), -- Linh Mục: Hồi Xuân Thuật
(26, 14, 6, 3, 1), -- Linh Mục: Hồi Máu Nhóm
(27, 15, 1, 3, 0), -- Ninja: Chém Mạnh
(28, 15, 7, 3, 1), -- Ninja: Tăng Lực
(29, 16, 1, 3, 0), -- Đấu Sĩ: Chém Mạnh
(30, 16, 8, 3, 1), -- Đấu Sĩ: Khiên Bảo Vệ
(31, 17, 2, 3, 0), -- Thần Bí: Cầu Lửa
(32, 18, 8, 3, 0), -- Vệ Binh: Khiên Bảo Vệ
(33, 19, 5, 3, 0), -- Đạo Sĩ: Hồi Xuân Thuật
(34, 20, 4, 3, 0), -- Sát Thủ Bóng Đêm: Độc Kích
(35, 20, 1, 3, 1), -- Sát Thủ Bóng Đêm: Chém Mạnh
(36, 20, 7, 3, 2); -- Sát Thủ Bóng Đêm: Tăng Lực

-- =====================================================
-- TEAMS
-- =====================================================
INSERT INTO teams (id, name, player_id) VALUES
(1, 'Đội Alpha', 1),
(2, 'Đội Beta', 2);

-- =====================================================
-- TEAM SLOTS (5 heroes per team)
-- =====================================================
INSERT INTO team_slots (id, team_id, hero_id, position_row, position_col) VALUES
-- Team 1 (Player 1)
(1, 1, 3, 0, 1),  -- Hiệp Sĩ - Front center
(2, 1, 1, 0, 0),  -- Chiến Binh - Front left
(3, 1, 5, 1, 2),  -- Sát Thủ - Middle right
(4, 1, 2, 2, 0),  -- Pháp Sư - Back left
(5, 1, 4, 2, 2),  -- Thầy Thuốc - Back right
-- Team 2 (Player 2)
(6, 2, 11, 0, 1), -- Kỵ Sĩ - Front center
(7, 2, 12, 1, 0), -- Cung Thủ - Middle left
(8, 2, 15, 1, 2), -- Ninja - Middle right
(9, 2, 13, 2, 1), -- Phù Thủy - Back center
(10, 2, 14, 2, 2); -- Linh Mục - Back right

-- =====================================================
-- UPDATE PLAYER ACTIVE TEAMS
-- =====================================================
UPDATE players SET active_team_id = 1 WHERE id = 1;
UPDATE players SET active_team_id = 2 WHERE id = 2;

