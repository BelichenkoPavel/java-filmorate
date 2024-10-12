package ru.yandex.practicum.filmorate.model;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;

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

    private Set<Long> friends;

    public Set<Long> getFriends() {
        if (friends == null) {
            friends = new HashSet<>();
        }

        return friends;
    }

    public void addFriend(User friend) {
        if (friends == null) {
            friends = new HashSet<>();
        }

        friends.add(friend.getId());
    }

    public void deleteFriend(User friend) {
        if (friends == null) {
            friends = new HashSet<>();
        }

        friends.remove(friend.getId());
    }
}
