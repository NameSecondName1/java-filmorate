package ru.yandex.practicum.filmorate.model;

import java.util.Objects;

public class Rating {
    private int id;
   private String name;

    public Rating(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public Rating(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return id == rating.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
