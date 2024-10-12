package ru.yandex.practicum.filmorate.storage;

public interface FilmLikeStorage {
    void addLike(Long filmId, Long userId);

    void deleteLike(Long filmId, Long userId);
}
