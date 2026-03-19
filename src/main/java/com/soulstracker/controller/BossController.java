package com.soulstracker.controller;

import com.soulstracker.service.BossService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
