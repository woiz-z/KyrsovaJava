package music.Panel;

import music.Factory.ContextMenuFactory;
import music.Models.MusicCompilation;
import music.Renderer.CompilationListRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * Панель для відображення списку музичних збірок із підтримкою пошуку та контекстного меню.
 * Надає інтерфейс для вибору збірок з можливістю перегляду деталей при подвійному кліку.
 */
public class CompilationListPanel {
    private static final Logger logger = LogManager.getLogger(CompilationListPanel.class);
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 250);
    private static final Color PANEL_COLOR = new Color(255, 255, 255);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static JList<MusicCompilation> compilationList;
    private static CompilationSearchPanel searchPanel;

    /**
     * Створює прокручувану панель зі списком музичних збірок.
     *
     * @param listModel    Модель даних для списку збірок
     * @param detailsAction Дія, яка виконується при подвійному кліку на збірці
     * @return JScrollPane, що містить список збірок та панель пошуку
     * @throws RuntimeException якщо створення панелі завершується невдачею
     */
    public static JScrollPane createScrollPane(DefaultListModel<MusicCompilation> listModel,
                                               Consumer<MusicCompilation> detailsAction) {
        try {
            compilationList = initializeCompilationList(listModel, detailsAction);
            ContextMenuFactory.createContextMenu(compilationList);

            JPanel containerPanel = createContainerPanel(listModel);
            JScrollPane scrollPane = configureScrollPane(containerPanel);

            logger.info("Панель списку збірок успішно створено");
            return scrollPane;
        } catch (Exception e) {
            logger.error("Помилка при створенні панелі списку збірок: {}", e.getMessage(), e);
            throw new RuntimeException("Не вдалося створити панель списку збірок", e);
        }
    }

    /**
     * Ініціалізує список збірок із заданою моделлю та обробником подвійного кліку.
     */
    static JList<MusicCompilation> initializeCompilationList(DefaultListModel<MusicCompilation> listModel,
                                                             Consumer<MusicCompilation> detailsAction) {
        JList<MusicCompilation> list = new JList<>(listModel);
        list.setCellRenderer(new CompilationListRenderer());
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBackground(PANEL_COLOR);

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    MusicCompilation selected = list.getSelectedValue();
                    if (selected != null) {
                        logger.info("Вибрано збірку: {}", selected.getName());
                        detailsAction.accept(selected);
                    } else {
                        logger.warn("Подвійне натискання без вибраної збірки");
                    }
                }
            }
        });

        return list;
    }

    /**
     * Створює основну панель-контейнер із панеллю пошуку та списком.
     */
    static JPanel createContainerPanel(DefaultListModel<MusicCompilation> listModel) {
        JPanel containerPanel = new JPanel(new BorderLayout());
        containerPanel.setBackground(BACKGROUND_COLOR);

        searchPanel = new CompilationSearchPanel(compilationList, listModel);
        containerPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(createListPanelBorder());
        listPanel.setBackground(PANEL_COLOR);
        listPanel.add(compilationList, BorderLayout.CENTER);

        containerPanel.add(listPanel, BorderLayout.CENTER);
        return containerPanel;
    }

    /**
     * Налаштовує прокручувану панель із заданим контейнером.
     */
    static JScrollPane configureScrollPane(JPanel containerPanel) {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(containerPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    /**
     * Створює рамку для панелі списку збірок.
     */
    static Border createListPanelBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        "Список збірок",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        MAIN_FONT.deriveFont(Font.BOLD),
                        new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    /**
     * Повертає компонент списку збірок.
     *
     * @return JList<MusicCompilation> список збірок
     */
    public static JList<MusicCompilation> getCompilationList() {
        return compilationList;
    }

    /**
     * Повертає панель пошуку збірок.
     *
     * @return CompilationSearchPanel панель пошуку
     */
    public static CompilationSearchPanel getSearchPanel() {
        return searchPanel;
    }
}