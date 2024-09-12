package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.TreeMap;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private TreeMap<Integer, Film> films = new TreeMap<>();

    private int id = 0;

    @Override
    public void addFilm(Film film) {
        film.setId(getId());

        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        films.put(film.getId(), film);
    }

    @Override
    public Film getFilm(Long filmId) {
        return films.get(filmId.intValue());
    }

    @Override
    public ArrayList<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    private int getId() {
        return ++id;
    }
}
