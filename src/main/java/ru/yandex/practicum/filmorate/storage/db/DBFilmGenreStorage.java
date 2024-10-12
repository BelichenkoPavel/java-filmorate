package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Component("dbFilmGenreStorage")
@RequiredArgsConstructor
public class DBFilmGenreStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    private String insert = "INSERT INTO \"film_genre\" (FILM_ID, GENRE_ID) VALUES (?, ?)";

    private String delete = "DELETE from \"film_genre\" WHERE FILM_ID = ?";

    @Override
    public void addFilmGenres(Film film) {
        jdbcTemplate.update(delete, film.getId());

        jdbcTemplate.batchUpdate(
                insert,
                new AddGenresPreparedStatementSetter(film.getGenres().stream().toList(), film.getId())
        );
    }

    class AddGenresPreparedStatementSetter implements BatchPreparedStatementSetter {
        private List<Genre> genreSet;

        private Long filmId;

        AddGenresPreparedStatementSetter(List<Genre> genreSet, Long filmId) {
            this.genreSet = genreSet;
            this.filmId = filmId;
        }

        @Override
        public void setValues(PreparedStatement ps, int i) throws SQLException {
            Genre genre = genreSet.get(i);

            ps.setLong(1, filmId);
            ps.setLong(2, genre.getId());
        }

        @Override
        public int getBatchSize() {
            return genreSet.size();
        }
    }
}
