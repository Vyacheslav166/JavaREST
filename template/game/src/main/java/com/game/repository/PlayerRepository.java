package com.game.repository;

// Repository interface for {@link Player} class.

import com.game.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
