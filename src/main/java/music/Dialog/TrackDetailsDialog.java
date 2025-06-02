package music.Dialog;

import music.Models.MusicTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Діалогове вікно для відображення детальної інформації про музичний трек.
 * Включає такі дані: назва, виконавець, жанр, тривалість та ID треку.
 * Вікно має стилізований інтерфейс з градієнтним фоном, кастомними кнопками та текстом.
 */
public class TrackDetailsDialog extends JDialog {
    private static final Logger logger = LogManager.getLogger(TrackDetailsDialog.class);

    /**
     * Конструктор діалогового вікна.
     *
     * @param parent батьківський фрейм для центрування вікна
     * @param track об'єкт MusicTrack, дані якого будуть відображені
     */
    public TrackDetailsDialog(JFrame parent, MusicTrack track) {
        super(parent, "Деталі треку: " + track.getTitle(), true);
        logger.info("Створення діалогу деталей треку для: {}", track.getTitle());
        try {
            initializeUI(track);
        } catch (Exception e) {
            logger.error("Помилка ініціалізації UI для треку {}: {}", track.getTitle(), e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                    "Виникла помилка при відображенні деталей треку",
                    "Помилка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Ініціалізує інтерфейс користувача діалогового вікна.
     *
     * @param track об'єкт MusicTrack для відображення даних
     */
    private void initializeUI(MusicTrack track) {
        logger.debug("Ініціалізація UI для треку: {}", track.getTitle());
        configureDialogProperties();
        JPanel mainPanel = createMainPanel();
        add(mainPanel);

        mainPanel.add(createTitleLabel(), BorderLayout.NORTH);
        mainPanel.add(createTrackInfoPanel(track), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(track), BorderLayout.SOUTH);
    }

    /**
     * Налаштовує основні властивості діалогового вікна.
     */
    private void configureDialogProperties() {
        setSize(500, 500);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    /**
     * Створює головну панель з градієнтним фоном.
     *
     * @return JPanel з налаштованим градієнтним фоном
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                try {
                    Color color1 = new Color(245, 248, 250);
                    Color color2 = new Color(220, 230, 240);
                    GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } catch (Exception e) {
                    logger.error("Помилка малювання градієнтного фону: {}", e.getMessage(), e);
                }
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return mainPanel;
    }

    /**
     * Створює заголовок вікна з градієнтним текстом та тінню.
     *
     * @return JLabel зі стилізованим заголовком
     */
    private JLabel createTitleLabel() {
        JLabel titleLabel = new JLabel("Деталі треку", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                try {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.drawString(getText(), 153, 23);

                    GradientPaint gradient = new GradientPaint(0, 0, new Color(70, 130, 180), 0, 20, new Color(50, 100, 150));
                    g2.setPaint(gradient);
                    g2.drawString(getText(), 150, 22);
                } catch (Exception e) {
                    logger.error("Помилка малювання заголовку: {}", e.getMessage(), e);
                }
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        return titleLabel;
    }

    /**
     * Створює панель з інформацією про трек.
     *
     * @param track об'єкт MusicTrack для відображення даних
     * @return JPanel з інформацією про трек
     */
    private JPanel createTrackInfoPanel(MusicTrack track) {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createCompoundBorder());

        try {
            infoPanel.add(createDetailRow("Назва:", track.getTitle()));
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            infoPanel.add(createDetailRow("Виконавець:", track.getArtist()));
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            infoPanel.add(createDetailRow("Жанр:", track.getGenre().toString()));
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

            long totalSeconds = track.getDuration().getSeconds();
            String durationText = String.format("%d хв %d сек", totalSeconds / 60, totalSeconds % 60);
            infoPanel.add(createDetailRow("Тривалість:", durationText));
            infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            infoPanel.add(createDetailRow("ID треку:", track.getId() != null ? track.getId().toString() : "N/A"));
        } catch (Exception e) {
            logger.error("Помилка додавання інформації про трек: {}", e.getMessage(), e);
        }

        return infoPanel;
    }

    /**
     * Створює панель з кнопкою закриття.
     *
     * @param track об'єкт MusicTrack для логування
     * @return JPanel з кнопкою закриття
     */
    private JPanel createButtonPanel(MusicTrack track) {
        JButton closeButton = createCloseButton(track);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        buttonPanel.add(closeButton);
        return buttonPanel;
    }

    /**
     * Створює стилізовану кнопку закриття.
     *
     * @param track об'єкт MusicTrack для логування
     * @return JButton зі стилізованим виглядом
     */
    private JButton createCloseButton(MusicTrack track) {
        JButton closeButton = new JButton("Закрити") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                try {
                    if (getModel().isPressed()) {
                        g2.setColor(new Color(70, 130, 180).darker());
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(70, 130, 180).brighter());
                    } else {
                        g2.setColor(new Color(70, 130, 180));
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    super.paintComponent(g);
                } catch (Exception e) {
                    logger.error("Помилка малювання кнопки закриття: {}", e.getMessage(), e);
                }
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                try {
                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
                } catch (Exception e) {
                    logger.error("Помилка малювання межі кнопки: {}", e.getMessage(), e);
                }
            }
        };

        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setForeground(Color.WHITE);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> {
            logger.info("Закриття діалогу деталей треку: {}", track.getTitle());
            dispose();
        });

        return closeButton;
    }

    /**
     * Створює рядок з детальною інформацією.
     *
     * @param label назва поля
     * @param value значення поля
     * @return JPanel з відформатованим рядком інформації
     */
    private JPanel createDetailRow(String label, String value) {
        logger.debug("Створення рядка деталей: {} - {}", label, value);
        JPanel rowPanel = new JPanel(new BorderLayout(15, 0));
        rowPanel.setOpaque(false);
        rowPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(new Color(80, 80, 80));
        labelComponent.setPreferredSize(new Dimension(120, 20));

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(new Color(40, 40, 40));

        JPanel valuePanel = new JPanel(new BorderLayout(10, 0));
        valuePanel.setOpaque(false);
        valuePanel.add(createIconLabel(), BorderLayout.WEST);
        valuePanel.add(valueComponent, BorderLayout.CENTER);

        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valuePanel, BorderLayout.CENTER);

        return rowPanel;
    }

    /**
     * Створює мітку з іконкою у вигляді крапки.
     *
     * @return JLabel зі стилізованою крапкою
     */
    private JLabel createIconLabel() {
        JLabel iconLabel = new JLabel("•") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                try {
                    g2.setColor(new Color(70, 130, 180));
                    g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString("•", 0, y);
                } catch (Exception e) {
                    logger.error("Помилка малювання іконки: {}", e.getMessage(), e);
                }
            }
        };
        iconLabel.setPreferredSize(new Dimension(15, 15));
        return iconLabel;
    }
}