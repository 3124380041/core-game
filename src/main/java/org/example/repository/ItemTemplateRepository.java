package org.example.repository;

import org.example.domain.entity.ItemTemplate;
import org.example.domain.enums.EquipmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemTemplateRepository extends JpaRepository<ItemTemplate, Long> {
    List<ItemTemplate> findBySlot(EquipmentSlot slot);
    List<ItemTemplate> findByEquipmentSetId(Long setId);
    List<ItemTemplate> findByRarity(int rarity);
}

