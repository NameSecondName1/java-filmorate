package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    final Set<Long> likes = new HashSet<>();
}
