package music.Dialog;

import music.Manager.DiscManager;
import music.Music.MusicCompilation;
import music.Panel.ButtonPanel;
import music.Panel.HeaderPanel;
import music.Panel.TrackListPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Діалогове вікно для відображення деталей музичної збірки.
 * Відображає інформацію про збірку, список треків та кнопки для взаємодії.
 * Використовує градієнтний фон для покращення візуального вигляду.
 */
public class CompilationDetailsDialog extends JDialog {
    private static final Logger logger = LogManager.getLogger(CompilationDetailsDialog.class);
    public final MusicCompilation compilation;
    private final DiscManager discManager;
    TrackListPanel trackListPanel; // Зберігаємо посилання на TrackListPanel

    /**
     * Конструктор діалогового вікна.
     *
     * @param parent       Батьківське вікно (JFrame).
     * @param compilation  Музична збірка, деталі якої відображаються.
     */
    public CompilationDetailsDialog(JFrame parent, MusicCompilation compilation) {
        super(parent, "Деталі збірки: " + compilation.getTitle(), true);
        this.compilation = compilation;
        this.discManager = new DiscManager();
        logger.info("Створення діалогу для збірки: {}", compilation.getTitle());
        initializeUI();
    }

    /**
     * Ініціалізація інтерфейсу користувача.
     * Налаштовує розміри, розташування та додає компоненти до діалогового вікна.
     */
    private void initializeUI() {
        try {
            setSize(1400, 750);
            setLocationRelativeTo(getParent());
            setResizable(true);

            JPanel mainPanel = createMainPanel();
            add(mainPanel);

            addHeaderPanel(mainPanel);
            addTrackListPanel(mainPanel);
            addButtonPanel(mainPanel);

            logger.debug("Інтерфейс для збірки {} успішно ініціалізовано", compilation.getTitle());
        } catch (Exception e) {
            logger.error("Помилка ініціалізації інтерфейсу для збірки {}: {}", compilation.getTitle(), e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Виникла помилка при ініціалізації інтерфейсу.", "Помилка", JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Не вдалося ініціалізувати компоненти інтерфейсу", e);
        }
    }

    /**
     * Створює головну панель із градієнтним фоном.
     *
     * @return Налаштована головна панель.
     */
    JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Graphics2D g2d = (Graphics2D) g;
                    Color color1 = new Color(245, 248, 250);
                    Color color2 = new Color(230, 235, 240);
                    GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                } catch (Exception e) {
                    logger.error("Помилка при малюванні градієнтного фону: {}", e.getMessage(), e);
                }
            }
        };
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        return mainPanel;
    }

    /**
     * Додає панель заголовка до головної панелі.
     *
     * @param mainPanel Головна панель, до якої додається компонент.
     */
    void addHeaderPanel(JPanel mainPanel) {
        HeaderPanel headerPanel = new HeaderPanel(compilation);
        mainPanel.add(headerPanel.getPanel(), BorderLayout.NORTH);
    }

    /**
     * Додає панель списку треків до головної панелі.
     *
     * @param mainPanel Головна панель, до якої додається компонент.
     */
    void addTrackListPanel(JPanel mainPanel) {
        trackListPanel = new TrackListPanel(this, compilation);
        mainPanel.add(trackListPanel.getPanel(), BorderLayout.CENTER);
    }

    /**
     * Додає панель кнопок до головної панелі.
     *
     * @param mainPanel Головна панель, до якої додається компонент.
     */
    void addButtonPanel(JPanel mainPanel) {
        ButtonPanel buttonPanel = new ButtonPanel(this, compilation, trackListPanel);
        mainPanel.add(buttonPanel.getPanel(), BorderLayout.SOUTH);
    }
}