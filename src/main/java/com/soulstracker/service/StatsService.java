package com.soulstracker.service;

import com.soulstracker.model.Boss;
import com.soulstracker.model.BossAttempt;
import com.soulstracker.repository.BossAttemptRepository;
import com.soulstracker.repository.BossRepository;
import com.soulstracker.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final BossAttemptRepository bossAttemptRepository;
    private final BossRepository bossRepository;
    private final GameRepository gameRepository;

    public long getTotalDeaths() {
        return bossAttemptRepository.countByDiedTrue();
    }

    public long getBossesCleared() {
        return bossRepository.countByClearedTrue();
    }

    /** Games where at least one boss is cleared but not all bosses are cleared. */
    public long getGamesInProgress() {
        return gameRepository.findAll().stream()
                .filter(g -> {
                    List<Boss> bosses = bossRepository.findByGame(g);
                    long cleared = bosses.stream().filter(Boss::isCleared).count();
                    return cleared > 0 && cleared < bosses.size();
                })
                .count();
    }

    /** Longest run of consecutive attempts where the player did not die. */
    public int getLongestStreakWithoutDying() {
        List<BossAttempt> all = bossAttemptRepository.findAllOrderByAttemptedAtAsc();
        int longest = 0, current = 0;
        for (BossAttempt a : all) {
            if (!a.isDied()) {
                longest = Math.max(longest, ++current);
            } else {
                current = 0;
            }
        }
        return longest;
    }

    public List<BossDeathStat> getTopBossesByDeaths(int limit) {
        return bossRepository.findAll().stream()
                .map(b -> new BossDeathStat(
                        b.getName(),
                        b.getGame().getName(),
                        bossAttemptRepository.countByBossAndDiedTrue(b)))
                .filter(s -> s.deaths() > 0)
                .sorted(Comparator.comparingLong(BossDeathStat::deaths).reversed())
                .limit(limit)
                .toList();
    }

    public List<GameCompletionStat> getCompletionByGame() {
        return gameRepository.findAll().stream()
                .map(g -> {
                    List<Boss> bosses = bossRepository.findByGame(g);
                    long cleared = bosses.stream().filter(Boss::isCleared).count();
                    return new GameCompletionStat(g.getName(), cleared, bosses.size());
                })
                .toList();
    }

    public List<BossAttempt> getRecentActivity() {
        return bossAttemptRepository.findTop10ByOrderByAttemptedAtDesc();
    }

    public record BossDeathStat(String bossName, String gameName, long deaths) {}
    public record GameCompletionStat(String gameName, long cleared, int total) {}
}
