package org.example.repository;

import org.example.domain.entity.CombatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CombatLogRepository extends JpaRepository<CombatLog, Long> {
    List<CombatLog> findByMatchIdOrderByTimestampAsc(Long matchId);
    List<CombatLog> findByMatchIdAndRoundNumber(Long matchId, int roundNumber);
}

