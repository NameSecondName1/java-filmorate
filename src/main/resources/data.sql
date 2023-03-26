MERGE INTO PUBLIC."rating"
USING (VALUES (1, 'G', 'Без возрастных ограничений.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
ON PUBLIC."rating".RATING_ID = s.RATING_ID
WHEN NOT MATCHED THEN INSERT VALUES (1, 'G', 'Без возрастных ограничений.');

MERGE INTO PUBLIC."rating"
USING (VALUES (2, 'PG', 'Просмотр в присутствии родителей.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
ON PUBLIC."rating".RATING_ID = s.RATING_ID
WHEN NOT MATCHED THEN INSERT VALUES (2, 'PG', 'Просмотр в присутствии родителей.');

MERGE INTO PUBLIC."rating"
USING (VALUES (3, 'PG_13', 'Детям до 13 лет не рекомендуется к просмотру.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
ON PUBLIC."rating".RATING_ID = s.RATING_ID
WHEN NOT MATCHED THEN INSERT VALUES (3, 'PG_13', 'Детям до 13 лет не рекомендуется к просмотру.');

MERGE INTO PUBLIC."rating"
USING (VALUES (4, 'R', 'Лицам до 17 лет обязательно присутствие родителей.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
ON PUBLIC."rating".RATING_ID = s.RATING_ID
WHEN NOT MATCHED THEN INSERT VALUES (4, 'R', 'Лицам до 17 лет обязательно присутствие родителей.');

MERGE INTO PUBLIC."rating"
USING (VALUES (5, 'NC_17', 'Лицам до 18 лет просмотр запрещен.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
ON PUBLIC."rating".RATING_ID = s.RATING_ID
WHEN NOT MATCHED THEN INSERT VALUES (5, 'NC_17', 'Лицам до 18 лет просмотр запрещен.');


MERGE INTO PUBLIC."genre"
USING (VALUES (1, 'ужасы')) s(GENRE_ID, GENRE_NAME)
ON PUBLIC."genre".GENRE_ID = s.GENRE_ID
WHEN NOT MATCHED THEN INSERT VALUES (1, 'ужасы');

MERGE INTO PUBLIC."genre"
USING (VALUES (2, 'комедия')) s(GENRE_ID, GENRE_NAME)
ON PUBLIC."genre".GENRE_ID = s.GENRE_ID
WHEN NOT MATCHED THEN INSERT VALUES (2, 'комедия');

MERGE INTO PUBLIC."genre"
USING (VALUES (3, 'драма')) s(GENRE_ID, GENRE_NAME)
ON PUBLIC."genre".GENRE_ID = s.GENRE_ID
WHEN NOT MATCHED THEN INSERT VALUES (3, 'драма');

MERGE INTO PUBLIC."genre"
USING (VALUES (4, 'мультфильм')) s(GENRE_ID, GENRE_NAME)
ON PUBLIC."genre".GENRE_ID = s.GENRE_ID
 WHEN NOT MATCHED THEN INSERT VALUES (4, 'мультфильм');

MERGE INTO PUBLIC."genre"
USING (VALUES (5, 'документальный')) s(GENRE_ID, GENRE_NAME)
ON PUBLIC."genre".GENRE_ID = s.GENRE_ID
WHEN NOT MATCHED THEN INSERT VALUES (5, 'документальный');

MERGE INTO PUBLIC."genre"
USING (VALUES (6, 'боевик')) s(GENRE_ID, GENRE_NAME)
ON PUBLIC."genre".GENRE_ID = s.GENRE_ID
WHEN NOT MATCHED THEN INSERT VALUES (6, 'боевик');

MERGE INTO PUBLIC."genre"
USING (VALUES (7, 'фантастика')) s(GENRE_ID, GENRE_NAME)
ON PUBLIC."genre".GENRE_ID = s.GENRE_ID
WHEN NOT MATCHED THEN INSERT VALUES (7, 'фантастика');


MERGE INTO PUBLIC."user"
USING (VALUES (1, 'UserName1', 'Login1', 'email1@email.com', '1990-05-05')) s(ID, NAME , LOGIN, EMAIL, BIRTHDAY)
ON PUBLIC."user".ID = s.ID
WHEN NOT MATCHED THEN INSERT VALUES (1, 'UserName1', 'Login1', 'email1@email.com', '1990-05-05');

MERGE INTO PUBLIC."user"
USING (VALUES (2, 'UserName2', 'Login2', 'email2@email.com', '1990-06-06')) s(ID, NAME , LOGIN, EMAIL, BIRTHDAY)
ON PUBLIC."user".ID = s.ID
WHEN NOT MATCHED THEN INSERT VALUES (2, 'UserName2', 'Login2', 'email2@email.com', '1990-06-06');

MERGE INTO PUBLIC."user"
USING (VALUES (3, 'UserName3', 'Login3', 'email3@email.com', '1990-07-07')) s(ID, NAME , LOGIN, EMAIL, BIRTHDAY)
ON PUBLIC."user".ID = s.ID
WHEN NOT MATCHED THEN INSERT VALUES (3, 'UserName3', 'Login3', 'email3@email.com', '1990-07-07');



MERGE INTO PUBLIC."film"
USING (VALUES (1, 'Матрица', 'Классный фильм', '1999-10-14', 120, 4)) s(ID, NAME , DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
ON PUBLIC."film".ID = s.ID
WHEN NOT MATCHED THEN INSERT VALUES (1, 'Матрица', 'Классный фильм', '1999-10-14', 120, 4);


MERGE INTO PUBLIC."film_genres"
USING (VALUES (1, 1, 6)) s(FILM_GENRES_ID, FILM_ID, GENRE_ID)
ON PUBLIC."film_genres".FILM_GENRES_ID  = s.FILM_GENRES_ID
WHEN NOT MATCHED THEN INSERT VALUES (1, 1, 6);

MERGE INTO PUBLIC."film_genres"
USING (VALUES (2, 1, 7)) s(FILM_GENRES_ID, FILM_ID, GENRE_ID)
ON PUBLIC."film_genres".FILM_GENRES_ID  = s.FILM_GENRES_ID
WHEN NOT MATCHED THEN INSERT VALUES (2, 1, 7);




















