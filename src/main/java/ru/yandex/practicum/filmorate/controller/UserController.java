package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

@RestController
@RequestMapping("/users")
public class UserController {

    private int id = 0;

    TreeMap<Integer, User> users = new TreeMap<>();
    private final Logger log = LoggerFactory.getLogger(UserController.class);

    private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

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

    private int getId() {
        return ++id;
    }
}
