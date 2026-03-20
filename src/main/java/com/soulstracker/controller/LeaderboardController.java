package com.soulstracker.controller;

import com.soulstracker.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LeaderboardController {

    private final StatsService statsService;

    @GetMapping("/leaderboard")
    public String leaderboard(@RequestParam(defaultValue = "deaths") String sort,
                              @RequestParam(defaultValue = "asc")    String dir,
                              Model model) {
        List<StatsService.LeaderboardRow> rows = statsService.getLeaderboard(sort, dir);
        model.addAttribute("rows", rows);
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        return "leaderboard";
    }

    @GetMapping("/leaderboard/export")
    public ResponseEntity<byte[]> export() {
        List<StatsService.LeaderboardRow> rows = statsService.getLeaderboard("deaths", "asc");

        var csv = new StringBuilder("Boss,Game,Deaths,Status,Cleared At\n");
        for (StatsService.LeaderboardRow row : rows) {
            csv.append(escapeCsv(row.bossName())).append(",")
               .append(escapeCsv(row.gameName())).append(",")
               .append(row.deaths()).append(",")
               .append(row.cleared() ? "Cleared" : "Remaining").append(",")
               .append(row.clearedAt() != null ? row.clearedAt().toString() : "")
               .append("\n");
        }

        byte[] bytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"leaderboard.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(bytes);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
