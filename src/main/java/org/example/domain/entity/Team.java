package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho team chiến đấu.
 * Mỗi team có tối đa 5 hero, được đặt trên grid 3x3.
 */
@Entity
@Table(name = "teams")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"player", "slots"})
public class Team {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TeamSlot> slots = new ArrayList<>();

    /**
     * Số lượng hero trong team.
     */
    public int getHeroCount() {
        return slots.size();
    }

    /**
     * Kiểm tra team đã đầy chưa (max 5 heroes).
     */
    public boolean isFull() {
        return slots.size() >= 5;
    }

    /**
     * Thêm hero vào team tại vị trí xác định.
     */
    public boolean addHero(Hero hero, int row, int col) {
        if (isFull()) {
            return false;
        }
        if (row < 0 || row > 2 || col < 0 || col > 2) {
            return false;
        }
        // Kiểm tra vị trí đã có hero chưa
        for (TeamSlot slot : slots) {
            if (slot.getPositionRow() == row && slot.getPositionCol() == col) {
                return false;
            }
        }
        
        TeamSlot newSlot = TeamSlot.builder()
                .team(this)
                .hero(hero)
                .positionRow(row)
                .positionCol(col)
                .build();
        slots.add(newSlot);
        return true;
    }

    /**
     * Xóa hero khỏi team.
     */
    public boolean removeHero(Hero hero) {
        return slots.removeIf(slot -> slot.getHero().equals(hero));
    }

    /**
     * Lấy danh sách heroes trong team.
     */
    public List<Hero> getHeroes() {
        return slots.stream()
                .map(TeamSlot::getHero)
                .toList();
    }

    /**
     * Lấy hero tại vị trí xác định.
     */
    public Hero getHeroAt(int row, int col) {
        return slots.stream()
                .filter(slot -> slot.getPositionRow() == row && slot.getPositionCol() == col)
                .map(TeamSlot::getHero)
                .findFirst()
                .orElse(null);
    }

    /**
     * Lấy heroes ở hàng trước (row = 0).
     */
    public List<Hero> getFrontRow() {
        return slots.stream()
                .filter(slot -> slot.getPositionRow() == 0)
                .map(TeamSlot::getHero)
                .toList();
    }

    /**
     * Lấy heroes ở hàng giữa (row = 1).
     */
    public List<Hero> getMiddleRow() {
        return slots.stream()
                .filter(slot -> slot.getPositionRow() == 1)
                .map(TeamSlot::getHero)
                .toList();
    }

    /**
     * Lấy heroes ở hàng sau (row = 2).
     */
    public List<Hero> getBackRow() {
        return slots.stream()
                .filter(slot -> slot.getPositionRow() == 2)
                .map(TeamSlot::getHero)
                .toList();
    }
}

