package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;

import java.util.List;

public interface PlayerService {

    Player save(Player player);

    Player update(Player oldPlayer, Player newPlayer) throws IllegalArgumentException;

    void delete(Long id);

    Player getById(Long id);

    List<Player> getAll(
            String name,
            String title,
            Race race,
            Profession profession,
            Long after,
            Long before,
            Boolean banned,
            Integer minExperience,
            Integer maxExperience,
            Integer minLevel,
            Integer maxLevel
    );

    List<Player> getPage(List<Player> players, Integer pageNumber, Integer PageSize);

    List<Player> sortPlayers(List<Player> players, PlayerOrder order);

    boolean isPlayerValid(Player player);
}
