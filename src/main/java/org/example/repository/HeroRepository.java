package org.example.repository;

import org.example.domain.entity.Hero;
import org.example.domain.enums.HeroType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeroRepository extends JpaRepository<Hero, Long> {
    List<Hero> findByOwnerId(Long ownerId);
    List<Hero> findByType(HeroType type);
    List<Hero> findByOwnerIdAndType(Long ownerId, HeroType type);
}

