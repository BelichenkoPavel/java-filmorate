package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.db.DBGenreStorage;

import java.util.List;

@Service
public class GenreService {
    private GenreStorage genreStorage;

    public GenreService(DBGenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getById(Long id) {
        return genreStorage.getById(id);
    }

    public List<Genre> getList() {
        return genreStorage.getList();
    }
}
