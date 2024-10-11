package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    void addFilm(Film film);

    void updateFilm(Film film);

    Film getFilm(Long filmId);

    List<Film> getAllFilms();

    List<Film> getPopularFilms(Integer count);

    void likeFilm(Long id, Long userId);

    void deleteLikeFilm(Long id, Long userId);
}
