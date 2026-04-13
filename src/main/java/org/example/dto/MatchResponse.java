package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.domain.entity.Match;
import org.example.domain.enums.MatchStatus;
import org.example.domain.runtime.HeroRuntimeState;
import org.example.domain.runtime.MatchRuntimeState;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response cho Match state.
 */
@Data
@Builder
public class MatchResponse {
    private Long id;
    private MatchStatus status;
    private int currentRound;
    private int currentTurnIndex;

    private PlayerInfo player1;
    private PlayerInfo player2;

    private List<HeroStateInfo> team1;
    private List<HeroStateInfo> team2;

    private HeroStateInfo currentTurnHero;
    private Long winnerId;
    private String winnerName;

    private LocalDateTime createdAt;
    private LocalDateTime endedAt;

    @Data
    @Builder
    public static class PlayerInfo {
        private Long id;
        private String name;
    }

    @Data
    @Builder
    public static class HeroStateInfo {
        private Long heroId;
        private String name;
        private String type;
        private int currentHp;
        private int maxHp;
        private int currentMp;
        private boolean alive;
        private int positionRow;
        private int positionCol;
        private List<String> activeEffects;
    }

    public static MatchResponse from(Match match) {
        MatchResponseBuilder builder = MatchResponse.builder()
                .id(match.getId())
                .status(match.getStatus())
                .currentRound(match.getCurrentRound())
                .currentTurnIndex(match.getCurrentTurnIndex())
                .createdAt(match.getCreatedAt())
                .endedAt(match.getEndedAt());

        if (match.getPlayer1() != null) {
            builder.player1(PlayerInfo.builder()
                    .id(match.getPlayer1().getId())
                    .name(match.getPlayer1().getName())
                    .build());
        }

        if (match.getPlayer2() != null) {
            builder.player2(PlayerInfo.builder()
                    .id(match.getPlayer2().getId())
                    .name(match.getPlayer2().getName())
                    .build());
        }

        if (match.getWinner() != null) {
            builder.winnerId(match.getWinner().getId())
                    .winnerName(match.getWinner().getName());
        }

        return builder.build();
    }

    public static MatchResponse fromRuntime(MatchRuntimeState state) {
        MatchResponseBuilder builder = MatchResponse.builder()
                .id(state.getMatchId())
                .status(state.getStatus())
                .currentRound(state.getCurrentRound())
                .currentTurnIndex(state.getCurrentTurnIndex());

        Match match = state.getMatch();
        if (match != null) {
            if (match.getPlayer1() != null) {
                builder.player1(PlayerInfo.builder()
                        .id(match.getPlayer1().getId())
                        .name(match.getPlayer1().getName())
                        .build());
            }
            if (match.getPlayer2() != null) {
                builder.player2(PlayerInfo.builder()
                        .id(match.getPlayer2().getId())
                        .name(match.getPlayer2().getName())
                        .build());
            }
        }

        builder.team1(state.getTeam1States().stream()
                .map(MatchResponse::heroStateInfo)
                .toList());

        builder.team2(state.getTeam2States().stream()
                .map(MatchResponse::heroStateInfo)
                .toList());

        HeroRuntimeState currentHero = state.getCurrentTurnHero();
        if (currentHero != null) {
            builder.currentTurnHero(heroStateInfo(currentHero));
        }

        if (state.getWinnerTeamIndex() != null) {
            // Determine winner player
            if (match != null) {
                if (state.getWinnerTeamIndex() == 0 && match.getPlayer1() != null) {
                    builder.winnerId(match.getPlayer1().getId())
                            .winnerName(match.getPlayer1().getName());
                } else if (state.getWinnerTeamIndex() == 1 && match.getPlayer2() != null) {
                    builder.winnerId(match.getPlayer2().getId())
                            .winnerName(match.getPlayer2().getName());
                }
            }
        }

        return builder.build();
    }

    private static HeroStateInfo heroStateInfo(HeroRuntimeState state) {
        return HeroStateInfo.builder()
                .heroId(state.getHeroId())
                .name(state.getName())
                .type(state.getHero().getType().name())
                .currentHp(state.getCurrentHp())
                .maxHp(state.getHero().getMaxHp())
                .currentMp(state.getCurrentMp())
                .alive(state.isAlive())
                .positionRow(state.getPositionRow())
                .positionCol(state.getPositionCol())
                .activeEffects(state.getActiveEffects().stream()
                        .map(e -> e.getName())
                        .toList())
                .build();
    }
}

