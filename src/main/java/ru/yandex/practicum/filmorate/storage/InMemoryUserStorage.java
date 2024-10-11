package ru.yandex.practicum.filmorate.storage;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

@Component
@Qualifier("inMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private long id = 0;

    private TreeMap<Long, User> users = new TreeMap<>();

    @Override
    public void addUser(User user) {
        user.setId(getId());
        users.put(user.getId(), user);
    }

    @Override
    public User getUser(Long userId) {
        return users.get(userId);
    }

    @Override
    public void updateUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public void addFriend(User user, User friend) {
        user.addFriend(friend);
        updateUser(user);
    }

    public void deleteFriend(User user, User friend) {
        user.deleteFriend(friend);
        friend.deleteFriend(user);
        updateUser(user);
        updateUser(friend);
    }

    public ArrayList<User> getFriends(User user) {
        Set<Long> friends = user.getFriends();
        ArrayList<User> friendsList = new ArrayList<>();

        for (Long friendId : friends) {
            User friend = getUser(friendId);
            friendsList.add(friend);
        }

        return friendsList;
    }

    public ArrayList<User> getFriendsOfFriends(User user, User friend) {
        ArrayList<User> friends = getFriends(user);
        ArrayList<User> otherFriends = getFriends(friend);

        ArrayList<User> friendsList = new ArrayList<>();

        for (User friendItem : friends) {
            long id = friendItem.getId();
            User other = getUser(id);
            if (otherFriends.contains(id)) {
                friendsList.add(other);
            }
        }

        return friendsList;
    }

    private long getId() {
        return ++id;
    }
}
