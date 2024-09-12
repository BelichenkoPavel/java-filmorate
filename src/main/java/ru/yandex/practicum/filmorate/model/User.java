package ru.yandex.practicum.filmorate.model;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private int id;

    @NotNull(message = "email обязательно")
    @Pattern(regexp = "^([\\w+._-]+@[\\w._-]+\\.[\\w_-]+)$", message = "email не соответствует формату")
    private String email;

    @NotBlank(message = "логин не может быть пустым")
    @Pattern(regexp = "^[\\w]+$", message = "login не может содержать знаки пробелов")
    private String login;

    @Nullable
    private String name;

    @PastOrPresent(message = "дата рождения не может быть в будущем")
    @NotNull(message = "дата рождения обязательна")
    private LocalDate birthday;
}
