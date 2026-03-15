package com.soulstracker.repository;

import com.soulstracker.model.Boss;
import com.soulstracker.model.BossAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BossAttemptRepository extends JpaRepository<BossAttempt, Long> {
    List<BossAttempt> findByBossOrderByAttemptedAtDesc(Boss boss);
    long countByBossAndDiedTrue(Boss boss);
}
