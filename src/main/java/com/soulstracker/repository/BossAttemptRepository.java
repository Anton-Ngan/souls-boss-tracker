package com.soulstracker.repository;

import com.soulstracker.model.Boss;
import com.soulstracker.model.BossAttempt;
import com.soulstracker.model.Game;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BossAttemptRepository extends JpaRepository<BossAttempt, Long> {
    List<BossAttempt> findByBossOrderByAttemptedAtDesc(Boss boss);
    long countByBossAndDiedTrue(Boss boss);
    long countByDiedTrue();

    @EntityGraph(attributePaths = {"boss", "boss.game"})
    List<BossAttempt> findTop10ByOrderByAttemptedAtDesc();

    @Query("SELECT a FROM BossAttempt a ORDER BY a.attemptedAt ASC")
    List<BossAttempt> findAllOrderByAttemptedAtAsc();

    void deleteByBoss(Boss boss);

    @EntityGraph(attributePaths = {"boss", "boss.game"})
    List<BossAttempt> findTop10ByBossGameOrderByAttemptedAtDesc(Game boss_game);
}
