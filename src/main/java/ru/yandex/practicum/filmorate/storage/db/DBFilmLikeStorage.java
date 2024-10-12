package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.sql.PreparedStatement;

@Component("dbFilmLikeStorage")
@RequiredArgsConstructor
public class DBFilmLikeStorage implements FilmLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    private String insert = "INSERT INTO \"film_like\" (FILM_ID, USER_ID) VALUES (?, ?)";

    private String delete = "DELETE from \"film_like\" WHERE FILM_ID = ? AND USER_ID = ?";

    @Override
    public void addLike(Long filmId, Long userId) {
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insert);

            ps.setLong(1, filmId);
            ps.setLong(2, userId);

            return ps;
        });
    }

    @Override
    public void deleteLike(Long filmId, Long userId) {
        jdbcTemplate.update(delete, filmId, userId);
    }
}
