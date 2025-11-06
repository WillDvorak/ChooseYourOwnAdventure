package com.textquest.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=true",
    "spring.flyway.baseline-on-migrate=true",
    "spring.flyway.validate-on-migrate=false"
})
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void contextLoads() {
        // Test that Spring Boot application context loads without errors
        assertNotNull(dataSource);
        assertNotNull(jdbcTemplate);
    }

    @Test
    void databaseConnectionWorks() throws SQLException {
        // Test that we can establish a connection to the database
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        }
    }

    @Test
    void flywayMigrationsRun() {
        // Test that Flyway migrations ran and created the expected tables
        String scenesTableExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'textquest' AND table_name = 'scenes'",
            String.class
        );
        assertEquals("1", scenesTableExists, "Scenes table should exist");

        String choicesTableExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'textquest' AND table_name = 'choices'",
            String.class
        );
        assertEquals("1", choicesTableExists, "Choices table should exist");

        String gameSessionsTableExists = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'textquest' AND table_name = 'game_sessions'",
            String.class
        );
        assertEquals("1", gameSessionsTableExists, "Game_sessions table should exist");
    }

    @Test
    void seedDataLoaded() {
        // Test that seed data was loaded correctly
        Integer sceneCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM scenes", Integer.class);
        assertTrue(sceneCount > 0, "Should have at least one scene from seed data");

        Integer choiceCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM choices", Integer.class);
        assertTrue(choiceCount > 0, "Should have at least one choice from seed data");
    }
}
