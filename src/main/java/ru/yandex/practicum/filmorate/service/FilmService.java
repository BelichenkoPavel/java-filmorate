package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class FilmService {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Logger log = LoggerFactory.getLogger(FilmController.class);

    private LocalDate minDate = LocalDate.of(1895, 12, 28);

    private FilmStorage filmStorage;

    private final UserService userService;

    @Autowired
    public FilmService(InMemoryFilmStorage filmStorage, UserService userService) {
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

        getFilm((long) film.getId());

        filmStorage.updateFilm(film);

        return film;
    }

    public ArrayList<Film> getFilms() {
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
        Film film = getFilm(id);

        film.addLike(userId);

        updateFilm(film);
    }

    public void deleteLikeFilm(Long id, Long userId) {
        userService.getUser(userId);
        Film film = getFilm(id);

        film.removeLike(userId);

        updateFilm(film);
    }

    public List<Film> getPopularFilms(Integer count) {
        ArrayList<Film> films = getFilms();

        films.sort((film1, film2) -> {
            int likes1 = film1.getLikes().size();
            int likes2 = film2.getLikes().size();

            return likes2 - likes1;
        });

        if (count != null) {
            count = 10;
        }

        if (count > films.size()) {
            count = films.size();
        }

        return films.subList(0, count);
    }
}
