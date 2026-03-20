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

    public List<String> findAreasByGame(Game game) {
        return bossRepository.findDistinctAreasByGame(game);
    }

    public List<Boss> findBossesByGame(Game game, String filter) {
        return findBossesByGame(game, filter, null, null);
    }

    public List<Boss> findBossesByGame(Game game, String filter, String q, String area) {
        List<Boss> all = bossRepository.findByGameOrderByNameAsc(game);
        return all.stream()
                .filter(b -> switch (filter) {
                    case "cleared"   -> b.isCleared();
                    case "remaining" -> !b.isCleared();
                    default          -> true;
                })
                .filter(b -> q == null || q.isBlank() ||
                        b.getName().toLowerCase().contains(q.toLowerCase()))
                .filter(b -> area == null || area.isBlank() ||
                        area.equals(b.getArea()))
                .toList();
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

    @Transactional
    public void resetDeaths(Boss boss) {
        bossAttemptRepository.deleteByBoss(boss);
    }
}
