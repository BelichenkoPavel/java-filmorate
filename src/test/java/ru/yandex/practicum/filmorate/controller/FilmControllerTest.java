package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    InMemoryUserStorage userStorage = new InMemoryUserStorage();
    UserService userService = new UserService(userStorage);

    InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
    FilmService filmService = new FilmService(filmStorage, userService);
    FilmController filmController = new FilmController(filmService);

    Film film = getFilm();

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);

        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userService);
        filmController = new FilmController(filmService);
        film = getFilm();
    }

    @Test
    public void testAddFilm() throws ValidationException {
        List<Film> films = filmController.getFilms();

        assertEquals(0, films.size(), "Список должен быть пустым");

        filmController.addFilm(film);

        films = filmController.getFilms();
        assertEquals(1, films.size(), "Список не должен быть пустым");

        film = getFilm();
        film.setName(" ");
        assertThrows(ValidationException.class, () -> filmController.addFilm(film), "Название не должно быть пустым");

        film = getFilm();
        film.setDescription("1".repeat(201));
        assertThrows(ValidationException.class, () -> filmController.addFilm(film), "Описание должно быть меннее 200 символов");

        film = getFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> filmController.addFilm(film), "Дата выхода фильма должна быть позднее 1895-12-28");

        film = getFilm();
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> filmController.addFilm(film), "Продолжительность фильма должна быть больше 0");

        films = filmController.getFilms();
        assertEquals(1, films.size(), "Список не должен быть пустым");
    }

    @Test
    public void testGetFilms() throws ValidationException {
        List<Film> films = filmController.getFilms();
        assertEquals(0, films.size(), "Список должен быть пустым");

        filmController.addFilm(film);
        filmController.addFilm(film);
        filmController.addFilm(film);

        films = filmController.getFilms();
        assertEquals(3, films.size(), "Список не должен быть пустым");
    }

    @Test
    public void testUpdateFilm() throws ValidationException {
        filmController.addFilm(film);

        List<Film> films = filmController.getFilms();
        assertEquals(film.getName(), films.get(0).getName(), "Названия должны совпадать");

        film.setName("Звёздные войны: Эпизод 5 – Империя наносит ответный удар");
        filmController.updateFilm(film);

        films = filmController.getFilms();
        assertEquals("Звёздные войны: Эпизод 5 – Империя наносит ответный удар", films.get(0).getName(), "Названия должны совпадать");

        film = getFilm();
        film.setId(9999);
        assertThrows(NotFoundException.class, () -> filmController.updateFilm(film), "Невозможно обновить несуществующий фильм");
    }

    private Film getFilm() {
        return Film.builder()
                .name("Звёздные войны: Эпизод 4 – Новая надежда")
                .description("Coming to your galaxy this summer")
                .releaseDate(LocalDate.of(1977, 1, 1))
                .duration(121)
                .build();
    }
}
