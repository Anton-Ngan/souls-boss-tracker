package com.soulstracker.controller;

import com.soulstracker.model.Game;
import com.soulstracker.service.BossService;
import com.soulstracker.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final StatsService statsService;
    private final BossService bossService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) String game, Model model) {
        // Stat cards
        model.addAttribute("totalDeaths",    statsService.getTotalDeaths());
        model.addAttribute("bossesCleared",  statsService.getBossesCleared());
        model.addAttribute("gamesInProgress",statsService.getGamesInProgress());
        model.addAttribute("longestStreak",  statsService.getLongestStreakWithoutDying());

        // Bar chart — top 10 bosses by deaths
        List<StatsService.BossDeathStat> topBosses = statsService.getTopBossesByDeaths(10);
        model.addAttribute("topBossLabels", topBosses.stream().map(StatsService.BossDeathStat::bossName).toList());
        model.addAttribute("topBossDeaths", topBosses.stream().map(StatsService.BossDeathStat::deaths).toList());

        // Doughnut chart — completion per game
        List<StatsService.GameCompletionStat> completion = statsService.getCompletionByGame();
        model.addAttribute("gameLabels",  completion.stream().map(StatsService.GameCompletionStat::gameName).toList());
        model.addAttribute("gameCleared", completion.stream().map(StatsService.GameCompletionStat::cleared).toList());
        model.addAttribute("gameTotal",   completion.stream().map(StatsService.GameCompletionStat::total).toList());

        // Game filter
        model.addAttribute("games", bossService.findAllGames());
        model.addAttribute("selectedGame", game);

        Game selectedGameObj = null;
        if (game != null && !game.isBlank()) {
            try { selectedGameObj = bossService.findGameBySlug(game); } catch (Exception ignored) {}
        }

        if (selectedGameObj != null) {
            model.addAttribute("gameDeaths",        statsService.getTotalDeathsForGame(selectedGameObj));
            model.addAttribute("gameBossesCleared", statsService.getBossesClearedForGame(selectedGameObj));
            model.addAttribute("deathsByArea",      statsService.getDeathsByArea(selectedGameObj));
            model.addAttribute("recentActivity",    statsService.getRecentActivityForGame(selectedGameObj));
        } else {
            model.addAttribute("recentActivity", statsService.getRecentActivity());
        }

        return "dashboard";
    }
}
