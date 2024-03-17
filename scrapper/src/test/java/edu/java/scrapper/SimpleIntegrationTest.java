package edu.java.scrapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import javax.sql.DataSource;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class SimpleIntegrationTest extends IntegrationTest {

    static DataSource dataSource;
    static JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void createDataSource() {
        var driverManagerDataSource = new DriverManagerDataSource();
        driverManagerDataSource.setDriverClassName(POSTGRES.getDriverClassName());
        driverManagerDataSource.setUrl(POSTGRES.getJdbcUrl());
        driverManagerDataSource.setUsername(POSTGRES.getUsername());
        driverManagerDataSource.setPassword(POSTGRES.getPassword());
        dataSource = driverManagerDataSource;
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Test
    void schemaShouldBeCreatedCorrectly() {
        assertThatNoException().isThrownBy(() -> {
            jdbcTemplate.update("INSERT INTO tg_chat VALUES (123)");
            jdbcTemplate.update(
                "INSERT INTO link (url, type, last_polled) VALUES ('http://example.com', 'GITHUB_REPOSITORY', ?)",
                OffsetDateTime.now()
            );
            jdbcTemplate.update("INSERT INTO track_info VALUES ((SELECT link_id FROM link LIMIT(1)), 123)");
        });
    }

}
