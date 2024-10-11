package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DBFilmStorageTest {
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
    public void testAddFilm() {
        filmStorage.addFilm(getFilm());

        List<Film> films = filmStorage.getAllFilms();

        assertEquals(1, films.size());

        Film filmWithWrongMPA = getFilm();
        filmWithWrongMPA.setMpa(MPA.builder().id(9999L).build());

        assertThrows(ValidationException.class, () -> filmStorage.addFilm(filmWithWrongMPA));

        Film filmWithoutGenre = getFilm();
        filmWithoutGenre.setGenres(new HashSet<>());

        filmStorage.addFilm(filmWithoutGenre);

        assertEquals(0, filmWithoutGenre.getGenres().size());

        Film filmWithWrongGenre = getFilm();
        filmWithWrongGenre.getGenres().add(Genre.builder().id(9999L).build());

        assertThrows(ValidationException.class, () -> filmStorage.addFilm(filmWithWrongGenre));
    }

    @Test
    public void testUpdateFilm() {
        Film film = getFilm();
        filmStorage.addFilm(film);

        Film filmToUpdate = filmStorage.getFilm(film.getId());

        filmToUpdate.setName("Новое название");

        filmStorage.updateFilm(filmToUpdate);

        Film updatedFilm = filmStorage.getFilm(filmToUpdate.getId());

        assertEquals(filmToUpdate.getName(), updatedFilm.getName());
    }

    @Test
    public void testGetEmptyFilm() {
        Film film = filmStorage.getFilm(1L);

        assertEquals(null, film);
    }

    @Test
    public void testGetAllFilms() {
        List<Film> films = filmStorage.getAllFilms();

        assertEquals(null, films);
        filmStorage.addFilm(getFilm());
        filmStorage.addFilm(getFilm());
        filmStorage.addFilm(getFilm());

        films = filmStorage.getAllFilms();

        assertEquals(3, films.size());
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
