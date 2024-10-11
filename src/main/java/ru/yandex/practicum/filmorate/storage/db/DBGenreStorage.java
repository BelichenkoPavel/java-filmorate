package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component("dbGenreStorage")
@RequiredArgsConstructor
public class DBGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    String SELECT_BY_ID = "SELECT * FROM \"genre\" WHERE ID = ?";

    String SELECT_LIST = "SELECT * FROM \"genre\"";

    String SELECT_GENRES_BY_FILM_ID = "SELECT * FROM \"genre\" g\n" +
            "INNER JOIN \"film_genre\" fg ON fg.GENRE_ID = g.ID\n" +
            "WHERE fg.FILM_ID = ?" +
            "ORDER BY g.ID";

    @Override
    public Genre getById(Long genreId) {
        List<Genre> result = jdbcTemplate.query(SELECT_BY_ID, this::mapToGenre, genreId);

        if (result.isEmpty()) {
            throw new NotFoundException("Genre with id " + genreId + " not found");
        }

        return result.get(0);
    }

    @Override
    public ArrayList<Genre> getGenres(Film film) {
        List<String> genresIds = new ArrayList<>();

        for (Genre genre : film.getGenres()) {
            genresIds.add(genre.getId().toString());
        }

        String inSql = String.join(",", Collections.nCopies(genresIds.size(), "?"));
        ArrayList<Genre> genres = (ArrayList<Genre>) jdbcTemplate.query(
                String.format("SELECT * FROM \"genre\" WHERE ID IN (%s)", inSql),
                this::mapToGenre,
                genresIds.toArray()
                );

        return genres;
    }

    public List<Genre> getGenresByFilm(Film film) {
        return jdbcTemplate.query(SELECT_GENRES_BY_FILM_ID, this::mapToGenre, film.getId());
    }

    @Override
    public List<Genre> getList() {
        List<Genre> result = jdbcTemplate.query(SELECT_LIST, this::mapToGenre);

        if (result.isEmpty()) {
            return null;
        }

        return result;
    }

    private Genre mapToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("ID"))
                .name(resultSet.getString("NAME"))
                .build();
    }
}
