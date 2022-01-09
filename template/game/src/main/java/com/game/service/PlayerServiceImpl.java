package com.game.service;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService{

    private PlayerRepository playerRepository;

    public PlayerServiceImpl(){
    }

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        super();
        this.playerRepository = playerRepository;
    }

    @Override
    public Player getById(Long id) {
        return playerRepository.getOne(id);
    }

    @Override
    public void save(Player player) {
        playerRepository.save(player);
    }

    @Override
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    @Override
    public List<Player> getAll() {
        return playerRepository.findAll();
    }
}
