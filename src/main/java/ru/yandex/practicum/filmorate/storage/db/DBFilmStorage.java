package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("dbFilmStorage")
@RequiredArgsConstructor
public class DBFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    private final DBMPAStorage dbmpaStorage;

    private final DBFilmGenreStorage dbFilmGenreStorage;

    private final DBGenreStorage dbGenreStorage;

    private final DBFilmLikeStorage dbFilmLikeStorage;

    private String selectById = "SELECT * FROM \"film\" WHERE ID = ?";

    private String selectList = "SELECT * FROM \"film\"";

    private String insert = "INSERT INTO \"film\" (name, description, release_date, duration, mpa_id) VALUES (?, ?, ?, ?, ?)";

    private String update = "UPDATE \"film\" SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? WHERE ID = ?";

    private String getPopularFilms = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, f.MPA_ID  FROM \"film\" f \n" +
            "INNER JOIN \"film_like\" fl ON fl.FILM_ID = f.ID\n" +
            "GROUP BY fl.FILM_ID\n" +
            "ORDER BY count(fl.FILM_ID) DESC\n" +
            "LIMIT ?";

    @Override
    public void addFilm(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        MPA mpa = dbmpaStorage.getById(film.getMpa().getId());

        if (mpa == null) {
            throw new ValidationException("MPA with id = " + film.getMpa().getId() + "not found");
        }

        List<Genre> genres;
        if (film.getGenres() != null) {
            genres = dbGenreStorage.getGenres(film);

            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Genres not found");
            }
        } else {
            film.setGenres(new HashSet<>());
            genres = new ArrayList<>();
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insert, new String[]{"ID"});

            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());

            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        dbFilmGenreStorage.addFilmGenres(film);

        film.setMpa(mpa);
        film.setGenres(new LinkedHashSet<>(genres));
    }

    @Override
    public void updateFilm(Film film) {
        MPA mpa = dbmpaStorage.getById(film.getMpa().getId());

        if (mpa == null) {
            throw new ValidationException("MPA with id = " + film.getMpa().getId() + "not found");
        }

        List<Genre> genres;
        if (film.getGenres() != null) {
            genres = dbGenreStorage.getGenres(film);

            if (genres.size() != film.getGenres().size()) {
                throw new ValidationException("Genres not found");
            }
        } else {
            film.setGenres(new HashSet<>());
            genres = new ArrayList<>();
        }

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(update);

            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            ps.setLong(6, film.getId());

            return ps;
        });

        dbFilmGenreStorage.addFilmGenres(film);

        film.setMpa(mpa);
        film.setGenres(new HashSet<>(genres));
    }

    @Override
    public Film getFilm(Long filmId) {
        List<Film> result = jdbcTemplate.query(selectById, this::mapToFilm, filmId);

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> result = jdbcTemplate.query(selectList, this::mapToFilm);

        if (result.isEmpty()) {
            return null;
        }

        return result;
    }

    private Film mapToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        long mpaId = resultSet.getLong("MPA_ID");

        MPA mpa = dbmpaStorage.getById(mpaId);

        Film film = Film.builder()
                .id(resultSet.getLong("ID"))
                .name(resultSet.getString("NAME"))
                .description(resultSet.getString("DESCRIPTION"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .duration(resultSet.getInt("DURATION"))
                .mpa(mpa)
                .build();

        List <Genre> genres = dbGenreStorage.getGenresByFilm(film);
        film.setGenres(new HashSet<>(genres));

        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        List<Film> result = jdbcTemplate.query(getPopularFilms, this::mapToFilm, count);

        if (result.isEmpty()) {
            return null;
        }

        return result;
    }

    @Override
    public void likeFilm(Long id, Long userId) {
        dbFilmLikeStorage.addLike(id, userId);
    }

    @Override
    public void deleteLikeFilm(Long id, Long userId) {
        dbFilmLikeStorage.deleteLike(id, userId);
    }
}
