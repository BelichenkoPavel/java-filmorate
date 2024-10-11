package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.PreparedStatement;

@Component("dbFilmGenreStorage")
@RequiredArgsConstructor
public class DBFilmGenreStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    private String insert = "INSERT INTO \"film_genre\" (FILM_ID, GENRE_ID) VALUES (?, ?)";

    private String delete = "DELETE from \"film_genre\" WHERE FILM_ID = ?";

    @Override
    public void addFilmGenres(Film film) {
        jdbcTemplate.update(delete, film.getId());

        for (Genre genre: film.getGenres()) {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(insert);

                ps.setLong(1, film.getId());
                ps.setLong(2, genre.getId());

                return ps;
            });
        }
    }
}
