package ru.yandex.practicum.filmorate.storage.Rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface RatingsStorage {

    List<Rating> getRatings();
    Rating getRatingById(int id);
}
