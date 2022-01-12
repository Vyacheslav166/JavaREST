package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService{

    private static final int MAX_LENGTH_NAME = 12;
    private static final int MAX_LENGTH_TITLE = 30;
    private static final int MAX_EXPERIENCE = 10000000;
    private static final int MIN_BIRTHDAY = 2000;
    private static final int MAX_BIRTHDAY = 3000;

    private PlayerRepository playerRepository;

    public PlayerServiceImpl(){
    }

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    //создание игрока
    @Override
    public Player create(Player player) {
        isPlayerValid(player);

        if(player.getBanned() == null)
            player.setBanned(false);

        player.setLevel(getCurrentLevel(player.getExperience()));
        player.setUntilNextLevel(getExperienceUntilNextLevel(player.getExperience(), player.getLevel()));

        return playerRepository.saveAndFlush(player);
    }

    //получение игрока по id
    @Override
    public Player getById(Long id) {
        isIdValid(id);
        Player player;
        try {
            player = playerRepository.getOne(id);
        } catch (Exception e) {
            throw new NotFoundException("Player not found!");
        }
        return player;
    }

    //получение всех игроков по фильтрам
    @Override
    public Page<Player> getAll(Specification<Player> specification, Pageable pageable) {
        return playerRepository.findAll(specification, pageable);
    }

    @Override
    public Long getCount(Specification<Player> specification) {
        return playerRepository.count(specification);
    }


    //получение страницы с игроками
    public Page<Player> getPage(
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
            Integer maxLevel,
            Pageable pageable
    ) {
        final Date afterDate = after == null ? null : new Date(after);
        final Date beforeDate = before == null ? null : new Date(before);
        List<Player> playerList = new ArrayList<>();
        playerRepository.findAll().forEach((player) -> {
            if (name != null && !player.getName().contains(name)) return;
            if (title != null && !player.getTitle().contains(title)) return;
            if (race != null && player.getRace() != race) return;
            if (profession != null && player.getProfession() != profession) return;
            if (afterDate != null && player.getBirthday().before(afterDate)) return;
            if (beforeDate != null && player.getBirthday().after(beforeDate)) return;
            if (banned != null && player.getBanned().booleanValue() != banned.booleanValue()) return;
            if (minExperience != null && player.getExperience().compareTo(minExperience) < 0) return;
            if (maxExperience != null && player.getExperience().compareTo(maxExperience) > 0) return;
            if (minLevel != null && player.getLevel().compareTo(minLevel) < 0) return;
            if (maxLevel != null && player.getLevel().compareTo(maxLevel) > 0) return;

            playerList.add(player);
        });
        return playerRepository.findAll(playerList, pageable);
    }

    //сортировка игроков
        public List<Player> sortPlayers(List<Player> players, PlayerOrder order) {
        if (order != null) {
            players.sort((player1, player2) -> {
                switch (order) {
                    case ID: return player1.getId().compareTo(player2.getId());
                    case LEVEL: return player1.getLevel().compareTo(player2.getLevel());
                    case EXPERIENCE: return player1.getExperience().compareTo(player2.getExperience());
                    case BIRTHDAY: return player1.getBirthday().compareTo(player2.getBirthday());
                    default: return 0;
                }
            });
        }
        return players;
    }

    //изменение игрока
    @Override
    public Player update(Long id, Player newPlayer) {
        Player oldPlayer = getById(id);

        final String name = newPlayer.getName();
        if (name != null) {
            isNameValid(name);
            oldPlayer.setName(name);
        }

        final String title = newPlayer.getTitle();
        if (title != null) {
            isTitleValid(title);
            oldPlayer.setTitle(title);
        }

        final Race race = newPlayer.getRace();
        if (race != null) {
            isRaceValid(race);
            oldPlayer.setRace(race);
        }

        final Profession profession = newPlayer.getProfession();
        if (profession != null) {
            isProfessionValid(profession);
            oldPlayer.setProfession(profession);
        }

        final Date birthday = newPlayer.getBirthday();
        if (birthday != null) {
            isBirthdayValid(birthday);
            oldPlayer.setBirthday(birthday);
        }

        final Integer experience = newPlayer.getExperience();
        if (experience != null) {
            isExperienceValid(experience);
            oldPlayer.setExperience(experience);
        }

        if (newPlayer.getBanned() != null) {
            oldPlayer.setBanned(newPlayer.getBanned());
        }

        oldPlayer.setLevel(getCurrentLevel(oldPlayer.getExperience()));
        oldPlayer.setUntilNextLevel(getExperienceUntilNextLevel(oldPlayer.getExperience(), oldPlayer.getLevel()));

        return playerRepository.saveAndFlush(oldPlayer);
    }

    //удаление игрока
    @Override
    public void delete(Long id) {
        playerRepository.deleteById(id);
    }

    //проверка правильности данных игрока
    public void isPlayerValid(Player player) {
        if(player == null)
            throw new BadRequestException("Invalid player");
        isIdValid(player.getId());
        isExperienceValid(player.getExperience());
        isNameValid(player.getName());
        isTitleValid(player.getTitle());
        isRaceValid(player.getRace());
        isProfessionValid(player.getProfession());
        isBirthdayValid(player.getBirthday());
    }

    //проверка id
    private void isIdValid(Long value) {
        if(value <= 0)
            throw new BadRequestException("Invalid ID");
    }
    //проверка значений опыта
    private void isExperienceValid(Integer value) {
       if(value < 0 || value > MAX_EXPERIENCE)
           throw new BadRequestException("Invalid experience");
    }

    //проверка имени
    private void isNameValid(String value) {
        if(value == null || value.isEmpty() || value.length() > MAX_LENGTH_NAME)
            throw new BadRequestException("Invalid name");
    }

    //проверка титула
    private void isTitleValid(String value) {
        if(value == null || value.isEmpty() || value.length() > MAX_LENGTH_TITLE)
            throw new BadRequestException("Invalid title");
    }

    //проверка расы
    private void isRaceValid(Race value) {
        if(value == null)
            throw new BadRequestException("Invalid race");
    }

    //проверка профессии
    private void isProfessionValid(Profession value) {
        if(value == null)
            throw new BadRequestException("Invalid profession");
    }

    //проверка даты рождения
    private void isBirthdayValid(Date date) {
        if(date == null)
            throw new BadRequestException("Invalid birthday");

        final Date startDate = getDateForYear(MIN_BIRTHDAY);
        final Date finishDate = getDateForYear(MAX_BIRTHDAY);
        if (date.before(startDate) || date.after(finishDate))
            throw new BadRequestException("Birthday is not included");
    }

    //дата по введенному году
    private Date getDateForYear(int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    //год по введенной дате
    private int getYearFromDate(Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    //возвращает текущий уровень игрока
    public Integer getCurrentLevel(Integer experience) {
        return ((int)Math.sqrt(2500 + 200 * experience) - 50) / 100;
    }

    //опыт до следующего уровня
    public Integer getExperienceUntilNextLevel(Integer experience, Integer level) {
        return 50 * (level + 1) * (level + 2) - experience;
    }

}
