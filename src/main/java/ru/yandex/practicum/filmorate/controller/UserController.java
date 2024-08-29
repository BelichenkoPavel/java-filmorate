package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.TreeMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private int id = 0;

    TreeMap<Integer, User> users = new TreeMap<>();
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public User createUser(@RequestBody User user) throws ValidationException {
        validate(user);
        user.setId(getId());
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws ValidationException {
        validate(user);

        if (!users.containsKey(user.getId())) {
            log.error("User not found");
            throw new ValidationException("User not found");
        }

        users.put(user.getId(), user);

        return user;
    }

    @GetMapping
    public ArrayList<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    private void validate(User user) throws ValidationException {
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            log.error("Name is empty");
            throw new ValidationException("Name is empty");
        }

        if (user.getEmail().isEmpty() || user.getEmail().isBlank()) {
            log.error("Email is empty");
            throw new ValidationException("Email is empty");
        }

        if (!user.getEmail().contains("@")) {
            log.error("Email must contain @");
            throw new ValidationException("Email is invalid");
        }

        if (user.getLogin().isEmpty() || user.getLogin().isBlank()) {
            log.error("Login is empty");
            throw new ValidationException("Login is empty");
        }

        if (user.getLogin().contains(" ")) {
            log.error("Login contains spaces");
            throw new ValidationException("Login contains spaces");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("birthday is after today");
            throw new ValidationException("Validation exception");
        }
    }

    private int getId() {
        return ++id;
    }
}
