package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Logger log = LoggerFactory.getLogger(FilmController.class);

    private LocalDate minDate = LocalDate.of(1895, 12, 28);

    @Autowired
    @Qualifier("dbFilmStorage")
    private FilmStorage filmStorage;

    @Autowired
    private UserService userService;

    @Autowired
    public FilmService(@Qualifier("dbFilmStorage") FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Film addFilm(Film film) {
        validate(film);

        filmStorage.addFilm(film);

        return film;
    }

    public Film updateFilm(Film film) throws ValidationException {
        validate(film);

        getFilm(film.getId());

        filmStorage.updateFilm(film);

        return film;
    }

    public List<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilm(Long filmId) {
        Film film = filmStorage.getFilm(filmId);

        if (film == null) {
            log.error("Film with id " + filmId + " not found");
            throw new NotFoundException("Film with id " + filmId + " not found");
        }

        return film;
    }

    private void validate(Film film) throws ValidationException {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (violations.size() > 0) {
            violations.forEach(error -> {
                log.error(error.getMessage());
            });

            throw new ValidationException("Validation exception");
        }

        if (film.getReleaseDate().isBefore(minDate)) {
            log.error("Film releaseDate must not be before 1895-12-28");
            throw new ValidationException("Validation exception");
        }
    }

    public void likeFilm(Long id, Long userId) {
        userService.getUser(userId);
        filmStorage.likeFilm(id, userId);
    }

    public void deleteLikeFilm(Long id, Long userId) {
        userService.getUser(userId);
        filmStorage.deleteLikeFilm(id, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count != null) {
            count = 10;
        }

        return filmStorage.getPopularFilms(count);
    }
}
