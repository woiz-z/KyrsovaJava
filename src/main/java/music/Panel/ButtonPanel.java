package music.Panel;

import music.Dialog.CompilationDetailsDialog;
import music.Dialog.StatisticsDialog;
import music.Dialog.TrackDialogs;
import music.Manager.TrackDatabaseManager;
import music.Music.MusicCompilation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Панель із кнопками для керування треками в діалоговому вікні збірок.
 * Забезпечує функціонал додавання, редагування, видалення треків, сортування, фільтрації та відображення статистики.
 */
public class ButtonPanel {
    private static final Logger logger = LogManager.getLogger(ButtonPanel.class);
    private final JPanel panel;
    private final CompilationDetailsDialog parent;
    private final MusicCompilation compilation;
    private final TrackListPanel trackListPanel;

    /**
     * Конструктор панелі кнопок.
     *
     * @param parent        Діалогове вікно, яке містить цю панель
     * @param compilation   Музична компіляція, з якою працює панель
     * @param trackListPanel Панель зі списком треків
     */
    public ButtonPanel(CompilationDetailsDialog parent, MusicCompilation compilation, TrackListPanel trackListPanel) {
        this.parent = parent;
        this.compilation = compilation;
        this.trackListPanel = trackListPanel;
        this.panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panel.setOpaque(false);

        initializeButtons();
    }

    /**
     * Ініціалізує кнопки панелі та їхні обробники подій.
     */
    private void initializeButtons() {
        JButton[] buttons = {
                createButton("Додати трек", new Color(76, 175, 80), e -> TrackDialogs.showAddTrackDialog(parent, compilation, trackListPanel)),
                createButton("Редагувати", new Color(33, 150, 243), e -> TrackDialogs.showEditTrackDialog(parent, trackListPanel)),
                createButton("Видалити", new Color(244, 67, 54), e -> TrackDatabaseManager.deleteSelectedTrack(parent, trackListPanel, compilation)),
                createButton("Сортувати за жанром", new Color(156, 39, 176), e -> TrackDatabaseManager.sortTracksByGenre(trackListPanel, compilation)),
                createButton("Фільтр за тривалістю", new Color(255, 152, 0), e -> TrackDialogs.showFilterByDurationDialog(parent, trackListPanel)),
                createButton("Скинути фільтр", new Color(96, 125, 139), e -> trackListPanel.resetFilter(getHeaderPanel())),
                createButton("Статистика", new Color(121, 85, 72), e -> showStatistics()),
                createButton("Закрити", new Color(120, 120, 120), e -> parent.dispose())
        };

        Arrays.stream(buttons).forEach(panel::add);
    }

    /**
     * Створює стилізовану кнопку з заданим текстом, кольором та обробником подій.
     *
     * @param text  Текст кнопки
     * @param color Колір фону кнопки
     * @param action Обробник події натискання
     * @return Стилізована кнопка
     */
    JButton createButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g2);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 50));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };

        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            try {
                action.actionPerformed(e);
            } catch (Exception ex) {
                logger.error("Помилка при виконанні дії кнопки '{}': {}", text, ex.getMessage(), ex);
            }
        });

        return button;
    }

    /**
     * Відображає діалогове вікно зі статистикою збірок.
     */
    void showStatistics() {
        StatisticsDialog dialog = new StatisticsDialog((JFrame) parent.getParent(), compilation);
        dialog.setVisible(true);
    }

    /**
     * Отримує панель заголовка для скидання фільтру.
     *
     * @return HeaderPanel або null у разі помилки
     */
    HeaderPanel getHeaderPanel() {
        try {
            JPanel mainPanel = (JPanel) parent.getContentPane().getComponent(0);
            return new HeaderPanel(compilation);
        } catch (Exception e) {
            logger.error("Помилка при отриманні HeaderPanel: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Повертає панель із кнопками.
     *
     * @return JPanel із кнопками
     */
    public JPanel getPanel() {
        return panel;
    }
}