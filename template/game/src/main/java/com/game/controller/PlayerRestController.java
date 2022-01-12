package com.game.controller;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private PlayerService playerService;

    public PlayerRestController() {
    }

    @Autowired
    public PlayerRestController(PlayerService playerService) {
        this.playerService = playerService;
    }

    //получение игрока по id
    @GetMapping("/players/{id}")
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

    //создание нового игрока
    @PostMapping("/players")
    public ResponseEntity<Player> savePlayer(@RequestBody Player player){
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        playerService.save(player);

        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    //изменение игрока по id
    @PostMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(@RequestBody Player player, @PathVariable("id") Long id) {

        if(id<=0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        playerService.save(player);
        return new ResponseEntity<>(player, HttpStatus.CREATED);
    }

    //удаление игрока по id
    @DeleteMapping("/players/{id}")
    public ResponseEntity<Player> deletePlayer(@PathVariable("id") Long id) {
        final Player player = this.playerService.getById(id);

        if(player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        this.playerService.delete(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    //получение всех игроков
    @GetMapping("/players")
    public ResponseEntity<List<Player>> getAllPlayers(
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
        final List<Player> players = playerService.getAll(name, title, race, profession, after, before, banned,
                minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize);

        return ResponseEntity.ok(players);
    }

    //получение количества всех игроков
    @GetMapping("/players/count")
    public Integer getPlayersCount(
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
        return playerService.getAll(name, title, race, profession, after, before, banned, minExperience, maxExperience,
            minLevel, maxLevel, order, pageNumber, pageSize).size();
    }
}
