package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import com.game.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/rest")
public class PlayerRestController {

    private PlayerServiceImpl playerService;

    public PlayerRestController() {
    }

    @Autowired
    public PlayerRestController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
    }

    //создание нового игрока
    @PostMapping("/players")
    @ResponseBody
    public ResponseEntity<Player> createPlayer(@RequestBody Player player){
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        playerService.create(player);

        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    //получение игрока по id
    @GetMapping("/players/{id}")
    @ResponseBody
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long playerId) {
        if (playerId == null || playerId <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final Player player = playerService.getById(playerId);

        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(player, HttpStatus.OK);
    }
    //получение всех игроков
    @GetMapping("/players")
    @ResponseBody
    public List<Player> getAllPlayers(
        @RequestParam(name = "name", defaultValue = "", required = false) String name,
        @RequestParam(name = "title", defaultValue = "", required = false) String title,
        @RequestParam(name = "race", defaultValue = "", required = false) Race race,
        @RequestParam(name = "profession", defaultValue = "", required = false) Profession profession,
        @RequestParam(name = "after", defaultValue = "", required = false) Long after,
        @RequestParam(name = "before", defaultValue = "", required = false) Long before,
        @RequestParam(name = "banned", defaultValue = "", required = false) Boolean banned,
        @RequestParam(name = "minExperience", defaultValue = "", required = false) Integer minExperience,
        @RequestParam(name = "maxExperience", defaultValue = "", required = false) Integer maxExperience,
        @RequestParam(name = "minLevel", defaultValue = "", required = false) Integer minLevel,
        @RequestParam(name = "maxLevel", defaultValue = "", required = false) Integer maxLevel,
        @RequestParam(name = "order", defaultValue = "", required = false) PlayerOrder order,
        @RequestParam(name = "pageNumber", defaultValue = "", required = false) Integer pageNumber,
        @RequestParam(name = "pageSize", defaultValue = "", required = false) Integer pageSize
    ) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        return playerService.getAll(
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
    @GetMapping("/players/count")
    @ResponseBody
    public Long getPlayersCount(
        @RequestParam(name = "name", defaultValue = "", required = false) String name,
        @RequestParam(name = "title", defaultValue = "", required = false) String title,
        @RequestParam(name = "race", defaultValue = "", required = false) Race race,
        @RequestParam(name = "profession", defaultValue = "", required = false) Profession profession,
        @RequestParam(name = "after", defaultValue = "", required = false) Long after,
        @RequestParam(name = "before", defaultValue = "", required = false) Long before,
        @RequestParam(name = "banned", defaultValue = "", required = false) Boolean banned,
        @RequestParam(name = "minExperience", defaultValue = "", required = false) Integer minExperience,
        @RequestParam(name = "maxExperience", defaultValue = "", required = false) Integer maxExperience,
        @RequestParam(name = "minLevel", defaultValue = "", required = false) Integer minLevel,
        @RequestParam(name = "maxLevel", defaultValue = "", required = false) Integer maxLevel
    ) {
        return playerService.getCount(
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
    @PostMapping("/players/{id}")
    @ResponseBody
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") Long id, @RequestBody Player player) {
        if(id<=0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        playerService.update(id, player);
        return new ResponseEntity<>(player, HttpStatus.CREATED);
    }

    //удаление игрока по id
    @DeleteMapping("/players/{id}")
    @ResponseBody
    public ResponseEntity<Player> deletePlayer(@PathVariable("id") Long id) {
        final Player player = this.playerService.getById(id);

        if(player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        this.playerService.delete(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
