package com.soulstracker.controller;

import com.soulstracker.service.BossService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class GameController {

    private final BossService bossService;

    @GetMapping("/games")
    public String gameList(Model model) {
        model.addAttribute("games", bossService.findAllGames());
        return "game-list";
    }

    @GetMapping("/games/{slug}")
    public String bossList(@PathVariable String slug,
                           @RequestParam(defaultValue = "all") String filter,
                           Model model) {
        var game = bossService.findGameBySlug(slug);
        model.addAttribute("game", game);
        model.addAttribute("bosses", bossService.findBossesByGame(game, filter));
        model.addAttribute("filter", filter);
        return "boss-list";
    }
}
