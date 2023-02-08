MERGE INTO PUBLIC."rating"
USING (VALUES (1, 'G', 'Без возрастных ограничений.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
ON PUBLIC."rating".RATING_ID = s.RATING_ID
WHEN NOT MATCHED THEN INSERT VALUES (1, 'G', 'Без возрастных ограничений.');

INSERT INTO PUBLIC."rating" (rating_id, rating_name, rating_description)
VALUES (2, 'PG', 'Просмотр в присутствии родителей.');

INSERT INTO PUBLIC."rating" (rating_id, rating_name, rating_description)
VALUES (3, 'PG_13', 'Детям до 13 лет не рекомендуется к просмотру.');

INSERT INTO PUBLIC."rating" (rating_id, rating_name, rating_description)
VALUES (4, 'R', 'Лицам до 17 лет обязательно присутствие родителей.');

INSERT INTO PUBLIC."rating" (rating_id, rating_name, rating_description)
VALUES (5, 'NC_17', 'Лицам до 18 лет просмотр запрещен.');

INSERT INTO PUBLIC."genre" (genre_id, GENRE_NAME)
VALUES (1, 'ужасы');

INSERT INTO PUBLIC."genre" (genre_id, GENRE_NAME)
VALUES (2, 'комедия');

INSERT INTO PUBLIC."genre" (genre_id, GENRE_NAME)
VALUES (3, 'драма');

INSERT INTO PUBLIC."genre" (genre_id, GENRE_NAME)
VALUES (4, 'мультфильм');

INSERT INTO PUBLIC."genre" (genre_id, GENRE_NAME)
VALUES (5, 'документальный');

INSERT INTO PUBLIC."genre" (genre_id, GENRE_NAME)
VALUES (6, 'боевик');

INSERT INTO PUBLIC."genre" (genre_id, GENRE_NAME)
VALUES (7, 'фантастика');


INSERT INTO PUBLIC."user" (id, NAME, LOGIN, EMAIL, BIRTHDAY)
VALUES (1, 'UserName1', 'Login1', 'email1@email.com', '1990-05-05');

INSERT INTO PUBLIC."user" (id, NAME, LOGIN, EMAIL, BIRTHDAY)
VALUES (2, 'UserName2', 'Login2', 'email2@email.com', '1990-06-06');


INSERT INTO PUBLIC."film" (id, NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
VALUES (1, 'Матрица', 'Классный фильм', '1999-10-14', 120, 4);

INSERT INTO PUBLIC."film_genres" (FILM_ID, GENRE_ID)
VALUES (1, 6);

INSERT INTO PUBLIC."film_genres" (FILM_ID, GENRE_ID)
VALUES (1, 7);