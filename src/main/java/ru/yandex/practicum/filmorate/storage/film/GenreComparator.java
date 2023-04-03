package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;

public class GenreComparator implements Comparator<Genre> {
    @Override
    public int compare(Genre genre1, Genre genre2) {
        return genre1.getId() - genre2.getId();
    }
}
