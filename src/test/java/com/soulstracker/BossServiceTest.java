package com.soulstracker;

import com.soulstracker.model.Boss;
import com.soulstracker.model.BossAttempt;
import com.soulstracker.model.Game;
import com.soulstracker.repository.BossAttemptRepository;
import com.soulstracker.repository.BossRepository;
import com.soulstracker.repository.GameRepository;
import com.soulstracker.service.BossService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BossServiceTest {

    @Mock BossRepository        bossRepository;
    @Mock BossAttemptRepository bossAttemptRepository;
    @Mock GameRepository        gameRepository;

    @InjectMocks BossService bossService;

    // ── logDeath ────────────────────────────────────────────────────────────

    @Test
    void logDeath_savesAttemptWithDiedTrueAndCorrectBoss() {
        Boss boss = new Boss();

        bossService.logDeath(boss);

        ArgumentCaptor<BossAttempt> captor = ArgumentCaptor.forClass(BossAttempt.class);
        verify(bossAttemptRepository).save(captor.capture());
        assertThat(captor.getValue().isDied()).isTrue();
        assertThat(captor.getValue().getBoss()).isSameAs(boss);
    }

    // ── toggleCleared ───────────────────────────────────────────────────────

    @Test
    void toggleCleared_setsClearedAndTimestamp_whenNotCleared() {
        Boss boss = new Boss();
        boss.setCleared(false);

        bossService.toggleCleared(boss);

        assertThat(boss.isCleared()).isTrue();
        assertThat(boss.getClearedAt()).isNotNull();
        verify(bossRepository).save(boss);
    }

    @Test
    void toggleCleared_unsetsClearedAndNullsTimestamp_whenAlreadyCleared() {
        Boss boss = new Boss();
        boss.setCleared(true);
        boss.setClearedAt(LocalDateTime.now());

        bossService.toggleCleared(boss);

        assertThat(boss.isCleared()).isFalse();
        assertThat(boss.getClearedAt()).isNull();
        verify(bossRepository).save(boss);
    }

    // ── saveNotes ───────────────────────────────────────────────────────────

    @Test
    void saveNotes_updatesNotesAndPersists() {
        Boss boss = new Boss();

        bossService.saveNotes(boss, "tried jumping attack");

        assertThat(boss.getNotes()).isEqualTo("tried jumping attack");
        verify(bossRepository).save(boss);
    }

    // ── findBossesByGame filter ─────────────────────────────────────────────

    @Test
    void findBossesByGame_withClearedFilter_returnsOnlyClearedBosses() {
        Game game = new Game();
        Boss cleared   = bossWithCleared("Godrick", true);
        Boss remaining = bossWithCleared("Margit",  false);
        when(bossRepository.findByGameOrderByNameAsc(game)).thenReturn(List.of(cleared, remaining));

        List<Boss> result = bossService.findBossesByGame(game, "cleared");

        assertThat(result).containsExactly(cleared);
    }

    @Test
    void findBossesByGame_withRemainingFilter_returnsOnlyNonClearedBosses() {
        Game game = new Game();
        Boss cleared   = bossWithCleared("Godrick", true);
        Boss remaining = bossWithCleared("Margit",  false);
        when(bossRepository.findByGameOrderByNameAsc(game)).thenReturn(List.of(cleared, remaining));

        List<Boss> result = bossService.findBossesByGame(game, "remaining");

        assertThat(result).containsExactly(remaining);
    }

    @Test
    void findBossesByGame_withAllFilter_returnsEveryBoss() {
        Game game = new Game();
        Boss cleared   = bossWithCleared("Godrick", true);
        Boss remaining = bossWithCleared("Margit",  false);
        when(bossRepository.findByGameOrderByNameAsc(game)).thenReturn(List.of(cleared, remaining));

        List<Boss> result = bossService.findBossesByGame(game, "all");

        assertThat(result).containsExactly(cleared, remaining);
    }

    // ── helpers ─────────────────────────────────────────────────────────────

    private Boss bossWithCleared(String name, boolean cleared) {
        Boss b = new Boss();
        b.setName(name);
        b.setCleared(cleared);
        return b;
    }
}
