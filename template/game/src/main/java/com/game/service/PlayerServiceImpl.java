package com.game.service;

import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception.BadRequestException;
import com.game.exception.NotFoundException;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class PlayerServiceImpl implements PlayerService{

    private static final int MAX_LENGTH_NAME = 12;
    private static final int MAX_LENGTH_TITLE = 30;
    private static final int MAX_EXPERIENCE = 10000000;
    private static final int MIN_BIRTHDAY = 2000;
    private static final int MAX_BIRTHDAY = 3000;

    private PlayerRepository playerRepository;

    @Autowired
    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    //создание игрока
    @Override
    public Player createPlayer(Player player) {
        isPlayerValid(player);

        if (player.getBanned() == null)
            player.setBanned(false);

        player.setLevel(getCurrentLevel(player.getExperience()));
        player.setUntilNextLevel(getExperienceUntilNextLevel(player.getExperience(), player.getLevel()));

        return playerRepository.save(player);
    }

    //получение игрока по id
    @Override
    public Player getPlayerById(Long id) {
        isIdValid(id);
        return playerRepository.findById(id).orElseThrow(() ->
            new NotFoundException("Player not found!"));
    }

    //получение всех игроков по фильтрам
    @Override
    public Page<Player> getAllPlayers(Specification<Player> specification, Pageable pageable) {
        return playerRepository.findAll(specification, pageable);
    }

    @Override
    public Long getPlayersCount(Specification<Player> specification) {
        return playerRepository.count(specification);
    }

    //изменение игрока
    @Override
    public Player updatePlayer(Long id, Player newPlayer) {
        Player oldPlayer = getPlayerById(id);

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
    public Player deletePlayer(Long id) {
        Player player = getPlayerById(id);
        playerRepository.delete(player);
        return player;
    }

    //проверка правильности данных игрока
    public void isPlayerValid(Player player) {
        if (player == null)
            throw new BadRequestException("Invalid player");
        isExperienceValid(player.getExperience());
        isNameValid(player.getName());
        isTitleValid(player.getTitle());
        isRaceValid(player.getRace());
        isProfessionValid(player.getProfession());
        isBirthdayValid(player.getBirthday());
    }

    //проверка id
    public void isIdValid(Long value) throws BadRequestException {
        if (value == null || value <= 0)
            throw new BadRequestException("Invalid ID");
    }

    /**
     * проверка значений опыта
     *
     * @param value значение опыта
     * @throws BadRequestException при значении опыта мсеньше 0 либо больше <code>MAX_EXPERIENCE</code>
     */
    public void isExperienceValid(Integer value) throws BadRequestException {
       if (value == null || value < 0 || value > MAX_EXPERIENCE)
           throw new BadRequestException("Invalid experience");
    }

    /**
     * проверка имени
     *
     * @param value имя игрова
     * @throws BadRequestException при не корректном значении имени игрока во входном параметре
     */
    public void isNameValid(String value) throws BadRequestException {
        if (value == null || value.isEmpty() || value.length() > MAX_LENGTH_NAME)
            throw new BadRequestException("Invalid name");
    }

    //проверка титула
    public void isTitleValid(String value) throws BadRequestException {
        if (value == null || value.isEmpty() || value.length() > MAX_LENGTH_TITLE)
            throw new BadRequestException("Invalid title");
    }

    //проверка расы
    public void isRaceValid(Race value) {
        if (value == null)
            throw new BadRequestException("Invalid race");
    }

    //проверка профессии
    public void isProfessionValid(Profession value) {
        if (value == null)
            throw new BadRequestException("Invalid profession");
    }

    //проверка даты рождения
    public void isBirthdayValid(Date date) {
        if (date == null)
            throw new BadRequestException("Invalid birthday");

        final Date startDate = getDateForYear(MIN_BIRTHDAY);
        final Date finishDate = getDateForYear(MAX_BIRTHDAY);
        if (date.before(startDate) || date.after(finishDate))
            throw new BadRequestException("Birthday is not included");
    }

    //дата по введенному году
    public Date getDateForYear(int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    //год по введенной дате
    public int getYearFromDate(Date date) {
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

    //написание запросов по спецификациям
    public Specification<Player> findAllByNameLike(String name) {
        return (root, query, criteriaBuilder) ->
            name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public Specification<Player> findAllByTitleLike(String title) {
        return (root, query, criteriaBuilder) ->
            title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    public Specification<Player> findAllByRaceLike(Race race) {
        return (root, query, criteriaBuilder) ->
            race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    public Specification<Player> findAllByProfessionLike(Profession profession) {
        return (root, query, criteriaBuilder) ->
            profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    public Specification<Player> findAllByBirtdayLike(Long after, Long before) {
        return (root, query, criteriaBuilder) -> {
            if (after == null && before == null)
                return null;

            if (after == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("birthday"), new Date(before));

            if (before == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("birthday"), new Date(after));

            return criteriaBuilder.between(root.get("birthday"), new Date(after), new Date(before));
        };
    }

    public Specification<Player> findAllByBannedLike(Boolean isBanned) {
        return (root, query, criteriaBuilder) -> {
            if (isBanned == null)
                return null;

            if (isBanned)
                return criteriaBuilder.isTrue(root.get("banned"));

            return criteriaBuilder.isFalse(root.get("banned"));
        };
    }

    public Specification<Player> findAllByExperienceLike(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null)
                return null;

            if (min == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("experience"), max);

            if (max == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("experience"), min);

            return criteriaBuilder.between(root.get("experience"), min, max);
        };
    }

    public Specification<Player> findAllByLevelLike(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null)
                return null;

            if (min == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("level"), max);

            if (max == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("level"), min);

            return criteriaBuilder.between(root.get("level"), min, max);
        };
    }

    public Specification<Player> findAllByUntilNextLevelLike(Integer min, Integer max) {
        return (root, query, criteriaBuilder) -> {
            if (min == null && max == null)
                return null;

            if (min == null)
                return criteriaBuilder.lessThanOrEqualTo(root.get("untilNextLevel"), max);

            if (max == null)
                return criteriaBuilder.greaterThanOrEqualTo(root.get("untilNextLevel"), min);

            return criteriaBuilder.between(root.get("untilNextLevel"), min, max);
        };
    }
}
