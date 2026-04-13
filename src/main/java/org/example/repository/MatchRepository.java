package org.example.repository;

import org.example.domain.entity.Match;
import org.example.domain.enums.MatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByPlayer1IdOrPlayer2Id(Long player1Id, Long player2Id);
    List<Match> findByStatus(MatchStatus status);
    List<Match> findByPlayer1IdAndStatus(Long playerId, MatchStatus status);
    List<Match> findByPlayer2IdAndStatus(Long playerId, MatchStatus status);
}

