CREATE TABLE IF NOT EXISTS users (
id long  PRIMARY KEY,
name varchar (100),
login varchar (100),
email varchar (200),
birthday timestamp
);

CREATE TABLE IF NOT EXISTS friendships (
friendship_id long GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
from_id long REFERENCES users (id),
to_id long REFERENCES users (id),
friendship_status varchar(20)
);

CREATE TABLE IF NOT EXISTS ratings (
rating_id int PRIMARY KEY,
rating_name varchar (100) NOT NULL,
rating_description varchar(500)
);


CREATE TABLE IF NOT EXISTS films (
id long PRIMARY KEY,
name varchar(100),
description varchar (200),
release_date timestamp,
duration int,
rating_id int REFERENCES ratings (rating_id)
);

CREATE TABLE IF NOT EXISTS genres (
genre_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
genre_name varchar(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
film_genres_id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
film_id long REFERENCES films (id),
genre_id int REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS likes (
likes_id long GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
film_id long REFERENCES films (id),
user_id long REFERENCES users (id)
);
