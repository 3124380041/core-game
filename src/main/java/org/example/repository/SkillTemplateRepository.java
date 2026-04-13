package org.example.repository;

import org.example.domain.entity.SkillTemplate;
import org.example.domain.enums.SkillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillTemplateRepository extends JpaRepository<SkillTemplate, Long> {
    Optional<SkillTemplate> findByName(String name);
    List<SkillTemplate> findBySkillType(SkillType skillType);
}

