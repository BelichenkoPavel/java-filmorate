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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.*;

import java.time.LocalDate;
import java.util.*;

@Service
public class FilmService {
    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private Logger log = LoggerFactory.getLogger(FilmController.class);

    private LocalDate minDate = LocalDate.of(1895, 12, 28);

    @Autowired
    private FilmStorage filmStorage;

    @Autowired
    private UserService userService;

    @Autowired
    private GenreStorage genreStorage;

    @Autowired
    private FilmGenreStorage filmGenreStorage;

    @Autowired
    private MPAStorage mpaStorage;

    @Autowired
    private FilmLikeStorage filmLikeStorage;

    public Film addFilm(Film film) {
        validate(film);

        MPA mpa = mpaStorage.getById(film.getMpa().getId());

        if (mpa == null) {
            throw new ValidationException("MPA with id = " + film.getMpa().getId() + "not found");
        }

        List<Genre> genres;
        if (film.getGenres() != null) {
            genres = genreStorage.getGenres(film);

            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Genres not found");
            }
        } else {
            film.setGenres(new HashSet<>());
            genres = new ArrayList<>();
        }

        filmStorage.addFilm(film);

        filmGenreStorage.addFilmGenres(film);

        film.setMpa(mpa);
        film.setGenres(new LinkedHashSet<>(genres));

        return film;
    }

    public Film updateFilm(Film film) {
        validate(film);

        getFilm(film.getId());

        MPA mpa = mpaStorage.getById(film.getMpa().getId());

        if (mpa == null) {
            throw new ValidationException("MPA with id = " + film.getMpa().getId() + "not found");
        }

        List<Genre> genres;
        if (film.getGenres() != null) {
            genres = genreStorage.getGenres(film);

            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Genres not found");
            }
        } else {
            film.setGenres(new HashSet<>());
            genres = new ArrayList<>();
        }

        filmStorage.updateFilm(film);

        filmGenreStorage.addFilmGenres(film);

        film.setMpa(mpa);
        film.setGenres(new HashSet<>(genres));

        return film;
    }

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getAllFilms();

        films.forEach(film -> {
            List<Genre> genres = genreStorage.getGenresByFilm(film);

            film.setGenres(new HashSet<>(genres));
        });

        return films;
    }

    public Film getFilm(Long filmId) {
        Film film = filmStorage.getFilm(filmId);

        if (film == null) {
            log.error("Film with id " + filmId + " not found");
            throw new NotFoundException("Film with id " + filmId + " not found");
        }

        List<Genre> genres = genreStorage.getGenresByFilm(film);
        film.setGenres(new HashSet<>(genres));

        return film;
    }

    private void validate(Film film) {
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
        filmLikeStorage.addLike(id, userId);
    }

    public void deleteLikeFilm(Long id, Long userId) {
        userService.getUser(userId);
        filmLikeStorage.deleteLike(id, userId);
    }

    public List<Film> getPopularFilms(Integer count) {
        if (count != null) {
            count = 10;
        }

        List<Film> films = filmStorage.getPopularFilms(count);

        films.forEach(film -> {
            List<Genre> genres = genreStorage.getGenresByFilm(film);

            film.setGenres(new HashSet<>(genres));
        });

        return films;
    }
}
