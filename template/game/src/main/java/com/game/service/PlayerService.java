package com.game.service;

import com.game.entity.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface PlayerService {

    Player create(Player player);

    Player getById(Long id);

    Page<Player> getAll(Specification<Player> specification, Pageable pageable);

    Long getCount(Specification<Player> specification);

    Player update(Long id, Player oldPlayer) throws IllegalArgumentException;

    void delete(Long id);
}
