package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    void addUser(User user);

    User getUser(Long userId);

    void updateUser(User user);

    List<User> getUsers();
}
