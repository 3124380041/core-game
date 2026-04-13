package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.domain.entity.Player;

import java.util.List;

/**
 * Response cho Player.
 */
@Data
@Builder
public class PlayerResponse {
    private Long id;
    private String username;
    private String name;
    private int level;
    private int experience;
    private List<HeroSummary> heroes;
    private TeamSummary activeTeam;

    public static PlayerResponse from(Player player) {
        PlayerResponseBuilder builder = PlayerResponse.builder()
                .id(player.getId())
                .username(player.getUsername())
                .name(player.getName())
                .level(player.getLevel())
                .experience(player.getExperience());

        if (player.getHeroes() != null) {
            builder.heroes(player.getHeroes().stream()
                    .map(HeroSummary::from)
                    .toList());
        }

        if (player.getActiveTeam() != null) {
            builder.activeTeam(TeamSummary.from(player.getActiveTeam()));
        }

        return builder.build();
    }
}

