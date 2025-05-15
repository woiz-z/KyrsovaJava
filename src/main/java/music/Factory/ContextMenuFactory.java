package music.Factory;

import music.Music.MusicCompilation;
import music.MusicAppGUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Клас для створення контекстного меню для списку музичних компіляцій.
 * Забезпечує функціонал для редагування та видалення компіляцій через контекстне меню.
 */
public class ContextMenuFactory {
    static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Logger logger = LogManager.getLogger(ContextMenuFactory.class);

    /**
     * Створює та налаштовує контекстне меню для списку компіляцій.
     * Додає пункти меню для редагування та видалення компіляцій, а також обробник подій для відображення меню.
     *
     * @param compilationList Список компіляцій, для якого створюється контекстне меню.
     */
    public static void createContextMenu(JList<MusicCompilation> compilationList) {
        JPopupMenu contextMenu = new JPopupMenu();

        configureMenuItems(contextMenu, compilationList);
        addMouseListener(compilationList, contextMenu);

        logger.info("Контекстне меню успішно створено та додано до списку компіляцій");
    }

    /**
     * Налаштовує пункти контекстного меню для редагування та видалення компіляцій.
     *
     * @param contextMenu Контекстне меню, до якого додаються пункти.
     * @param compilationList Список компіляцій, для якого налаштовуються дії.
     */
    static void configureMenuItems(JPopupMenu contextMenu, JList<MusicCompilation> compilationList) {
        JMenuItem editItem = createMenuItem("Змінити назву", e -> {
            logger.info("Вибрано пункт 'Змінити назву'");
            executeAction(compilationList, MusicAppGUI::renameCompilation, "перейменування");
        });

        JMenuItem deleteItem = createMenuItem("Видалити", e -> {
            logger.info("Вибрано пункт 'Видалити'");
            executeAction(compilationList, MusicAppGUI::deleteCompilation, "видалення");
        });

        contextMenu.add(editItem);
        contextMenu.add(deleteItem);
    }

    /**
     * Створює пункт меню з заданою назвою та обробником дії.
     *
     * @param name Назва пункту меню.
     * @param action Обробник дії для пункту меню.
     * @return Налаштований пункт меню.
     */
    private static JMenuItem createMenuItem(String name, java.awt.event.ActionListener action) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.setFont(MAIN_FONT);
        menuItem.setIcon(new ImageIcon(new byte[0]));
        menuItem.addActionListener(action);
        return menuItem;
    }

    /**
     * Виконує дію для компіляції, отримуючи екземпляр MusicAppGUI.
     *
     * @param compilationList Список компіляцій.
     * @param action Дія, яка виконується (редагування або видалення).
     * @param actionName Назва дії для логування.
     */
    static void executeAction(JList<MusicCompilation> compilationList,
                              Consumer<MusicAppGUI> action,
                              String actionName) {
        try {
            MusicAppGUI app = (MusicAppGUI) SwingUtilities.getAncestorOfClass(MusicAppGUI.class, compilationList);
            if (app != null) {
                action.accept(app);
                logger.debug("Дію '{}' успішно виконано", actionName);
            } else {
                logger.warn("Не вдалося отримати MusicAppGUI для {}", actionName);
            }
        } catch (Exception ex) {
            logger.error("Помилка при {} компіляції: {}", actionName, ex.getMessage(), ex);
        }
    }

    /**
     * Додає обробник подій миші для відображення контекстного меню.
     *
     * @param compilationList Список компіляцій.
     * @param contextMenu Контекстне меню для відображення.
     */
    static void addMouseListener(JList<MusicCompilation> compilationList, JPopupMenu contextMenu) {
        compilationList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                checkPopup(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                checkPopup(e);
            }

            private void checkPopup(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    try {
                        compilationList.setSelectedIndex(compilationList.locationToIndex(e.getPoint()));
                        contextMenu.show(compilationList, e.getX(), e.getY());
                    } catch (Exception ex) {
                        logger.error("Помилка при відображенні контекстного меню: {}", ex.getMessage(), ex);
                    }
                }
            }
        });
    }

    /**
     * Функціональний інтерфейс для виконання дій з MusicAppGUI.
     */
    @FunctionalInterface
    interface Consumer<T> {
        void accept(T t);
    }
}