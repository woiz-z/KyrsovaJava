package music;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Клас для налаштування та управління підключенням до бази даних MySQL.
 * Надає метод для отримання з'єднання з базою даних music_collection.
 */
public class DatabaseConfig {
    private static final Logger logger = LogManager.getLogger(DatabaseConfig.class);

    // Конфігураційні параметри бази даних
    private static final String URL = "jdbc:mysql://localhost:3306/music_collection?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Kinolog123456";

    /**
     * Отримує з'єднання з базою даних MySQL.
     *
     * @return Connection об'єкт для взаємодії з базою даних
     * @throws SQLException якщо виникає помилка підключення до бази даних
     */
    public static Connection getConnection() throws SQLException {
        logger.info("Встановлення з'єднання з базою даних: {}", URL);
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("З'єднання з базою даних успішно встановлено");
            return connection;
        } catch (SQLException e) {
            logger.error("Помилка підключення до бази даних: {}", e.getMessage(), e);
            throw e;
        }
    }
}