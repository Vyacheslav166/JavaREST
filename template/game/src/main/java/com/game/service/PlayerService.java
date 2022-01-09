package com.game.service;

import com.game.entity.Player;

import java.util.List;

public interface PlayerService {

    void save(Player player);

    void delete(Long id);

    Player getById(Long id);

    List<Player> getAll();
}
