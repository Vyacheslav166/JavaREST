package com.game.service;

import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PlayerService {

    Player createPlayer(Player player);

    Player getPlayerById(Long id);

    Page<Player> getAllPlayers(Specification<Player> specification, Pageable pageable);

    Long getPlayersCount(Specification<Player> specification);

    Player updatePlayer(Long id, Player oldPlayer) throws IllegalArgumentException;

    Player deletePlayer(Long id);
}
