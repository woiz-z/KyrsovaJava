package music.Dialog;

import music.Manager.TrackDatabaseManager;
import music.Models.MusicCompilation;
import music.Models.MusicGenre;
import music.Models.MusicTrack;
import music.Panel.HeaderPanel;
import music.Panel.TrackListPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;

/**
 * Клас для створення діалогових вікон управління музичними треками.
 * Надає методи для додавання, редагування та фільтрації треків у компіляціях.
 */
public class TrackDialogs {
    private static final Logger logger = LogManager.getLogger(TrackDialogs.class);
    static final Color PANEL_BACKGROUND = new Color(245, 248, 250);
    static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    /**
     * Відображає діалогове вікно для додавання нового треку до компіляції.
     *
     * @param parent          батьківське діалогове вікно
     * @param compilation     компіляція, до якої додається трек
     * @param trackListPanel  панель зі списком треків
     */
    public static void showAddTrackDialog(CompilationDetailsDialog parent, MusicCompilation compilation,
                                          TrackListPanel trackListPanel) {
        logger.info("Розпочато додавання треку до компіляції: {}", compilation.getTitle());

        JPanel panel = createDialogPanel("Додати новий трек");
        JPanel fieldsPanel = createFieldsPanel();

        JTextField titleField = createStyledTextField();
        JTextField artistField = createStyledTextField();
        JComboBox<MusicGenre> genreCombo = createStyledComboBox(MusicGenre.values());
        JSpinner minutesSpinner = createStyledSpinner(0, 59, 3);
        JSpinner secondsSpinner = createStyledSpinner(0, 59, 30);

        addFieldComponents(fieldsPanel, titleField, artistField, genreCombo, minutesSpinner, secondsSpinner);
        panel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        JButton okButton = createDialogButton("Додати", new Color(76, 175, 80));
        JButton cancelButton = createDialogButton("Скасувати", new Color(120, 120, 120));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = createDialog(parent, "Додати трек", panel, 400, 400);

        okButton.addActionListener(evt -> {
            try {
                String title = titleField.getText().trim();
                String artist = artistField.getText().trim();

                if (title.isEmpty() || artist.isEmpty()) {
                    logger.warn("Порожні поля: назва='{}', виконавець='{}'", title, artist);
                    showErrorMessage(dialog, "Назва та виконавець не можуть бути порожніми");
                    return;
                }

                MusicGenre genre = (MusicGenre) genreCombo.getSelectedItem();
                Duration duration = Duration.ofSeconds(
                        (int) minutesSpinner.getValue() * 60 + (int) secondsSpinner.getValue()
                );

                if (duration.isZero()) {
                    logger.warn("Нульова тривалість треку");
                    showErrorMessage(dialog, "Тривалість не може бути нульовою");
                    return;
                }

                MusicTrack newTrack = new MusicTrack(title, artist, genre, duration);
                TrackDatabaseManager.addTrackToCompilation(parent, compilation, trackListPanel, newTrack);
                logger.info("Трек додано: {}", newTrack.getTitle());
                dialog.dispose();
            } catch (Exception e) {
                logger.error("Помилка додавання треку: {}", e.getMessage(), e);
                showErrorMessage(dialog, "Помилка при додаванні треку");
            }
        });

        cancelButton.addActionListener(evt -> dialog.dispose());
        dialog.setVisible(true);
    }

    /**
     * Відображає діалогове вікно для редагування вибраного треку.
     *
     * @param parent         батьківське діалогове вікно
     * @param trackListPanel панель зі списком треків
     */
    public static void showEditTrackDialog(CompilationDetailsDialog parent, TrackListPanel trackListPanel) {
        MusicTrack selectedTrack = trackListPanel.getTrackList().getSelectedValue();
        if (selectedTrack == null) {
            logger.warn("Трек не вибрано для редагування");
            JOptionPane.showMessageDialog(parent, "Виберіть трек для редагування", "Попередження",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        logger.info("Розпочато редагування треку: {}", selectedTrack.getTitle());

        JPanel panel = createDialogPanel("Редагувати трек: " + selectedTrack.getTitle());
        JPanel fieldsPanel = createFieldsPanel();

        JTextField titleField = createStyledTextField(selectedTrack.getTitle());
        JTextField artistField = createStyledTextField(selectedTrack.getArtist());
        JComboBox<MusicGenre> genreCombo = createStyledComboBox(MusicGenre.values());
        genreCombo.setSelectedItem(selectedTrack.getGenre());
        long totalSeconds = selectedTrack.getDuration().getSeconds();
        JSpinner minutesSpinner = createStyledSpinner(0, 59, (int) (totalSeconds / 60));
        JSpinner secondsSpinner = createStyledSpinner(0, 59, (int) (totalSeconds % 60));

        addFieldComponents(fieldsPanel, titleField, artistField, genreCombo, minutesSpinner, secondsSpinner);
        panel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        JButton okButton = createDialogButton("Зберегти", new Color(33, 150, 243));
        JButton cancelButton = createDialogButton("Скасувати", new Color(120, 120, 120));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = createDialog(parent, "Редагувати трек", panel, 400, 400);

        okButton.addActionListener(evt -> {
            try {
                String title = titleField.getText().trim();
                String artist = artistField.getText().trim();

                if (title.isEmpty() || artist.isEmpty()) {
                    logger.warn("Порожні поля: назва='{}', виконавець='{}'", title, artist);
                    showErrorMessage(dialog, "Назва та виконавець не можуть бути порожніми");
                    return;
                }

                Duration duration = Duration.ofSeconds(
                        ((Number) minutesSpinner.getValue()).intValue() * 60 +
                                ((Number) secondsSpinner.getValue()).intValue()
                );

                if (duration.isZero()) {
                    logger.warn("Нульова тривалість треку");
                    showErrorMessage(dialog, "Тривалість не може бути нульовою");
                    return;
                }

                selectedTrack.setTitle(title);
                selectedTrack.setArtist(artist);
                selectedTrack.setGenre((MusicGenre) genreCombo.getSelectedItem());
                selectedTrack.setDuration(duration);

                TrackDatabaseManager.updateTrack(parent, trackListPanel, selectedTrack);
                logger.info("Трек оновлено: {}", selectedTrack.getTitle());
                dialog.dispose();
            } catch (Exception e) {
                logger.error("Помилка оновлення треку: {}", e.getMessage(), e);
                showErrorMessage(dialog, "Помилка при оновленні треку");
            }
        });

        cancelButton.addActionListener(evt -> dialog.dispose());
        dialog.setVisible(true);
    }

    /**
     * Відображає діалогове вікно для фільтрації треків за тривалістю.
     *
     * @param parent         батьківське діалогове вікно
     * @param trackListPanel панель зі списком треків
     */
    public static void showFilterByDurationDialog(CompilationDetailsDialog parent, TrackListPanel trackListPanel) {
        logger.info("Розпочато фільтрування треків за тривалістю");

        JPanel panel = createDialogPanel("Фільтрувати треки за тривалістю");
        JPanel fieldsPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        fieldsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        fieldsPanel.setBackground(PANEL_BACKGROUND);

        JSpinner minMinutesSpinner = createStyledSpinner(0, 59, 0);
        JSpinner minSecondsSpinner = createStyledSpinner(0, 59, 0);
        JSpinner maxMinutesSpinner = createStyledSpinner(0, 59, 10);
        JSpinner maxSecondsSpinner = createStyledSpinner(0, 59, 0);

        fieldsPanel.add(createFormLabel("Мін. хвилин:"));
        fieldsPanel.add(minMinutesSpinner);
        fieldsPanel.add(createFormLabel("Мін. секунд:"));
        fieldsPanel.add(minSecondsSpinner);
        fieldsPanel.add(createFormLabel("Макс. хвилин:"));
        fieldsPanel.add(maxMinutesSpinner);
        fieldsPanel.add(createFormLabel("Макс. секунд:"));
        fieldsPanel.add(maxSecondsSpinner);

        panel.add(fieldsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        JButton okButton = createDialogButton("Фільтрувати", new Color(255, 152, 0));
        JButton cancelButton = createDialogButton("Скасувати", new Color(120, 120, 120));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = createDialog(parent, "Фільтр за тривалістю", panel, 350, 350);

        okButton.addActionListener(evt -> {
            try {
                Duration minDuration = Duration.ofSeconds(
                        ((Number) minMinutesSpinner.getValue()).intValue() * 60 +
                                ((Number) minSecondsSpinner.getValue()).intValue()
                );
                Duration maxDuration = Duration.ofSeconds(
                        ((Number) maxMinutesSpinner.getValue()).intValue() * 60 +
                                ((Number) maxSecondsSpinner.getValue()).intValue()
                );

                if (maxDuration.compareTo(minDuration) < 0) {
                    logger.warn("Неправильний діапазон: min={}, max={}", minDuration, maxDuration);
                    showErrorMessage(dialog, "Максимальна тривалість має бути більшою");
                    return;
                }

                trackListPanel.filterTracksByDuration(minDuration, maxDuration, getHeaderPanel(parent));
                logger.info("Фільтрування виконано: min={}, max={}", minDuration, maxDuration);
                dialog.dispose();
            } catch (Exception e) {
                logger.error("Помилка фільтрування: {}", e.getMessage(), e);
                showErrorMessage(dialog, "Помилка при фільтруванні");
            }
        });

        cancelButton.addActionListener(evt -> dialog.dispose());
        dialog.setVisible(true);
    }

    static JPanel createDialogPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(PANEL_BACKGROUND);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        return panel;
    }

    static JPanel createFieldsPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBorder(new EmptyBorder(15, 0, 0, 0));
        panel.setBackground(PANEL_BACKGROUND);
        return panel;
    }

    private static JDialog createDialog(CompilationDetailsDialog parent, String title, JPanel panel, int width, int height) {
        JDialog dialog = new JDialog(parent, title, true);
        dialog.setContentPane(panel);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(width, height);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);
        return dialog;
    }

    private static JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        return field;
    }

    static JTextField createStyledTextField(String text) {
        JTextField field = createStyledTextField();
        field.setText(text);
        return field;
    }

    static <T> JComboBox<T> createStyledComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(LABEL_FONT);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(2, 8, 2, 8)));
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBorder(new EmptyBorder(3, 5, 3, 5));
                return this;
            }
        });
        return comboBox;
    }

    static JSpinner createStyledSpinner(int min, int max, int value) {
        SpinnerNumberModel model = new SpinnerNumberModel(value, min, max, 1);
        JSpinner spinner = new JSpinner(model);
        spinner.setFont(LABEL_FONT);
        spinner.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        Component editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
            tf.setColumns(3);
        }
        return spinner;
    }

    static JButton createDialogButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? color.darker() : getModel().isRollover() ? color.brighter() : color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
            }
        };

        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(BUTTON_FONT);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        return button;
    }

    static void addFieldComponents(JPanel panel, JTextField titleField, JTextField artistField,
                                   JComboBox<MusicGenre> genreCombo, JSpinner minutesSpinner,
                                   JSpinner secondsSpinner) {
        panel.add(createFormLabel("Назва треку:"));
        panel.add(titleField);
        panel.add(createFormLabel("Виконавець:"));
        panel.add(artistField);
        panel.add(createFormLabel("Жанр:"));
        panel.add(genreCombo);
        panel.add(createFormLabel("Тривалість (хвилини):"));
        panel.add(minutesSpinner);
        panel.add(createFormLabel("Тривалість (секунди):"));
        panel.add(secondsSpinner);
    }

    static void showErrorMessage(JDialog dialog, String message) {
        JOptionPane.showMessageDialog(dialog, message, "Помилка", JOptionPane.ERROR_MESSAGE);
    }

    static HeaderPanel getHeaderPanel(CompilationDetailsDialog parent) {
        try {
            JPanel mainPanel = (JPanel) parent.getContentPane().getComponent(0);
            return new HeaderPanel(parent.compilation);
        } catch (Exception e) {
            logger.error("Помилка отримання HeaderPanel: {}", e.getMessage(), e);
            throw e;
        }
    }
}