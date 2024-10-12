package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

public interface GenreStorage {
    Genre getById(Long genreId);

    List<Genre> getList();

    ArrayList<Genre> getGenres(Film film);

    List<Genre> getGenresByFilm(Film film);
}
