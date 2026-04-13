package org.example.repository;

import org.example.domain.entity.Dungeon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DungeonRepository extends JpaRepository<Dungeon, Long> {
    Optional<Dungeon> findByCode(String code);
}

