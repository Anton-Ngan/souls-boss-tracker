package com.soulstracker.repository;

import com.soulstracker.model.Boss;
import com.soulstracker.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BossRepository extends JpaRepository<Boss, Long> {
    List<Boss> findByGame(Game game);
    List<Boss> findByGameOrderByNameAsc(Game game);
    long countByClearedTrue();

    @Query("SELECT DISTINCT b.area FROM Boss b WHERE b.game = :game AND b.area IS NOT NULL AND b.area <> '' ORDER BY b.area")
    List<String> findDistinctAreasByGame(@Param("game") Game game);
}
