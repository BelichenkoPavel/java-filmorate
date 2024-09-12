package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.TreeMap;

@Component
public class InMemoryUserStorage implements UserStorage {

    private int id = 0;

    TreeMap<Integer, User> users = new TreeMap<>();

    @Override
    public void addUser(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
    }

    @Override
    public User getUser(Long userId) {
        return users.get(userId.intValue());
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private int getId() {
        return ++id;
    }
}
