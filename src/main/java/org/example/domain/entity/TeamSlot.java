package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity đại diện cho slot trong team (hero + position).
 */
@Entity
@Table(name = "team_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"team", "hero"})
public class TeamSlot {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_id", nullable = false)
    private Hero hero;

    /**
     * Vị trí hàng trong grid 3x3 (0 = front, 1 = middle, 2 = back).
     */
    @Column(name = "position_row", nullable = false)
    private int positionRow;

    /**
     * Vị trí cột trong grid 3x3 (0 = left, 1 = center, 2 = right).
     */
    @Column(name = "position_col", nullable = false)
    private int positionCol;
}

