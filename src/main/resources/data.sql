MERGE INTO ratings
    USING (VALUES (1, 'G', 'Без возрастных ограничений.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'G', 'Без возрастных ограничений.');

MERGE INTO ratings
    USING (VALUES (2, 'PG', 'Просмотр в присутствии родителей.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'PG', 'Просмотр в присутствии родителей.');

MERGE INTO ratings
    USING (VALUES (3, 'PG-13', 'Детям до 13 лет не рекомендуется к просмотру.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'PG-13', 'Детям до 13 лет не рекомендуется к просмотру.');

MERGE INTO ratings
    USING (VALUES (4, 'R', 'Лицам до 17 лет обязательно присутствие родителей.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (4, 'R', 'Лицам до 17 лет обязательно присутствие родителей.');

MERGE INTO ratings
    USING (VALUES (5, 'NC-17', 'Лицам до 18 лет просмотр запрещен.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (5, 'NC-17', 'Лицам до 18 лет просмотр запрещен.');


MERGE INTO genres
    USING (VALUES (1, 'Комедия')) s(GENRE_ID, GENRE_NAME)
    ON genres.GENRE_ID = s.GENRE_ID
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'Комедия');

MERGE INTO genres
    USING (VALUES (2, 'Драма')) s(GENRE_ID, GENRE_NAME)
    ON genres.GENRE_ID = s.GENRE_ID
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'Драма');

MERGE INTO genres
    USING (VALUES (3, 'Мультфильм')) s(GENRE_ID, GENRE_NAME)
    ON genres.GENRE_ID = s.GENRE_ID
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'Мультфильм');

MERGE INTO genres
    USING (VALUES (4, 'Триллер')) s(GENRE_ID, GENRE_NAME)
    ON genres.GENRE_ID = s.GENRE_ID
    WHEN NOT MATCHED THEN INSERT VALUES (4, 'Триллер');

MERGE INTO genres
    USING (VALUES (5, 'Документальный')) s(GENRE_ID, GENRE_NAME)
    ON genres.GENRE_ID = s.GENRE_ID
    WHEN NOT MATCHED THEN INSERT VALUES (5, 'Документальный');

MERGE INTO genres
    USING (VALUES (6, 'Боевик')) s(GENRE_ID, GENRE_NAME)
    ON genres.GENRE_ID = s.GENRE_ID
    WHEN NOT MATCHED THEN INSERT VALUES (6, 'Боевик');





/*MERGE INTO users
    USING (VALUES (1, 'UserName1', 'Login1', 'email1@email.com', '1990-05-05')) s(ID, NAME , LOGIN, EMAIL, BIRTHDAY)
    ON users.ID = s.ID
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'UserName1', 'Login1', 'email1@email.com', '1990-05-05');

MERGE INTO users
    USING (VALUES (2, 'UserName2', 'Login2', 'email2@email.com', '1990-06-06')) s(ID, NAME , LOGIN, EMAIL, BIRTHDAY)
    ON users.ID = s.ID
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'UserName2', 'Login2', 'email2@email.com', '1990-06-06');

MERGE INTO users
    USING (VALUES (3, 'UserName3', 'Login3', 'email3@email.com', '1990-07-07')) s(ID, NAME , LOGIN, EMAIL, BIRTHDAY)
    ON users.ID = s.ID
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'UserName3', 'Login3', 'email3@email.com', '1990-07-07');



MERGE INTO films
    USING (VALUES (1, 'Матрица', 'Классный фильм', '1999-10-14', 120, 4)) s(ID, NAME , DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
    ON films.ID = s.ID
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'Матрица', 'Классный фильм', '1999-10-14', 120, 4);

MERGE INTO films
    USING (VALUES (2, 'Матрица2', 'Классный фильм2', '1899-10-14', 120, 4)) s(ID, NAME , DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID)
    ON films.ID = s.ID
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'Матрица2', 'Классный фильм2', '1899-10-14', 120, 4);*/