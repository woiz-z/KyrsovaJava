package music.Factory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Фабричний клас для створення панелі інструментів із кнопками для управління музичними збірками.
 * Надає методи для створення стилізованої панелі інструментів із підтримкою дій для завантаження,
 * збереження, додавання, перейменування та видалення збірок.
 */
public class ToolBarFactory {
    private static final Logger LOGGER = LogManager.getLogger(ToolBarFactory.class);
    private static final Color PANEL_COLOR = new Color(255, 255, 255);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final int BORDER_RADIUS = 10;
    private static final Dimension SEPARATOR_SIZE = new Dimension(10, 0);

    /**
     * Створює панель інструментів із кнопками для виконання заданих дій.
     *
     * @param loadAction   дія для завантаження даних із файлу
     * @param saveAction   дія для збереження даних у файл
     * @param addAction    дія для додавання нової збірки
     * @param renameAction дія для перейменування збірки
     * @param deleteAction дія для видалення збірки
     * @return стилізована панель інструментів типу JToolBar
     * @throws RuntimeException якщо виникає помилка при створенні панелі
     */
    public static JToolBar createToolBar(Runnable loadAction, Runnable saveAction,
                                         Runnable addAction, Runnable renameAction,
                                         Runnable deleteAction) {
            JToolBar toolBar = new JToolBar();
            configureToolBar(toolBar);
            addButtons(toolBar, loadAction, saveAction, addAction, renameAction, deleteAction);
            return toolBar;
    }

    /**
     * Налаштовує основні параметри панелі інструментів.
     *
     * @param toolBar панель інструментів для налаштування
     */
    static void configureToolBar(JToolBar toolBar) {
        toolBar.setFloatable(false);
        toolBar.setBackground(PANEL_COLOR);
        toolBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
    }

    /**
     * Додає кнопки до панелі інструментів із відповідними діями.
     *
     * @param toolBar      панель інструментів
     * @param loadAction   дія для завантаження
     * @param saveAction   дія для збереження
     * @param addAction    дія для додавання
     * @param renameAction дія для перейменування
     * @param deleteAction дія для видалення
     */
    static void addButtons(JToolBar toolBar, Runnable loadAction, Runnable saveAction,
                           Runnable addAction, Runnable renameAction, Runnable deleteAction) {
        addToolbarButton(toolBar, "Завантажити з файлу", "📂", loadAction);
        toolBar.addSeparator(SEPARATOR_SIZE);
        addToolbarButton(toolBar, "Зберегти у файл", "💾", saveAction);
        toolBar.addSeparator(SEPARATOR_SIZE);
        addToolbarButton(toolBar, "Додати збірку", "➕", addAction);
        toolBar.addSeparator(SEPARATOR_SIZE);
        addToolbarButton(toolBar, "Змінити назву", "✏️", renameAction);
        toolBar.addSeparator(SEPARATOR_SIZE);
        addToolbarButton(toolBar, "Видалити збірку", "🗑️", deleteAction);
    }

    /**
     * Додає стилізовану кнопку до панелі інструментів.
     *
     * @param toolBar панель інструментів
     * @param text    текст кнопки
     * @param icon    іконка кнопки (Unicode emoji)
     * @param action  дія, що виконується при натисканні кнопки
     * @throws RuntimeException якщо виникає помилка при створенні кнопки
     */
    static void addToolbarButton(JToolBar toolBar, String text, String icon, Runnable action) {
            JButton button = createStyledButton(text, icon, action);
            toolBar.add(button);
    }

    /**
     * Створює стилізовану кнопку з кастомним рендерингом.
     *
     * @param text   текст кнопки
     * @param icon   іконка кнопки
     * @param action дія кнопки
     * @return стилізована кнопка типу JButton
     */
    static JButton createStyledButton(String text, String icon, Runnable action) {
        JButton button = new JButton(String.format("%s %s", icon, text)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color backgroundColor = getModel().isPressed() ? ACCENT_COLOR.darker() :
                        getModel().isRollover() ? ACCENT_COLOR.brighter() : ACCENT_COLOR;

                g2.setColor(backgroundColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), BORDER_RADIUS, BORDER_RADIUS);
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(ACCENT_COLOR.darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BORDER_RADIUS, BORDER_RADIUS);
                g2.dispose();
            }
        };

        configureButton(button, action);
        return button;
    }

    /**
     * Налаштовує параметри кнопки.
     *
     * @param button кнопка для налаштування
     * @param action дія, що виконується при натисканні
     */
    static void configureButton(JButton button, Runnable action) {
        button.setContentAreaFilled(false);
        button.setFont(MAIN_FONT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            try {
                action.run();
            } catch (Exception ex) {
                LOGGER.error("Помилка при виконанні дії кнопки: {}", ex.getMessage(), ex);
                throw new RuntimeException("Помилка виконання дії кнопки", ex);
            }
        });
    }
}