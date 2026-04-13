package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.domain.entity.Team;

import java.util.List;

/**
 * Summary của Team.
 */
@Data
@Builder
public class TeamSummary {
    private Long id;
    private String name;
    private int heroCount;
    private List<TeamSlotInfo> slots;

    @Data
    @Builder
    public static class TeamSlotInfo {
        private Long heroId;
        private String heroName;
        private int row;
        private int col;
    }

    public static TeamSummary from(Team team) {
        return TeamSummary.builder()
                .id(team.getId())
                .name(team.getName())
                .heroCount(team.getHeroCount())
                .slots(team.getSlots().stream()
                        .map(slot -> TeamSlotInfo.builder()
                                .heroId(slot.getHero().getId())
                                .heroName(slot.getHero().getName())
                                .row(slot.getPositionRow())
                                .col(slot.getPositionCol())
                                .build())
                        .toList())
                .build();
    }
}

