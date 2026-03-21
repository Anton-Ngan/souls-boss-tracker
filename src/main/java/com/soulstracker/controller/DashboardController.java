package com.soulstracker.controller;

import com.soulstracker.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final StatsService statsService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
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

        // Recent activity feed
        model.addAttribute("recentActivity", statsService.getRecentActivity());

        return "dashboard";
    }
}
