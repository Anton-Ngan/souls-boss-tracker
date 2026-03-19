package com.soulstracker.service;

import com.soulstracker.model.Boss;
import com.soulstracker.model.BossAttempt;
import com.soulstracker.model.Game;
import com.soulstracker.repository.BossAttemptRepository;
import com.soulstracker.repository.BossRepository;
import com.soulstracker.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BossService {

    private final GameRepository gameRepository;
    private final BossRepository bossRepository;
    private final BossAttemptRepository bossAttemptRepository;

    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }

    public Game findGameBySlug(String slug) {
        return gameRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found: " + slug));
    }

    public List<Boss> findBossesByGame(Game game, String filter) {
        List<Boss> all = bossRepository.findByGameOrderByNameAsc(game);
        return switch (filter) {
            case "cleared"   -> all.stream().filter(Boss::isCleared).toList();
            case "remaining" -> all.stream().filter(b -> !b.isCleared()).toList();
            default          -> all;
        };
    }

    public Boss findBossById(Long id) {
        return bossRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Boss not found: " + id));
    }

    public List<BossAttempt> findAttemptsByBoss(Boss boss) {
        return bossAttemptRepository.findByBossOrderByAttemptedAtDesc(boss);
    }

    @Transactional
    public void logDeath(Boss boss) {
        BossAttempt attempt = new BossAttempt();
        attempt.setBoss(boss);
        attempt.setDied(true);
        bossAttemptRepository.save(attempt);
    }

    @Transactional
    public void toggleCleared(Boss boss) {
        boss.setCleared(!boss.isCleared());
        boss.setClearedAt(boss.isCleared() ? LocalDateTime.now() : null);
        bossRepository.save(boss);
    }

    @Transactional
    public void saveNotes(Boss boss, String notes) {
        boss.setNotes(notes);
        bossRepository.save(boss);
    }
}
