package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

public interface UserStorage {
    void addUser(User user);

    User getUser(Long userId);

    void updateUser(User user);

    List<User> getUsers();

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    ArrayList<User>  getFriends(User user);

    ArrayList<User> getFriendsOfFriends(User user, User friend);
}
