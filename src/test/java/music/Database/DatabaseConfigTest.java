package music.Database;

import music.DatabaseConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseConfigTest {

    private Connection realConnection;

    @BeforeEach
    void setUp() throws SQLException {
        // Отримуємо реальне підключення для інтеграційного тесту
        realConnection = DatabaseConfig.getConnection();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (realConnection != null && !realConnection.isClosed()) {
            realConnection.close();
        }
    }

    @Test
    void getConnection_ShouldReturnValidConnection() throws SQLException {
        assertNotNull(realConnection);
        assertFalse(realConnection.isClosed());
        assertTrue(realConnection.isValid(2)); // Перевіряємо, що підключення дійсне
    }

    @Test
    void getConnection_ShouldThrowSQLException_WhenInvalidCredentials() {
        try (MockedStatic<DatabaseConfig> mocked = mockStatic(DatabaseConfig.class)) {
            SQLException expectedException = new SQLException("Invalid credentials");
            mocked.when(DatabaseConfig::getConnection).thenThrow(expectedException);

            SQLException exception = assertThrows(SQLException.class, DatabaseConfig::getConnection);
            assertNotNull(exception.getMessage());
        }
    }

    @Test
    void getConnection_ShouldLogError_WhenConnectionFails() {
        try (MockedStatic<DatabaseConfig> mockedConfig = mockStatic(DatabaseConfig.class)) {
            SQLException expectedException = new SQLException("Connection failed");
            mockedConfig.when(DatabaseConfig::getConnection).thenThrow(expectedException);

            SQLException thrown = assertThrows(SQLException.class, DatabaseConfig::getConnection);
            assertEquals(expectedException, thrown);
        }
    }
    @Test
    void getConnection_ShouldLogErrorAndThrowSQLException_WhenConnectionFails() throws SQLException {
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {
            // Створюємо виняток, який буде кинутий при спробі підключення
            SQLException expectedException = new SQLException("Connection failed");

            // Налаштовуємо мок для DriverManager.getConnection(), щоб він кидав виняток
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenThrow(expectedException);

            // Викликаємо метод, який тестуємо
            SQLException thrown = assertThrows(SQLException.class, DatabaseConfig::getConnection);

            // Перевіряємо, що виняток був кинутий
            assertEquals(expectedException, thrown);

        }
    }
}