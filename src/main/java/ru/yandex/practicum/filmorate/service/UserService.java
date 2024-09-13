package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    private InMemoryUserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage userService) {
        this.userStorage = userService;
    }

    public User createUser(User user) throws ValidationException {
        validate(user);

        userStorage.addUser(user);

        return user;
    }

    public User updateUser(User user) throws ValidationException {
        validate(user);

        getUser((long) user.getId());

        userStorage.updateUser(user);

        return user;
    }

    private void validate(User user) throws ValidationException {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.size() > 0) {
            violations.forEach(error -> {
                log.error(error.getMessage());
            });

            throw new ValidationException("Validation exception");
        }

        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("Name is empty");
            user.setName(user.getLogin());
        }
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(Long userId) {
        User user = userStorage.getUser(userId);

        if (user == null) {
            log.error("User with id " + userId + " not found");
            throw new NotFoundException("User with id " + userId + " not found");
        }

        return user;
    }

    public void addFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.addFriend(friend);
        friend.addFriend(user);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void deleteFriend(Long userId, Long friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);

        user.deleteFriend(friend);
        friend.deleteFriend(user);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public ArrayList<User> getFriends(Long userId) {
        User user = getUser(userId);

        Set<Long> friends = user.getFriends();
        ArrayList<User> friendsList = new ArrayList<>();

        for (Long friendId : friends) {
            User friend = getUser(friendId);
            friendsList.add(friend);
        }

        return friendsList;
    }

    public ArrayList<User> getFriendsOfFriends(Long userId, Long otherId) {
        User user = userStorage.getUser(userId);
        User other = userStorage.getUser(otherId);

        Set<Long> friends = user.getFriends();
        Set<Long> otherFriends = other.getFriends();

        ArrayList<User> friendsList = new ArrayList<>();

        for (Long friendId : friends) {
            User friend = userStorage.getUser(friendId);
            if (otherFriends.contains(friendId)) {
                friendsList.add(friend);
            }
        }

        return friendsList;
    }
}
