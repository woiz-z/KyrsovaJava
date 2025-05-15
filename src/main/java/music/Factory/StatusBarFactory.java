package music.Factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.*;
import java.awt.*;

/**
 * Фабричний клас для створення статусного рядка в графічному інтерфейсі.
 * Створює JLabel з налаштованим виглядом для відображення статусної інформації.
 */
public class StatusBarFactory {
    private static final Logger LOGGER = LogManager.getLogger(StatusBarFactory.class);
    private static final Color PANEL_COLOR = new Color(255, 255, 255);
    private static final Color BORDER_COLOR = new Color(200, 200, 200);
    private static final Color TEXT_COLOR = new Color(100, 100, 100);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final String DEFAULT_TEXT = " CREATED BY IHOR";

    /**
     * Створює та налаштовує статусний рядок у вигляді JLabel.
     *
     * @return Налаштований компонент JLabel для використання як статусний рядок.
     * @throws RuntimeException якщо сталася помилка під час створення статусного рядка.
     */
    public static JLabel createStatusBar() {
        try {
            JLabel statusBar = new JLabel(DEFAULT_TEXT);
            configureStatusBar(statusBar);
            LOGGER.info("Статусний рядок успішно створено");
            return statusBar;
        } catch (Exception e) {
            LOGGER.error("Помилка при створенні статусного рядка: {}", e.getMessage(), e);
            throw new RuntimeException("Не вдалося створити статусний рядок", e);
        }
    }

    /**
     * Налаштовує зовнішній вигляд і властивості статусного рядка.
     *
     * @param statusBar Компонент JLabel, який потрібно налаштувати.
     */
    static void configureStatusBar(JLabel statusBar) {
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        statusBar.setFont(MAIN_FONT);
        statusBar.setForeground(TEXT_COLOR);
        statusBar.setBackground(PANEL_COLOR);
        statusBar.setOpaque(true);
    }
}