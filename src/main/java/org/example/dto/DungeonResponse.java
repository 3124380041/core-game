package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.domain.entity.Dungeon;

@Data
@Builder
public class DungeonResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private int recommendedPower;
    private int mapCount;

    public static DungeonResponse from(Dungeon dungeon) {
        return DungeonResponse.builder()
                .id(dungeon.getId())
                .code(dungeon.getCode())
                .name(dungeon.getName())
                .description(dungeon.getDescription())
                .recommendedPower(dungeon.getRecommendedPower())
                .mapCount(dungeon.getMaps() != null ? dungeon.getMaps().size() : 0)
                .build();
    }
}

