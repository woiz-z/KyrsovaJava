package music.Panel;

import music.Music.MusicCompilation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

/**
 * Клас HeaderPanel відповідає за створення та управління панеллю заголовка для відображення
 * інформації про музичну компіляцію, таку як назва, кількість треків, загальна тривалість
 * та фільтри тривалості.
 */
public class HeaderPanel {
    private static final Logger LOGGER = LogManager.getLogger(HeaderPanel.class);
    private final JPanel panel;
    private final JLabel infoLabel;

    /**
     * Конструктор ініціалізує панель заголовка для заданої музичної компіляції.
     *
     * @param compilation Музична компіляція, інформація про яку відображається.
     *                    Якщо null, відображається повідомлення про відсутність даних.
     * @throws RuntimeException Якщо виникає помилка при ініціалізації панелі.
     */
    public HeaderPanel(MusicCompilation compilation) {
        LOGGER.info("Ініціалізація HeaderPanel для компіляції: {}",
                compilation != null ? compilation.getTitle() : "null");

        try {
            panel = createPanel();
            JLabel titleLabel = createTitleLabel(compilation);
            panel.add(titleLabel, BorderLayout.CENTER);

            infoLabel = createInfoLabel(compilation);
            panel.add(infoLabel, BorderLayout.SOUTH);
        } catch (Exception e) {
            LOGGER.error("Критична помилка при створенні HeaderPanel: {}", e.getMessage(), e);
            throw new RuntimeException("Не вдалося ініціалізувати HeaderPanel", e);
        }
    }

    /**
     * Створює основну панель із відповідними налаштуваннями стилю.
     *
     * @return Нова JPanel з встановленими параметрами.
     */
    private JPanel createPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.setOpaque(false);
        return panel;
    }

    /**
     * Створює мітку заголовка з ефектом тіні для назви компіляції.
     *
     * @param compilation Музична компіляція для відображення назви.
     * @return Нова JLabel з налаштованим стилем та ефектом тіні.
     */
    JLabel createTitleLabel(MusicCompilation compilation) {
        JLabel titleLabel = new JLabel(compilation != null ? compilation.getTitle() : "Без назви") {
            @Override
            protected void paintComponent(Graphics g) {
                try {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                    g2.setColor(new Color(0, 0, 0, 30));
                    g2.drawString(getText(), 3, 23);

                    g2.setColor(new Color(50, 50, 50));
                    g2.drawString(getText(), 2, 22);

                    g2.setColor(new Color(70, 130, 180, 100));
                    g2.drawString(getText(), 1, 21);
                } catch (Exception e) {
                    LOGGER.error("Помилка при рендерингу заголовка: {}", e.getMessage(), e);
                }
            }
        };
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        return titleLabel;
    }

    /**
     * Створює інформаційну мітку з даними про компіляцію.
     *
     * @param compilation Музична компіляція для відображення інформації.
     * @return Нова JLabel з інформацією про кількість треків та тривалість.
     */
    JLabel createInfoLabel(MusicCompilation compilation) {
        JLabel infoLabel = new JLabel();
        if (compilation != null) {
            updateInfo(
                    compilation.getTracks().size(),
                    compilation.calculateTotalDuration().toMinutes(),
                    compilation.calculateTotalDuration().getSeconds()
            );
        } else {
            infoLabel.setText("Немає даних компіляції");
            LOGGER.warn("Компіляція null при ініціалізації");
        }
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setForeground(new Color(100, 100, 100));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        return infoLabel;
    }

    /**
     * Повертає панель заголовка.
     *
     * @return JPanel, що містить елементи заголовка.
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * Оновлює інформаційну мітку даними про кількість треків та загальну тривалість.
     *
     * @param trackCount    Кількість треків у компіляції.
     * @param totalMinutes  Загальна тривалість у хвилинах.
     * @param totalSeconds  Загальна тривалість у секундах.
     */
    public void updateInfo(int trackCount, long totalMinutes, long totalSeconds) {
        try {
            LOGGER.info("Оновлення інформації: {} треків, {} хв, {} сек",
                    trackCount, totalMinutes, totalSeconds);
            infoLabel.setText(String.format(
                    "%d треків • %d хв %d сек",
                    trackCount,
                    totalMinutes,
                    totalSeconds % 60
            ));
        } catch (Exception e) {
            LOGGER.error("Помилка при оновленні інформації: {}", e.getMessage(), e);
        }
    }

    /**
     * Оновлює інформаційну мітку з урахуванням фільтрів тривалості.
     *
     * @param trackCount    Кількість треків у компіляції.
     * @param totalMinutes  Загальна тривалість у хвилинах.
     * @param totalSeconds  Загальна тривалість у секундах.
     * @param minMinutes    Мінімальна тривалість у хвилинах для фільтра.
     * @param minSeconds    Мінімальна тривалість у секундах для фільтра.
     * @param maxMinutes    Максимальна тривалість у хвилинах для фільтра.
     * @param maxSeconds    Максимальна тривалість у секундах для фільтра.
     */
    public void updateFilterInfo(int trackCount, long totalMinutes, long totalSeconds,
                                 long minMinutes, long minSeconds, long maxMinutes, long maxSeconds) {
        try {
            LOGGER.info("Оновлення інформації з фільтром: {} треків, {} хв {} сек, фільтр {}:{}-{}:{}",
                    trackCount, totalMinutes, totalSeconds, minMinutes, minSeconds, maxMinutes, maxSeconds);
            infoLabel.setText(String.format(
                    "%d треків • %d хв %d сек • Фільтр: %d:%02d - %d:%02d",
                    trackCount,
                    totalMinutes,
                    totalSeconds % 60,
                    minMinutes,
                    minSeconds % 60,
                    maxMinutes,
                    maxSeconds % 60
            ));
        } catch (Exception e) {
            LOGGER.error("Помилка при оновленні інформації з фільтром: {}", e.getMessage(), e);
        }
    }
}