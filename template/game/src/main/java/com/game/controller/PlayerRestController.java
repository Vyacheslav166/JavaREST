package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(path = "/rest/players")
public class PlayerRestController {

    private final PlayerServiceImpl playerService;

    @Autowired
    public PlayerRestController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
    }

    /**
     * Создает игрока
     *
     * @param player Данные игрока
     * @return Объект игрока. <p>
     * При отсутствии входных данных возвращает пустой ответ с HTTP статусом <code>400</code>
     */
    @PostMapping(path = "/", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> createPlayer(@RequestBody Player player){
        return ResponseEntity.ok(playerService.createPlayer(player));
    }

    /**
     * Возвращает игрока по id
     *
     * @param id игрока
     * @return Объект игрока. <p>
     *      * При отсутствии входных данных возвращает пустой ответ с HTTP статусом <code>400</code>
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return  ResponseEntity.ok(playerService.getPlayerById(id));
    }

    /**
     * Возвращает список всех игроков
     *
     * @param name
     * @param title
     * @param race
     * @param profession
     * @param after
     * @param before
     * @param banned
     * @param minExperience
     * @param maxExperience
     * @param minLevel
     * @param maxLevel
     * @param order
     * @param pageNumber
     * @param pageSize
     * @return Список объектов игроков <p>
     *      * При отсутствии входных данных возвращает пустой ответ с HTTP статусом <code>400</code>
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
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

    /**
     * Возвращает количество всех игроков согласно фильтра
     *
     * @param name
     * @param title
     * @param race
     * @param profession
     * @param after
     * @param before
     * @param banned
     * @param minExperience
     * @param maxExperience
     * @param minLevel
     * @param maxLevel
     * @return Количество игроков отвечающих фильтрам <p>
     *       * При отсутствии входных данных возвращает пустой ответ с HTTP статусом <code>400</code>
     */
    @GetMapping(value = "/count", produces = MediaType.APPLICATION_JSON_VALUE)
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

    /**
     * Вносит изменения в поля игрока
     *
     * @param id
     * @param player
     * @return Объект игрока <p>
     *      * При отсутствии входных данных возвращает пустой ответ с HTTP статусом <code>400</code>
     */
    @PostMapping(value = "{id}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @PostMapping ("/{id}")
    @ResponseBody
    public ResponseEntity<Player> updatePlayer(@PathVariable Long id, @RequestBody @Validated Player player) {
        return ResponseEntity.ok(playerService.updatePlayer(id, player));
    }

    /**
     * Удаляет игрока
     *
     * @param id
     * @return Объект игрока <p>
     *      * При отсутствии входных данных возвращает пустой ответ с HTTP статусом <code>400</code>
     */
    //удаление игрока по id
    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Player> deletePlayer(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.deletePlayer(id));
    }
}
