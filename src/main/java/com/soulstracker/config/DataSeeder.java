package com.soulstracker.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soulstracker.model.Boss;
import com.soulstracker.model.Game;
import com.soulstracker.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final GameRepository gameRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (gameRepository.count() > 0) {
            log.info("Database already seeded — skipping.");
            return;
        }

        var resource = new ClassPathResource("data/bosses.json");
        List<GameSeedDto> seedData = objectMapper.readValue(
                resource.getInputStream(),
                new TypeReference<>() {}
        );

        for (GameSeedDto dto : seedData) {
            Game game = new Game();
            game.setName(dto.name());
            game.setSlug(dto.slug());
            game.setCoverImage(dto.coverImage());

            for (BossSeedDto bossDto : dto.bosses()) {
                Boss boss = new Boss();
                boss.setName(bossDto.name());
                boss.setArea(bossDto.area());
                boss.setLore(bossDto.lore());
                boss.setGame(game);
                game.getBosses().add(boss);
            }

            gameRepository.save(game);
            log.info("Seeded game: {} ({} bosses)", game.getName(), game.getBosses().size());
        }

        log.info("Seeding complete.");
    }

    record GameSeedDto(String name, String slug, String coverImage, List<BossSeedDto> bosses) {}
    record BossSeedDto(String name, String area, String lore) {}
}
