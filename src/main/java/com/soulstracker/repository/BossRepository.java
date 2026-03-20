package com.soulstracker.repository;

import com.soulstracker.model.Boss;
import com.soulstracker.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BossRepository extends JpaRepository<Boss, Long> {
    List<Boss> findByGame(Game game);
    List<Boss> findByGameOrderByNameAsc(Game game);
    long countByClearedTrue();
}
