MERGE INTO ratings
    USING (VALUES (1, 'G', 'Без возрастных ограничений.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (1, 'G', 'Без возрастных ограничений.');

MERGE INTO ratings
    USING (VALUES (2, 'PG', 'Просмотр в присутствии родителей.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (2, 'PG', 'Просмотр в присутствии родителей.');

MERGE INTO ratings
    USING (VALUES (3, 'PG_13', 'Детям до 13 лет не рекомендуется к просмотру.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (3, 'PG_13', 'Детям до 13 лет не рекомендуется к просмотру.');

MERGE INTO ratings
    USING (VALUES (4, 'R', 'Лицам до 17 лет обязательно присутствие родителей.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (4, 'R', 'Лицам до 17 лет обязательно присутствие родителей.');

MERGE INTO ratings
    USING (VALUES (5, 'NC_17', 'Лицам до 18 лет просмотр запрещен.')) s(RATING_ID ,RATING_NAME ,RATING_DESCRIPTION)
    ON ratings.RATING_ID = s.RATING_ID
    WHEN NOT MATCHED THEN INSERT VALUES (5, 'NC_17', 'Лицам до 18 лет просмотр запрещен.');
