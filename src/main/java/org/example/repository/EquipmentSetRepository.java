package org.example.repository;

import org.example.domain.entity.EquipmentSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EquipmentSetRepository extends JpaRepository<EquipmentSet, Long> {
    Optional<EquipmentSet> findByName(String name);
}

