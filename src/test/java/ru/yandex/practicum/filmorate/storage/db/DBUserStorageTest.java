package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DBUserStorageTest {
    private final DBUserStorage userStorage;

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
    public void testAddUser() {
        User user = getUser();
        userStorage.addUser(user);

        User userFromStorage = userStorage.getUser(user.getId());
        assertEquals(user.getId(), userFromStorage.getId());

        userStorage.addUser(getUser());

        assertEquals(2, userStorage.getUsers().size());
    }

    @Test
    public void testUpdateUser() {
        User user = getUser();
        userStorage.addUser(user);

        user.setName("Test2");
        userStorage.updateUser(user);

        User userFromStorage = userStorage.getUser(user.getId());
        assertEquals(user.getName(), userFromStorage.getName());
    }

    private User getUser() {
        return User.builder()
                .email("test@test.ru")
                .login("test")
                .name("Test")
                .birthday(LocalDate.parse("2000-01-01"))
                .build();
    }
}
