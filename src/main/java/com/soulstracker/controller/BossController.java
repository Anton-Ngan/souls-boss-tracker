package com.soulstracker.controller;

import com.soulstracker.service.BossService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequiredArgsConstructor
public class BossController {

    private final BossService bossService;

    @GetMapping("/games/{slug}/bosses/{id}")
    public String bossDetail(@PathVariable String slug,
                             @PathVariable Long id,
                             Model model) {
        var game = bossService.findGameBySlug(slug);
        var boss = bossService.findBossById(id);

        if (!boss.getGame().getId().equals(game.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Boss not found in this game");
        }

        model.addAttribute("game", game);
        model.addAttribute("boss", boss);
        model.addAttribute("attempts", bossService.findAttemptsByBoss(boss));
        return "boss-detail";
    }

    @PostMapping("/games/{slug}/bosses/{id}/attempt")
    public String logAttempt(@PathVariable String slug,
                             @PathVariable Long id,
                             @RequestParam(defaultValue = "list") String source) {
        var boss = bossService.findBossById(id);
        bossService.logDeath(boss);
        return "detail".equals(source)
                ? "redirect:/games/" + slug + "/bosses/" + id
                : "redirect:/games/" + slug;
    }

    @PostMapping("/games/{slug}/bosses/{id}/clear")
    public String toggleCleared(@PathVariable String slug,
                                @PathVariable Long id,
                                @RequestParam(defaultValue = "list") String source) {
        var boss = bossService.findBossById(id);
        bossService.toggleCleared(boss);
        return "detail".equals(source)
                ? "redirect:/games/" + slug + "/bosses/" + id
                : "redirect:/games/" + slug;
    }

    @PostMapping("/games/{slug}/bosses/{id}/notes")
    public String saveNotes(@PathVariable String slug,
                            @PathVariable Long id,
                            @RequestParam(defaultValue = "") String notes) {
        var boss = bossService.findBossById(id);
        bossService.saveNotes(boss, notes);
        return "redirect:/games/" + slug + "/bosses/" + id;
    }

    @PostMapping("/games/{slug}/bosses/{id}/reset")
    public String resetDeaths(@PathVariable String slug,
                              @PathVariable Long id,
                              @RequestParam(defaultValue = "list") String source) {
        var boss = bossService.findBossById(id);
        bossService.resetDeaths(boss);
        return "detail".equals(source)
                ? "redirect:/games/" + slug + "/bosses/" + id
                : "redirect:/games/" + slug;
    }
}
