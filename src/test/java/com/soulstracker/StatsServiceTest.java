package com.soulstracker;

import com.soulstracker.model.Boss;
import com.soulstracker.model.BossAttempt;
import com.soulstracker.model.Game;
import com.soulstracker.repository.BossAttemptRepository;
import com.soulstracker.repository.BossRepository;
import com.soulstracker.repository.GameRepository;
import com.soulstracker.service.StatsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock BossAttemptRepository bossAttemptRepository;
    @Mock BossRepository        bossRepository;
    @Mock GameRepository        gameRepository;

    @InjectMocks StatsService statsService;

    // ── getTopBossesByDeaths ────────────────────────────────────────────────

    @Test
    void getTopBossesByDeaths_returnsBossesDescendingByDeathCount() {
        Game game = gameNamed("Elden Ring");
        Boss malenia = bossIn(game, "Malenia");
        Boss margit   = bossIn(game, "Margit");
        Boss radahn   = bossIn(game, "Radahn");

        when(bossRepository.findAll()).thenReturn(List.of(malenia, margit, radahn));
        when(bossAttemptRepository.countByBossAndDiedTrue(malenia)).thenReturn(50L);
        when(bossAttemptRepository.countByBossAndDiedTrue(margit)).thenReturn(20L);
        when(bossAttemptRepository.countByBossAndDiedTrue(radahn)).thenReturn(35L);

        List<StatsService.BossDeathStat> result = statsService.getTopBossesByDeaths(10);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).bossName()).isEqualTo("Malenia");
        assertThat(result.get(0).deaths()).isEqualTo(50L);
        assertThat(result.get(1).bossName()).isEqualTo("Radahn");
        assertThat(result.get(2).bossName()).isEqualTo("Margit");
    }

    @Test
    void getTopBossesByDeaths_respectsLimit() {
        Game game = gameNamed("Elden Ring");
        when(bossRepository.findAll()).thenReturn(List.of(
                bossIn(game, "A"), bossIn(game, "B"), bossIn(game, "C")));
        when(bossAttemptRepository.countByBossAndDiedTrue(any())).thenReturn(5L);

        assertThat(statsService.getTopBossesByDeaths(2)).hasSize(2);
    }

    @Test
    void getTopBossesByDeaths_excludesBossesWithNoDeaths() {
        Game game = gameNamed("Elden Ring");
        Boss untouched = bossIn(game, "Tutorial Boss");

        when(bossRepository.findAll()).thenReturn(List.of(untouched));
        when(bossAttemptRepository.countByBossAndDiedTrue(untouched)).thenReturn(0L);

        assertThat(statsService.getTopBossesByDeaths(10)).isEmpty();
    }

    // ── getCompletionByGame ─────────────────────────────────────────────────

    @Test
    void getCompletionByGame_returnsCorrectClearedAndTotalCounts() {
        Game game = gameNamed("Elden Ring");
        Boss b1 = bossIn(game, "A"); b1.setCleared(true);
        Boss b2 = bossIn(game, "B"); b2.setCleared(true);
        Boss b3 = bossIn(game, "C"); b3.setCleared(false);

        when(gameRepository.findAll()).thenReturn(List.of(game));
        when(bossRepository.findByGame(game)).thenReturn(List.of(b1, b2, b3));

        List<StatsService.GameCompletionStat> result = statsService.getCompletionByGame();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).gameName()).isEqualTo("Elden Ring");
        assertThat(result.get(0).cleared()).isEqualTo(2L);
        assertThat(result.get(0).total()).isEqualTo(3);
    }

    @Test
    void getCompletionByGame_returnsZeroCleared_whenNoBossesDefeated() {
        Game game = gameNamed("Bloodborne");
        when(gameRepository.findAll()).thenReturn(List.of(game));
        when(bossRepository.findByGame(game)).thenReturn(List.of(
                bossIn(game, "Cleric Beast"), bossIn(game, "Father Gascoigne")));

        StatsService.GameCompletionStat stat = statsService.getCompletionByGame().get(0);

        assertThat(stat.cleared()).isZero();
        assertThat(stat.total()).isEqualTo(2);
    }

    // ── getLongestStreakWithoutDying ─────────────────────────────────────────

    @Test
    void getLongestStreak_returnsCorrectLongestNonDeathRun() {
        // death, survived, survived, survived, death, survived → longest = 3
        when(bossAttemptRepository.findAllOrderByAttemptedAtAsc()).thenReturn(List.of(
                attempt(true), attempt(false), attempt(false), attempt(false),
                attempt(true), attempt(false)
        ));

        assertThat(statsService.getLongestStreakWithoutDying()).isEqualTo(3);
    }

    @Test
    void getLongestStreak_returnsZero_whenAllAttemptsDied() {
        when(bossAttemptRepository.findAllOrderByAttemptedAtAsc()).thenReturn(List.of(
                attempt(true), attempt(true), attempt(true)
        ));

        assertThat(statsService.getLongestStreakWithoutDying()).isZero();
    }

    @Test
    void getLongestStreak_returnsZero_whenNoAttempts() {
        when(bossAttemptRepository.findAllOrderByAttemptedAtAsc()).thenReturn(List.of());

        assertThat(statsService.getLongestStreakWithoutDying()).isZero();
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Game gameNamed(String name) {
        Game g = new Game();
        g.setName(name);
        return g;
    }

    private Boss bossIn(Game game, String name) {
        Boss b = new Boss();
        b.setName(name);
        b.setGame(game);
        return b;
    }

    private BossAttempt attempt(boolean died) {
        BossAttempt a = new BossAttempt();
        a.setDied(died);
        return a;
    }
}
