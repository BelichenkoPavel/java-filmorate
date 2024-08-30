package ru.yandex.practicum.filmorate.model;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    @Nullable
    private int id;

    @NotBlank(message = "name не может быть пустым")
    private String name;

    @Length(min = 1, max = 200, message = "description должен быть от 1 до 200 символов")
    @NotBlank(message = "description не может быть пустым")
    private String description;

    @NotNull(message = "release date не может быть пустым")
    private LocalDate releaseDate;

    @Min(value = 1, message = "duration не может быть меньше 1")
    private Integer duration;
}
