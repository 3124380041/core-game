package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Mot map trong pho ban, moi map co 1 doi enemy co dinh.
 */
@Entity
@Table(name = "dungeon_maps")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"dungeon", "enemyTeam"})
public class DungeonMap {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dungeon_id", nullable = false)
    private Dungeon dungeon;

    @Column(name = "map_index", nullable = false)
    private int mapIndex;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enemy_team_id", nullable = false)
    private Team enemyTeam;

    @Column(name = "first_clear_gold_reward", nullable = false)
    @Builder.Default
    private int firstClearGoldReward = 0;
}

