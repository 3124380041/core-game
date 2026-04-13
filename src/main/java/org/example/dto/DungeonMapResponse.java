package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.domain.entity.DungeonMap;

@Data
@Builder
public class DungeonMapResponse {
    private Long id;
    private int mapIndex;
    private String name;
    private String enemyTeamName;
    private int enemyCount;
    private int firstClearGoldReward;

    public static DungeonMapResponse from(DungeonMap map) {
        int enemyCount = map.getEnemyTeam() != null && map.getEnemyTeam().getSlots() != null
                ? map.getEnemyTeam().getSlots().size()
                : 0;

        return DungeonMapResponse.builder()
                .id(map.getId())
                .mapIndex(map.getMapIndex())
                .name(map.getName())
                .enemyTeamName(map.getEnemyTeam() != null ? map.getEnemyTeam().getName() : null)
                .enemyCount(enemyCount)
                .firstClearGoldReward(map.getFirstClearGoldReward())
                .build();
    }
}

