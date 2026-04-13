package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.Player;
import org.example.dto.CreatePlayerRequest;
import org.example.exception.ResourceNotFoundException;
import org.example.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service quản lý Player.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlayerService {

    private final PlayerRepository playerRepository;

    /**
     * Tạo player mới.
     */
    public Player createPlayer(CreatePlayerRequest request) {
        if (playerRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username đã tồn tại: " + request.getUsername());
        }

        Player player = Player.builder()
                .username(request.getUsername())
                .name(request.getName())
                .passwordHash(resolveRawPassword(request))
                .level(1)
                .experience(0)
                .build();

        Player saved = playerRepository.save(player);
        log.info("Tạo player mới: {} ({})", saved.getName(), saved.getId());
        return saved;
    }

    /**
     * Lấy player theo ID.
     */
    @Transactional(readOnly = true)
    public Player getPlayer(Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Player không tồn tại: " + id));
    }

    /**
     * Lấy player theo username.
     */
    @Transactional(readOnly = true)
    public Player getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Player không tồn tại: " + username));
    }

    /**
     * Lấy tất cả players.
     */
    @Transactional(readOnly = true)
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    /**
     * Cập nhật player.
     */
    public Player updatePlayer(Long id, String name) {
        Player player = getPlayer(id);
        player.setName(name);
        return playerRepository.save(player);
    }

    /**
     * Tăng level cho player.
     */
    public Player levelUp(Long id) {
        Player player = getPlayer(id);
        player.setLevel(player.getLevel() + 1);
        log.info("Player {} tăng lên level {}", player.getName(), player.getLevel());
        return playerRepository.save(player);
    }

    /**
     * Thêm experience cho player.
     */
    public Player addExperience(Long id, int exp) {
        Player player = getPlayer(id);
        player.setExperience(player.getExperience() + exp);
        
        // Auto level up (mỗi 100 exp = 1 level)
        while (player.getExperience() >= player.getLevel() * 100) {
            player.setExperience(player.getExperience() - player.getLevel() * 100);
            player.setLevel(player.getLevel() + 1);
            log.info("Player {} tự động tăng lên level {}", player.getName(), player.getLevel());
        }
        
        return playerRepository.save(player);
    }

    private String resolveRawPassword(CreatePlayerRequest request) {
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            return request.getPassword();
        }
        return request.getUsername();
    }
}

