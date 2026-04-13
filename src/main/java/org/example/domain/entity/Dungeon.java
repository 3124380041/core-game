package org.example.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Pho ban gom nhieu map lien tiep.
 */
@Entity
@Table(name = "dungeons")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "maps")
public class Dungeon {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Builder.Default
    @Column(name = "recommended_power", nullable = false)
    private int recommendedPower = 0;

    @OneToMany(mappedBy = "dungeon", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("mapIndex")
    @Builder.Default
    private java.util.List<org.example.domain.entity.DungeonMap> maps = new java.util.ArrayList<>();
}
