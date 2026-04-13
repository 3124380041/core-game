package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.domain.entity.Player;
import org.example.exception.GameException;
import org.example.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final PlayerRepository playerRepository;

    public Player login(String username, String rawPassword) {
        Player player = playerRepository.findByUsername(username)
                .orElseThrow(() -> new GameException("Sai username hoặc password"));

        if (player.getPasswordHash() == null || !player.getPasswordHash().equals(rawPassword)) {
            throw new GameException("Sai username hoặc password");
        }

        return player;
    }
}

