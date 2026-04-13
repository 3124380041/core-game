package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.domain.enums.MatchMode;
import org.example.domain.enums.MatchStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity đại diện cho một trận đấu.
 */
@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"player1", "player2", "team1", "team2", "winner", "combatLogs"})
public class Match {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player1_id", nullable = false)
    private Player player1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id", nullable = false)
    private Player player2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team1_id")
    private Team team1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team2_id")
    private Team team2;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private MatchStatus status = MatchStatus.PENDING;

    @Column(name = "current_round", nullable = false)
    @Builder.Default
    private int currentRound = 1;

    @Column(name = "current_turn_index", nullable = false)
    @Builder.Default
    private int currentTurnIndex = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_id")
    private Player winner;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CombatLog> combatLogs = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false)
    @Builder.Default
    private MatchMode mode = MatchMode.PVP;

    // Nguon tao tran dau, dung cho dung map pho ban.
    @Column(name = "source_type", length = 32)
    private String sourceType;

    @Column(name = "source_id")
    private Long sourceId;

    /**
     * Chuyển sang round tiếp theo.
     */
    public void nextRound() {
        this.currentRound++;
        this.currentTurnIndex = 0;
    }

    /**
     * Chuyển sang turn tiếp theo.
     */
    public void nextTurn() {
        this.currentTurnIndex++;
    }

    /**
     * Kết thúc trận đấu.
     */
    public void endMatch(Player winner) {
        this.status = MatchStatus.COMPLETED;
        this.winner = winner;
        this.endedAt = LocalDateTime.now();
    }

    /**
     * Thêm log vào trận đấu.
     */
    public void addLog(CombatLog log) {
        combatLogs.add(log);
        log.setMatch(this);
    }
}
