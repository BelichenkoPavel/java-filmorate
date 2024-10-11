package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DBFilmGenreStorageTest {
    private final DBFilmGenreStorage filmGenreStorage;

    private final DBFilmStorage filmStorage;

    private final JdbcTemplate jdbcTemplate;

    @AfterEach
    public void tearDown() {
        jdbcTemplate.update("DELETE FROM \"film_genre\"");
        jdbcTemplate.update("DELETE FROM \"film_like\"");
        jdbcTemplate.update("DELETE FROM \"friend\"");
        jdbcTemplate.update("DELETE FROM \"user\"");
        jdbcTemplate.update("DELETE FROM \"film\"");
    }

    @Test
    public void testCreateFilmGenre() {
        Film film = getFilm();

        filmStorage.addFilm(film);
        filmGenreStorage.addFilmGenres(film);

        Set<Genre> filmWithGenres = filmStorage.getFilm(film.getId()).getGenres();
        assert filmWithGenres.size() == 2;
        assert filmWithGenres.contains(Genre
                .builder()
                .id(1L)
                .name("Комедия")
                .build());
        assert filmWithGenres.contains(Genre
                .builder()
                .id(2L)
                .name("Драма")
                .build());
    }

    private Film getFilm() {
        Genre genre1 = Genre
                .builder()
                .id(1L)
                .build();

        Genre genre2 = Genre
                .builder()
                .id(2L)
                .build();

        HashSet<Genre> genres = new HashSet<>();
        genres.add(genre1);
        genres.add(genre2);

        MPA mpa = MPA.builder().id(1L).build();

        return Film.builder()
                .name("Звёздные войны: Эпизод 4 – Новая надежда")
                .description("Coming to your galaxy this summer")
                .releaseDate(LocalDate.of(1977, 1, 1))
                .duration(121)
                .mpa(mpa)
                .genres(genres)
                .build();
    }
}
