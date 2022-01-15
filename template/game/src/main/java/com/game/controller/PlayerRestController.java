package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.BadRequestException;
import com.game.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/rest/players")
public class PlayerRestController {

    private final PlayerServiceImpl playerService;

    @Autowired
    public PlayerRestController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
    }

    /**
     * Создает игрока
     *
     * @param player данные игрока
     * @return объект сгрока. <p>
     * При отсутствии входных данных возвращает пустой ответ с HTTP статусом <code>400</code>
     */
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(@RequestBody Player player){
        return ResponseEntity.ok(playerService.createPlayer(player));
    }

    //получение игрока по id
    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return  ResponseEntity.ok(playerService.getPlayerById(id));
    }
    //получение всех игроков
    @GetMapping
    @ResponseBody
    public List<Player> getAllPlayers(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "title",  required = false) String title,
        @RequestParam(value = "race", required = false) Race race,
        @RequestParam(value = "profession", required = false) Profession profession,
        @RequestParam(value = "after", required = false) Long after,
        @RequestParam(value = "before", required = false) Long before,
        @RequestParam(value = "banned", required = false) Boolean banned,
        @RequestParam(value = "minExperience", required = false) Integer minExperience,
        @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
        @RequestParam(value = "minLevel", required = false) Integer minLevel,
        @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
        @RequestParam(value = "order", defaultValue = "ID", required = false) PlayerOrder order,
        @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
        @RequestParam(value = "pageSize", defaultValue = "3") Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return playerService.getAllPlayers(
            Specification.where(playerService.findAllByNameLike(name))
                .and(playerService.findAllByTitleLike(title))
                .and(playerService.findAllByRaceLike(race))
                .and(playerService.findAllByProfessionLike(profession))
                .and(playerService.findAllByBirtdayLike(after, before))
                .and(playerService.findAllByBannedLike(banned))
                .and(playerService.findAllByExperienceLike(minExperience, maxExperience))
                .and(playerService.findAllByLevelLike(minLevel, maxLevel)), pageable).getContent();
    }

    //получение количества всех игроков
    @GetMapping("/count")
    @ResponseBody
    public Long getPlayersCount(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "title",  required = false) String title,
            @RequestParam(value = "race", required = false) Race race,
            @RequestParam(value = "profession", required = false) Profession profession,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "banned", required = false) Boolean banned,
            @RequestParam(value = "minExperience", required = false) Integer minExperience,
            @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
            @RequestParam(value = "minLevel", required = false) Integer minLevel,
            @RequestParam(value = "maxLevel", required = false) Integer maxLevel
    ) {
        return playerService.getPlayersCount(
            Specification.where(playerService.findAllByNameLike(name))
                .and(playerService.findAllByTitleLike(title))
                .and(playerService.findAllByRaceLike(race))
                .and(playerService.findAllByProfessionLike(profession))
                .and(playerService.findAllByBirtdayLike(after, before))
                .and(playerService.findAllByBannedLike(banned))
                .and(playerService.findAllByExperienceLike(minExperience, maxExperience))
                .and(playerService.findAllByLevelLike(minLevel, maxLevel)));
    }

    //изменение игрока по id
    @PostMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PostMapping ("/{id}")
    @ResponseBody
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody @Validated Player player) {
        return ResponseEntity.ok(playerService.updatePlayer(id, player));
    }

    //удаление игрока по id
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Player> deletePlayer(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.deletePlayer(id));
    }
}
