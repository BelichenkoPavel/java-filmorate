package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Friend {
    private Long userId1;

    private Long userId2;
}
