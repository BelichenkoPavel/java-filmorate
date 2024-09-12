package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

public interface FilmStorage {
    void addFilm(Film film);

    void updateFilm(Film film);

    Film getFilm(Long filmId);

    ArrayList<Film> getAllFilms();
}
