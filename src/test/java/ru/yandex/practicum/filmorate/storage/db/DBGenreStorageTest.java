package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DBGenreStorageTest {
    private final DBGenreStorage genreStorage;

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
    public void testGetAllGenres() {
        List<Genre> list = genreStorage.getList();

        assertEquals(list.size(), 6);
    }

    @Test
    public void testGetById() {
        Genre genre = genreStorage.getById(1l);

        assertEquals(genre.getId(), 1l);
    }
}
