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
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DBFilmLikeStorageTest {
    private final DBFilmLikeStorage storage;

    private final DBUserStorage userStorage;

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
    public void testAddAndRemoveFilmLike() {
        User user1 = getUser();
        User user2 = getUser();
        User user3 = getUser();

        Film film1 = getFilm();
        Film film2 = getFilm();

        userStorage.addUser(user1);
        userStorage.addUser(user2);
        userStorage.addUser(user3);
        filmStorage.addFilm(film1);
        filmStorage.addFilm(film2);

        assertEquals(null, filmStorage.getPopularFilms(1));

        storage.addLike(film2.getId(), user1.getId());
        storage.addLike(film2.getId(), user2.getId());

        List<Film> films = filmStorage.getPopularFilms(2);

        assertEquals(films.get(0).getId(), film2.getId());

        storage.addLike(film1.getId(), user1.getId());
        storage.addLike(film1.getId(), user2.getId());

        storage.deleteLike(film2.getId(), user1.getId());

        films = filmStorage.getPopularFilms(2);
        assertEquals(films.get(0).getId(), film1.getId());
    }

    private User getUser() {
        return User.builder()
                .email("test@test.ru")
                .login("test")
                .name("Test")
                .birthday(LocalDate.parse("2000-01-01"))
                .build();
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

        MPA mpa = MPA.builder().id(1l).build();

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
