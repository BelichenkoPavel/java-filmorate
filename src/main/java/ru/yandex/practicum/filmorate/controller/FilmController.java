package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

@RestController
@RequestMapping("/films")
public class FilmController {

    private int id = 1;

    private TreeMap<Integer, Film> films = new TreeMap<>();

    private final static Logger log = LoggerFactory.getLogger(FilmController.class);

    private LocalDate minDate = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws ValidationException {
        validate(film);
        film.setId(getId());

        films.put(film.getId(), film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws ValidationException {
        validate(film);

        if (!films.containsKey(film.getId())) {
            log.error("Film not found");
            throw new ValidationException("Film not found");
        }

        films.put(film.getId(), film);

        return film;
    }

    @GetMapping
    public ArrayList<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    private void validate(Film film) throws ValidationException {
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            log.error("Film name must not be null");
            throw new ValidationException("Validation exception");
        }

        if (film.getDescription().length() >= 200) {
            log.error("Film description must not be null");
            throw new ValidationException("Validation exception");
        }

        if (film.getReleaseDate().isBefore(minDate)) {
            log.error("Film releaseDate must not be before 1895-12-28");
            throw new ValidationException("Validation exception");
        }

        if (film.getDuration() < 1) {
            log.error("Film duration must not be less than 0");
            throw new ValidationException("Validation exception");
        }
    }

    private int getId() {
        return ++id;
    }
}
