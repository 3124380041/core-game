package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.enums.DungeonRunStatus;

import java.time.LocalDateTime;

/**
 * Luu tien trinh vuot pho ban cua nguoi choi.
 */
@Entity
@Table(name = "dungeon_runs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"player", "dungeon"})
public class DungeonRun {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dungeon_id", nullable = false)
    private Dungeon dungeon;

    // Bat dau tu 1.
    @Builder.Default
    @Column(name = "current_map_index", nullable = false)
    private int currentMapIndex = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private DungeonRunStatus status = DungeonRunStatus.IN_PROGRESS;

    @Column(name = "active_match_id")
    private Long activeMatchId;

    @Builder.Default
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    @Column(name = "ended_at")
    private LocalDateTime endedAt;
}

