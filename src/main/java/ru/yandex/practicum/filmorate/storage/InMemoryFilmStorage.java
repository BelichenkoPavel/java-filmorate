package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Component("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private TreeMap<Long, Film> films = new TreeMap<>();

    private long id = 0;

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
        return films.get(filmId);
    }

    @Override
    public ArrayList<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void likeFilm(Long id, Long userId) {
        Film film = getFilm(id);

        film.addLike(userId);

        updateFilm(film);
    }

    @Override
    public void deleteLikeFilm(Long id, Long userId) {
        Film film = getFilm(id);

        film.removeLike(userId);

        updateFilm(film);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Film> films = getAllFilms();

        films.sort((film1, film2) -> {
            int likes1 = film1.getLikes().size();
            int likes2 = film2.getLikes().size();

            return likes2 - likes1;
        });

        if (count > films.size()) {
            count = films.size();
        }

        return films.subList(0, count);
    }

    private long getId() {
        return ++id;
    }
}
