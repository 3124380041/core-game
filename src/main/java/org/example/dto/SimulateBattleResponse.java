package org.example.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Response cho simulate battle với turn history.
 */
@Data
@Builder
public class SimulateBattleResponse {
    private MatchResponse matchResult;
    private List<TurnResultResponse> turnHistory;
}

