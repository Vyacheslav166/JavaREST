package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/rest/players/")
public class PlayerRestController {

    private PlayerService playerService;

    public PlayerRestController() {
    }

    @Autowired
    public PlayerRestController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @RequestMapping(path = "/rest/players/{id}",value = "{id}", method = RequestMethod.GET)
    public ResponseEntity<Player> getPlayer(@PathVariable("id") Long playerId) {
        if (playerId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Player player = this.playerService.getById(playerId);

        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(player, HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Player> savePlayer(@RequestBody @Validated Player player){
        HttpHeaders headers = new HttpHeaders();

        if (player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        this.playerService.save(player);

        return new ResponseEntity<>(player, headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Player> updatePlayer(@RequestBody @Validated Player player, UriComponentsBuilder builder) {
        HttpHeaders headers = new HttpHeaders();

        if(player == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        this.playerService.save(player);
        return new ResponseEntity<>(player, headers, HttpStatus.OK);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Player> deletePlayer(@PathVariable("id") Long id) {
        Player player = this.playerService.getById(id);

        if(player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        this.playerService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(path = "/rest/players", method = RequestMethod.GET)
    public ResponseEntity<List<Player>> getAllPlayers() {
        List<Player> players = this.playerService.getAll();

        if(players.isEmpty()) {
            return new ResponseEntity<List<Player>>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(players, HttpStatus.OK);
    }

    @RequestMapping("/")
    public ModelAndView home() {
        List<Player> listPlayer = playerService.getAll();
        ModelAndView mav = new ModelAndView("index");
        mav.addObject("listPPlayer", listPlayer);
        return mav;
    }
}
