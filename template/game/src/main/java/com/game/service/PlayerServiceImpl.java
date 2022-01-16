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

    /**
     * Проверка правильности данных игрока
     *
     * @param player объект игрока
     */
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

    /**
     * Проверка id
     *
     * @param value id
     * @throws BadRequestException при значении null или меньше 0
     */
    public void isIdValid(Long value) throws BadRequestException {
        if (value == null || value <= 0)
            throw new BadRequestException("Invalid ID");
    }

    /**
     * Проверка значений опыта
     *
     * @param value значение опыта
     * @throws BadRequestException при значении опыта меньше 0 либо больше <code>MAX_EXPERIENCE</code>
     */
    public void isExperienceValid(Integer value) throws BadRequestException {
       if (value == null || value < 0 || value > MAX_EXPERIENCE)
           throw new BadRequestException("Invalid experience");
    }

    /**
     * Проверка имени
     *
     * @param value имя игрова
     * @throws BadRequestException при не корректном значении имени игрока во входном параметре
     * и длине больше <code>MAX_LENGTH_NAME</code>
     */
    public void isNameValid(String value) throws BadRequestException {
        if (value == null || value.isEmpty() || value.length() > MAX_LENGTH_NAME)
            throw new BadRequestException("Invalid name");
    }

    /**
     * Проверка титула игрока
     * @param value титул игрока
     * @throws BadRequestException при не корректном значении титула игрока во входном параметре
     * и длине больше <code>MAX_LENGTH_TITLE</code>
     */
    public void isTitleValid(String value) throws BadRequestException {
        if (value == null || value.isEmpty() || value.length() > MAX_LENGTH_TITLE)
            throw new BadRequestException("Invalid title");
    }

    /**
     * Проверка расы игрока
     *
     * @param value раса игрока
     *@throws BadRequestException при не корректном значении расы игрока во входном параметре
     */
    public void isRaceValid(Race value) throws BadRequestException {
        if (value == null)
            throw new BadRequestException("Invalid race");
    }

    /**
     * Проверка профессии игрока
     *
     * @param value профессия игрока
     * @throws BadRequestException при не корректном значении профессии игрока во входном параметре
     */
    public void isProfessionValid(Profession value) throws BadRequestException {
        if (value == null)
            throw new BadRequestException("Invalid profession");
    }

    /**
     * Проверка даты рождения игрока
     *
     * @param date дата рождения игрока
     * @throws BadRequestException при не корректном значении даты рождения игрока во входном параметре,
     * даты ранее <code>MIN_BIRTHDAY</code> или позднее <code>MAX_BIRTHDAY</code>
     */
    public void isBirthdayValid(Date date) throws BadRequestException {
        if (date == null)
            throw new BadRequestException("Invalid birthday");

        final Date startDate = getDateForYear(MIN_BIRTHDAY);
        final Date finishDate = getDateForYear(MAX_BIRTHDAY);
        if (date.before(startDate) || date.after(finishDate))
            throw new BadRequestException("Birthday is not included");
    }

    /**
     * Возвращает дату в формате <code>Date</code>
     *
     * @param year год от рождества Христова
     * @return дату в формате <code>Date</code>
     */
    public Date getDateForYear(int year) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * Определяет уровень игрока
     *
     * @param experience текущий опыт
     * @return текущий уровень игрока
     */
    public Integer getCurrentLevel(Integer experience) {
        return ((int)Math.sqrt(2500 + 200 * experience) - 50) / 100;
    }

    /**
     * Опредляет уровень до следующего уровня игрока
     *
     * @param experience текущий опыт
     * @param level текущий уровень
     * @return величину опыта, необходимого для перехода на аледующий уровень
     */
    public Integer getExperienceUntilNextLevel(Integer experience, Integer level) {
        return 50 * (level + 1) * (level + 2) - experience;
    }

    /**
     * Спецификация объекта игрока по фильтру имени
     *
     * @param name имя согласно фильтра
     * @return спецификация объекта игрока
     */
    public Specification<Player> findAllByNameLike(String name) {
        return (root, query, criteriaBuilder) ->
            name == null ? null : criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    /**
     * Спецификация объекта игрока по фильтру титула
     *
     * @param title титул согласно фильтра
     * @return спецификация объекта игрока
     */
    public Specification<Player> findAllByTitleLike(String title) {
        return (root, query, criteriaBuilder) ->
            title == null ? null : criteriaBuilder.like(root.get("title"), "%" + title + "%");
    }

    /**
     * Спецификация объекта игрока по фильтру расы
     *
     * @param race раса согласно фильтра
     * @return спецификация объекта игрока
     */
    public Specification<Player> findAllByRaceLike(Race race) {
        return (root, query, criteriaBuilder) ->
            race == null ? null : criteriaBuilder.equal(root.get("race"), race);
    }

    /**
     * Спецификация объекта игрока по фильтру профессий
     *
     * @param profession профессия согласно фильтра
     * @return спецификация объекта игрока
     */
    public Specification<Player> findAllByProfessionLike(Profession profession) {
        return (root, query, criteriaBuilder) ->
            profession == null ? null : criteriaBuilder.equal(root.get("profession"), profession);
    }

    /**
     * Спецификация объекта игрока по фильтру даты рождения
     *
     * @param after дата с
     * @param before дата по
     * @return спецификация объекта игрока
     */
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

    /**
     * Спецификация объекта игрока по фильтру баннер
     *
     * @param isBanned статус параметра баннер
     * @return спецификация объекта игрока
     */
    public Specification<Player> findAllByBannedLike(Boolean isBanned) {
        return (root, query, criteriaBuilder) -> {
            if (isBanned == null)
                return null;

            if (isBanned)
                return criteriaBuilder.isTrue(root.get("banned"));

            return criteriaBuilder.isFalse(root.get("banned"));
        };
    }

    /**
     * Спецификация объекта игрока по фильтру опыта
     *
     * @param min опыт с
     * @param max опыт до
     * @return спецификация объекта игрока
     */
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

    /**
     * Спецификация объекта игрока по фильтру уровня
     *
     * @param min уровень с
     * @param max уровень до
     * @return спецификация объекта игрока
     */
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
}
