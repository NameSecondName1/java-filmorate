package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
//@Builder
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private int ratingId;
    private Set<Long> likes = new HashSet<>();
    private Set<Integer> genresId;

 //   Set<String> genres = new HashSet<>();
  //  Rating rating;


    public Film (long id, String name, String description, LocalDate releaseDate, int duration, int ratingId, Set<Integer> genresId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.ratingId = ratingId;
        this.genresId = genresId;
    }

}
