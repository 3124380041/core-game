package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.Dungeon;
import org.example.domain.entity.DungeonMap;
import org.example.domain.entity.DungeonRun;
import org.example.domain.entity.Match;
import org.example.domain.enums.DungeonRunStatus;
import org.example.domain.enums.MatchStatus;
import org.example.dto.DungeonMapResponse;
import org.example.dto.DungeonResponse;
import org.example.dto.DungeonRunResponse;
import org.example.dto.MatchResponse;
import org.example.exception.GameException;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.DungeonMapRepository;
import org.example.repository.DungeonRepository;
import org.example.repository.DungeonRunRepository;
import org.example.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DungeonService {

    private final DungeonRepository dungeonRepository;
    private final DungeonMapRepository dungeonMapRepository;
    private final DungeonRunRepository dungeonRunRepository;
    private final PlayerRepository playerRepository;
    private final MatchService matchService;

    @Transactional(readOnly = true)
    public List<DungeonResponse> getDungeons() {
        return dungeonRepository.findAll().stream()
                .map(DungeonResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DungeonMapResponse> getDungeonMaps(Long dungeonId) {
        return dungeonMapRepository.findByDungeonIdOrderByMapIndexAsc(dungeonId).stream()
                .map(DungeonMapResponse::from)
                .toList();
    }

    public DungeonRunResponse startRun(Long playerId, Long dungeonId) {
        var player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player không tồn tại: " + playerId));

        if (player.getActiveTeam() == null || player.getActiveTeam().getSlots().isEmpty()) {
            throw new GameException("Cần set active team trước khi vào phó bản");
        }

        Dungeon dungeon = dungeonRepository.findById(dungeonId)
                .orElseThrow(() -> new ResourceNotFoundException("Phó bản không tồn tại: " + dungeonId));

        if (dungeon.getMaps() == null || dungeon.getMaps().isEmpty()) {
            throw new GameException("Phó bản chưa có map");
        }

        DungeonRun run = DungeonRun.builder()
                .player(player)
                .dungeon(dungeon)
                .currentMapIndex(1)
                .status(DungeonRunStatus.IN_PROGRESS)
                .build();

        return DungeonRunResponse.from(dungeonRunRepository.save(run));
    }

    public Match startCurrentMapBattle(Long runId, Long playerId) {
        DungeonRun run = getOwnedRun(runId, playerId);

        if (run.getStatus() != DungeonRunStatus.IN_PROGRESS) {
            throw new GameException("Run đã kết thúc, không thể bắt đầu trận mới");
        }

        DungeonMap map = dungeonMapRepository
                .findByDungeonIdAndMapIndex(run.getDungeon().getId(), run.getCurrentMapIndex())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy map hiện tại"));

        Match match = matchService.createDungeonMatch(playerId, map.getEnemyTeam(), map.getId());
        run.setActiveMatchId(match.getId());
        dungeonRunRepository.save(run);

        return match;
    }

    public DungeonRunResponse resolveRun(Long runId, Long playerId) {
        DungeonRun run = getOwnedRun(runId, playerId);

        if (run.getActiveMatchId() == null) {
            throw new GameException("Run chưa có trận đấu để resolve");
        }

        Match match = matchService.getMatch(run.getActiveMatchId());
        if (match.getStatus() != MatchStatus.COMPLETED) {
            throw new GameException("Trận đấu chưa kết thúc");
        }

        boolean playerWon = match.getWinner() != null && match.getWinner().getId().equals(playerId);

        if (!playerWon) {
            run.setStatus(DungeonRunStatus.FAILED);
            run.setEndedAt(LocalDateTime.now());
            return DungeonRunResponse.from(dungeonRunRepository.save(run));
        }

        int totalMaps = run.getDungeon().getMaps() != null ? run.getDungeon().getMaps().size() : 0;
        if (run.getCurrentMapIndex() >= totalMaps) {
            run.setStatus(DungeonRunStatus.CLEARED);
            run.setEndedAt(LocalDateTime.now());
        } else {
            run.setCurrentMapIndex(run.getCurrentMapIndex() + 1);
        }

        return DungeonRunResponse.from(dungeonRunRepository.save(run));
    }

    @Transactional(readOnly = true)
    public DungeonRunResponse getRun(Long runId, Long playerId) {
        return DungeonRunResponse.from(getOwnedRun(runId, playerId));
    }

    private DungeonRun getOwnedRun(Long runId, Long playerId) {
        DungeonRun run = dungeonRunRepository.findById(runId)
                .orElseThrow(() -> new ResourceNotFoundException("Run không tồn tại: " + runId));

        if (!run.getPlayer().getId().equals(playerId)) {
            throw new GameException("Run không thuộc player này");
        }
        return run;
    }

    /**
     * Start run moi va vao tran dau map dau tien ngay lap tuc.
     */
    public DungeonBattleStartResult startRunAndBattle(Long playerId, Long dungeonId) {
        DungeonRunResponse run = startRun(playerId, dungeonId);
        MatchResponse match = startCurrentMapBattleResponse(run.getRunId(), playerId);
        return new DungeonBattleStartResult(run, match);
    }

    public MatchResponse startCurrentMapBattleResponse(Long runId, Long playerId) {
        Match match = startCurrentMapBattle(runId, playerId);
        return MatchResponse.fromRuntime(matchService.getMatchState(match.getId()));
    }

    public record DungeonBattleStartResult(DungeonRunResponse run, MatchResponse match) {}
}
