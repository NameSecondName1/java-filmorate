package ru.yandex.practicum.filmorate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

public class TestDataSource {
    private static final String DB_URL = "jdbc:h2:mem:test;MODE=PostgreSQL";
    private static final String DB_USERNAME = "sa";
    private static final String DB_PASSWORD = "";

    public static JdbcTemplate getJdbcTemplate() throws SQLException {
        DataSource dataSource = getDataSource();
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        createTables(jdbcTemplate);
        return jdbcTemplate;
    }

    private static DataSource getDataSource() throws SQLException {
        return new DriverManagerDataSource(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    private static void createTables(JdbcTemplate jdbcTemplate) {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS ratings (rating_id INTEGER PRIMARY KEY, rating_name VARCHAR(255));");
        jdbcTemplate.execute("INSERT INTO ratings (rating_id, rating_name) VALUES (1, 'G'), (2, 'PG'), (3, 'PG-13'), (4, 'R'), (5, 'NC-17');");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS genres (genre_id INTEGER PRIMARY KEY, genre_name VARCHAR(255));");
        jdbcTemplate.execute("INSERT INTO genres (genre_id, genre_name) VALUES (1, 'Action'), (2, 'Comedy'), (3, 'Drama'), (4, 'Horror'), (5, 'Sci-Fi');");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS films (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(255), description VARCHAR(1024), release_date DATE, duration INTEGER, rating_id INTEGER, FOREIGN KEY (rating_id) REFERENCES ratings (rating_id));");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS film_genres (film_id BIGINT, genre_id INTEGER, FOREIGN KEY (film_id) REFERENCES films (id), FOREIGN KEY (genre_id) REFERENCES genres (genre_id));");
    }
}
