package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    InMemoryUserStorage userStorage = new InMemoryUserStorage();
    UserService userService = new UserService(userStorage);
    UserController userController = new UserController(userService);

    User user = getUser();

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(userStorage);
        userController = new UserController(userService);

        user = getUser();
    }

    @Test
    public void testCreateUser() throws ValidationException {
        ArrayList<User> users = userController.getUsers();
        assertEquals(0, users.size(), "Список пользователей должен быть пустым");

        userController.createUser(user);

        users = userController.getUsers();
        assertEquals(1, users.size(), "Количество пользователей должно быть 1");

        user = getUser();
        user.setEmail(" ");

        assertThrows(ValidationException.class, () -> userController.createUser(user), "Невозможно создать пользователя без email");

        user = getUser();
        user.setEmail("test.tu");

        assertThrows(ValidationException.class, () -> userController.createUser(user), "Невозможно создать пользователя с неправильным email");

        user = getUser();
        user.setLogin(" ");

        assertThrows(ValidationException.class, () -> userController.createUser(user), "Невозможно создать пользователя с пустым login");

        user = getUser();
        user.setLogin("login login");

        assertThrows(ValidationException.class, () -> userController.createUser(user), "Невозможно создать пользователя с логином содержащим пустые символы");

        user = getUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> userController.createUser(user), "Невозможно создать пользователя с датой рождения в будущем");

        users = userController.getUsers();
        assertEquals(1, users.size(), "Количество пользователей должно быть 1");

        user = getUser();
        user.setName(null);

        userController.createUser(user);
        users = userController.getUsers();

        assertEquals(user.getName(), users.get(1).getLogin(), "Имя пользователя должно быть равно его логину");
    }

    @Test
    public void testGetUsers() throws ValidationException {
        ArrayList<User> users = userController.getUsers();
        assertEquals(0, users.size(), "Список пользователей должен быть пустым");

        userController.createUser(user);
        userController.createUser(user);
        userController.createUser(user);

        users = userController.getUsers();
        assertEquals(3, users.size(), "Количество пользователей должно быть 1");
    }

    @Test
    public void testUpdateUser() throws ValidationException {
        userController.createUser(user);

        ArrayList<User> users = userController.getUsers();
        assertEquals(user.getName(), users.get(0).getName(), "Имена пользователя должны быть равны");

        user.setName("User name2");
        userController.updateUser(user);

        users = userController.getUsers();
        assertEquals("User name2", users.get(0).getName(), "Имена пользователя должны быть равны");

        user = getUser();
        user.setId(9999);
        assertThrows(NotFoundException.class, () -> userController.updateUser(user), "Невозможно обновить несуществующего пользователя");

    }

    private User getUser() {
        return User.builder()
                .email("test@test.ru")
                .login("mylogin")
                .name("User name")
                .birthday(LocalDate.of(2000, 1, 1))
                .build();
    }
}
