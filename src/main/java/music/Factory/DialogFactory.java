package music.Factory;

import music.Manager.DiscManager;
import music.Music.MusicCompilation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Фабрика для створення діалогових вікон для управління музичними збірками.
 * Надає методи для створення вікон додавання, перейменування та видалення збірок
 * з графічним інтерфейсом, що включає градієнтний фон, сучасні кнопки та текстові поля.
 */
public class DialogFactory {
    private static final Logger LOGGER = LogManager.getLogger(DialogFactory.class);
    private static final Color EDIT_COLOR = new Color(33, 150, 243);
    private static final Color DELETE_COLOR = new Color(220, 53, 69);
    private static final Color ADD_COLOR = new Color(76, 175, 80);
    private static final Color CANCEL_COLOR = new Color(120, 120, 120);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Dimension DIALOG_SIZE_SMALL = new Dimension(400, 200);
    private static final Dimension DIALOG_SIZE_MEDIUM = new Dimension(350, 250);

    /**
     * Відображає діалогове вікно для додавання нової музичної збірки.
     *
     * @param parent      батьківське вікно
     * @param discManager менеджер для управління збірками
     * @param listModel   модель списку для оновлення UI
     * @param statusBar   мітка для відображення статусу
     */
    public static void showAddCompilationDialog(JFrame parent, DiscManager discManager,
                                                DefaultListModel<MusicCompilation> listModel,
                                                JLabel statusBar) {
        JPanel panel = createStyledPanel("Нова збірка");
        JTextField textField = createTextField();
        panel.add(textField, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        JButton okButton = createModernButton("Додати", ADD_COLOR);
        JButton cancelButton = createModernButton("Скасувати", CANCEL_COLOR);

        okButton.addActionListener(e -> handleAddAction(textField, discManager, listModel, statusBar, panel));
        cancelButton.addActionListener(e -> closeDialog(panel));

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        showDialog(parent, panel, DIALOG_SIZE_SMALL);
    }

    /**
     * Відображає діалогове вікно для перейменування існуючої збірки.
     *
     * @param parent      батьківське вікно
     * @param discManager менеджер для управління збірками
     * @param listModel   модель списку для оновлення UI
     * @param statusBar   мітка для відображення статусу
     * @param selected    збірка, яку потрібно перейменувати
     */
    public static void showRenameCompilationDialog(JFrame parent, DiscManager discManager,
                                                   DefaultListModel<MusicCompilation> listModel,
                                                   JLabel statusBar, MusicCompilation selected) {
        JPanel panel = createStyledPanel("Змінити назву збірки");
        JTextField textField = createTextField();
        textField.setText(selected.getTitle());
        panel.add(textField, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        JButton okButton = createModernButton("Зберегти", EDIT_COLOR);
        JButton cancelButton = createModernButton("Скасувати", CANCEL_COLOR);

        okButton.addActionListener(e -> handleRenameAction(textField, selected, discManager, listModel, statusBar, panel));
        cancelButton.addActionListener(e -> closeDialog(panel));

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        showDialog(parent, panel, DIALOG_SIZE_SMALL);
    }

    /**
     * Відображає діалогове вікно для підтвердження видалення збірки.
     *
     * @param parent      батьківське вікно
     * @param discManager менеджер для управління збірками
     * @param listModel   модель списку для оновлення UI
     * @param statusBar   мітка для відображення статусу
     * @param selected    збірка, яку потрібно видалити
     */
    public static void showDeleteCompilationDialog(JFrame parent, DiscManager discManager,
                                                   DefaultListModel<MusicCompilation> listModel,
                                                   JLabel statusBar, MusicCompilation selected) {
        JPanel panel = createGradientPanel();
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel messageLabel = new JLabel(createDeleteMessage(selected), SwingConstants.CENTER);
        messageLabel.setFont(MAIN_FONT);
        panel.add(messageLabel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        JButton deleteButton = createModernButton("Видалити", DELETE_COLOR);
        JButton cancelButton = createModernButton("Скасувати", CANCEL_COLOR);

        deleteButton.addActionListener(e -> handleDeleteAction(selected, discManager, listModel, statusBar, panel));
        cancelButton.addActionListener(e -> closeDialog(panel));

        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        showDialog(parent, panel, DIALOG_SIZE_MEDIUM);
    }

    static JPanel createStyledPanel(String title) {
        JPanel panel = createGradientPanel();
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(new Color(50, 50, 50));
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    static JPanel createGradientPanel() {
        return new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = new Color(245, 248, 250);
                Color color2 = new Color(230, 235, 240);
                GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
    }

    static JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(MAIN_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        return textField;
    }

    static JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? color.darker() : getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color.darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            }
        };
        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFont(MAIN_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    static JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);
        return buttonPanel;
    }

    static String createDeleteMessage(MusicCompilation selected) {
        return String.format(
                "<html><div style='text-align: center;'>" +
                        "<b>Ви впевнені, що хочете видалити збірку?</b><br><br>" +
                        "Назва: <i>%s</i><br>" +
                        "Кількість треків: economia <i>%d</i><br><br>" +
                        "Ця дія незворотня!" +
                        "</div></html>",
                selected.getTitle(), selected.getTracks().size());
    }

    static void showDialog(JFrame parent, JPanel panel, Dimension size) {
        try {
            JDialog dialog = new JDialog(parent, true);
            dialog.setContentPane(panel);
            dialog.setSize(size);
            dialog.setLocationRelativeTo(parent);
            dialog.setResizable(false);
            dialog.setVisible(true);
        } catch (Exception ex) {
            LOGGER.error("Помилка при відображенні діалогового вікна: {}", ex.getMessage(), ex);
        }
    }

    static void handleAddAction(JTextField textField, DiscManager discManager,
                                DefaultListModel<MusicCompilation> listModel,
                                JLabel statusBar, JPanel panel) {
        try {
            String title = textField.getText().trim();
            if (!title.isEmpty()) {
                discManager.addCompilation(new MusicCompilation(title));
                refreshList(listModel, discManager, statusBar, "Додано нову збірку: " + title);
                LOGGER.info("Додано нову збірку: {}", title);
                closeDialog(panel);
            } else {
                LOGGER.warn("Спроба додати збірку з порожньою назвою");
            }
        } catch (Exception ex) {
            LOGGER.error("Помилка при додаванні збірки: {}", ex.getMessage(), ex);
        }
    }

    static void handleRenameAction(JTextField textField, MusicCompilation selected,
                                   DiscManager discManager, DefaultListModel<MusicCompilation> listModel,
                                   JLabel statusBar, JPanel panel) {
        try {
            String newTitle = textField.getText().trim();
            if (!newTitle.isEmpty()) {
                discManager.updateCompilationTitle(selected, newTitle);
                refreshList(listModel, discManager, statusBar, "Збірку перейменовано на: " + newTitle);
                LOGGER.info("Збірку перейменовано: {} -> {}", selected.getTitle(), newTitle);
                closeDialog(panel);
            } else {
                LOGGER.warn("Спроба перейменувати збірку на порожню назву");
            }
        } catch (Exception ex) {
            LOGGER.error("Помилка при перейменуванні збірки: {}", ex.getMessage(), ex);
        }
    }

    static void handleDeleteAction(MusicCompilation selected, DiscManager discManager,
                                   DefaultListModel<MusicCompilation> listModel,
                                   JLabel statusBar, JPanel panel) {
        try {
            discManager.removeCompilation(selected);
            refreshList(listModel, discManager, statusBar, "Збірку видалено: " + selected.getTitle());
            LOGGER.info("Видалено збірку: {}", selected.getTitle());
            closeDialog(panel);
        } catch (Exception ex) {
            LOGGER.error("Помилка при видаленні збірки: {}", ex.getMessage(), ex);
        }
    }

    static void refreshList(DefaultListModel<MusicCompilation> listModel,
                            DiscManager discManager, JLabel statusBar, String message) {
        try {
            listModel.clear();
            discManager.getCompilations().forEach(listModel::addElement);
            statusBar.setText(message);
        } catch (Exception ex) {
            LOGGER.error("Помилка при оновленні списку збірок: {}", ex.getMessage(), ex);
        }
    }

    static void closeDialog(JPanel panel) {
        ((Window) SwingUtilities.getRoot(panel)).dispose();
    }
}