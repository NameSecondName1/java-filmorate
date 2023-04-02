package ru.yandex.practicum.filmorate.model;

public class Rating {
    private int id;
    private RatingsMPA name;

    public Rating(int id, RatingsMPA name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RatingsMPA getName() {
        return name;
    }

    public void setName(RatingsMPA name) {
        this.name = name;
    }
}
