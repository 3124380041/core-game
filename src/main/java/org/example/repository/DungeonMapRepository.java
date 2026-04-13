package org.example.repository;

import org.example.domain.entity.DungeonMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DungeonMapRepository extends JpaRepository<DungeonMap, Long> {
    List<DungeonMap> findByDungeonIdOrderByMapIndexAsc(Long dungeonId);
    Optional<DungeonMap> findByDungeonIdAndMapIndex(Long dungeonId, int mapIndex);
}

