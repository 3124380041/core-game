package org.example.repository;

import org.example.domain.entity.DungeonRun;
import org.example.domain.enums.DungeonRunStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DungeonRunRepository extends JpaRepository<DungeonRun, Long> {
    List<DungeonRun> findByPlayerIdOrderByStartedAtDesc(Long playerId);
    List<DungeonRun> findByPlayerIdAndStatus(Long playerId, DungeonRunStatus status);
}

