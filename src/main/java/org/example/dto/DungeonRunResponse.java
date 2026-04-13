package org.example.dto;

import lombok.Builder;
import lombok.Data;
import org.example.domain.entity.DungeonRun;

@Data
@Builder
public class DungeonRunResponse {
    private Long runId;
    private Long playerId;
    private Long dungeonId;
    private String dungeonName;
    private int currentMapIndex;
    private int totalMaps;
    private String status;
    private Long activeMatchId;

    public static DungeonRunResponse from(DungeonRun run) {
        int totalMaps = run.getDungeon() != null && run.getDungeon().getMaps() != null
                ? run.getDungeon().getMaps().size()
                : 0;

        return DungeonRunResponse.builder()
                .runId(run.getId())
                .playerId(run.getPlayer().getId())
                .dungeonId(run.getDungeon().getId())
                .dungeonName(run.getDungeon().getName())
                .currentMapIndex(run.getCurrentMapIndex())
                .totalMaps(totalMaps)
                .status(run.getStatus().name())
                .activeMatchId(run.getActiveMatchId())
                .build();
    }
}

