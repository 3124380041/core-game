package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Hero;
import org.example.domain.enums.HeroType;
import org.example.dto.CreateHeroRequest;
import org.example.dto.HeroResponse;
import org.example.dto.HeroSummary;
import org.example.service.HeroService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho Hero.
 */
@RestController
@RequestMapping("/api/heroes")
@RequiredArgsConstructor
public class HeroController {

    private final HeroService heroService;

    /**
     * Tạo hero mới cho player.
     * POST /api/heroes?playerId={playerId}
     */
    @PostMapping
    public ResponseEntity<HeroResponse> createHero(@RequestParam Long playerId,
                                                    @Valid @RequestBody CreateHeroRequest request) {
        Hero hero = heroService.createHero(playerId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(HeroResponse.from(hero));
    }

    /**
     * Lấy hero theo ID.
     * GET /api/heroes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<HeroResponse> getHero(@PathVariable Long id) {
        Hero hero = heroService.getHero(id);
        return ResponseEntity.ok(HeroResponse.from(hero));
    }

    /**
     * Lấy danh sách heroes.
     * GET /api/heroes - Lấy tất cả heroes
     * GET /api/heroes?playerId={playerId} - Lấy heroes của player
     * GET /api/heroes?type={type} - Lấy heroes theo type
     */
    @GetMapping
    public ResponseEntity<List<HeroSummary>> getHeroes(@RequestParam(required = false) Long playerId,
                                                        @RequestParam(required = false) HeroType type) {
        List<Hero> heroes;
        if (playerId != null) {
            heroes = heroService.getHeroesByPlayer(playerId);
        } else if (type != null) {
            heroes = heroService.getHeroesByType(type);
        } else {
            // Trả về tất cả heroes khi không có filter
            heroes = heroService.getAllHeroes();
        }
        
        return ResponseEntity.ok(heroes.stream()
                .map(HeroSummary::from)
                .toList());
    }

    /**
     * Tăng level hero.
     * POST /api/heroes/{id}/levelup
     */
    @PostMapping("/{id}/levelup")
    public ResponseEntity<HeroResponse> levelUp(@PathVariable Long id) {
        Hero hero = heroService.levelUp(id);
        return ResponseEntity.ok(HeroResponse.from(hero));
    }

    /**
     * Tăng sao hero.
     * POST /api/heroes/{id}/starup
     */
    @PostMapping("/{id}/starup")
    public ResponseEntity<HeroResponse> starUp(@PathVariable Long id) {
        Hero hero = heroService.starUp(id);
        return ResponseEntity.ok(HeroResponse.from(hero));
    }

    /**
     * Gán skill cho hero.
     * POST /api/heroes/{id}/skills?skillId={skillId}&slot={slot}
     */
    @PostMapping("/{id}/skills")
    public ResponseEntity<HeroResponse> assignSkill(@PathVariable Long id,
                                                     @RequestParam Long skillId,
                                                     @RequestParam int slot) {
        Hero hero = heroService.assignSkill(id, skillId, slot);
        return ResponseEntity.ok(HeroResponse.from(hero));
    }

    /**
     * Nâng cấp skill.
     * POST /api/heroes/{id}/skills/{slot}/upgrade
     */
    @PostMapping("/{id}/skills/{slot}/upgrade")
    public ResponseEntity<HeroResponse> upgradeSkill(@PathVariable Long id,
                                                      @PathVariable int slot) {
        Hero hero = heroService.upgradeSkill(id, slot);
        return ResponseEntity.ok(HeroResponse.from(hero));
    }
}

