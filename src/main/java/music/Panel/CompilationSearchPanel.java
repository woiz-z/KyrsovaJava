package music.Panel;

import music.Music.MusicCompilation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Панель пошуку музичних збірок із текстовим полем для фільтрації та відображенням результатів у списку.
 * Підтримує пошук у реальному часі, стилізоване поле введення з іконкою та кнопкою очищення.
 */
public class CompilationSearchPanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(CompilationSearchPanel.class);
    private final JTextField searchField;
    private final DefaultListModel<MusicCompilation> originalModel;
    private final DefaultListModel<MusicCompilation> filteredModel;
    private final JList<MusicCompilation> compilationList;
    private List<MusicCompilation> allCompilations;

    /**
     * Конструктор панелі пошуку.
     *
     * @param compilationList Список для відображення збірок.
     * @param listModel       Модель даних для списку збірок.
     */
    public CompilationSearchPanel(JList<MusicCompilation> compilationList, DefaultListModel<MusicCompilation> listModel) {
        logger.info("Ініціалізація панелі пошуку збірок");
        this.compilationList = compilationList;
        this.originalModel = listModel;
        this.filteredModel = new DefaultListModel<>();
        this.allCompilations = new ArrayList<>();
        this.searchField = createSearchField();

        initializeCompilations();
        setupLayout();
        addSearchListener();
    }

    /**
     * Ініціалізує список усіх збірок із моделі.
     */
    private void initializeCompilations() {
        try {
            for (int i = 0; i < originalModel.getSize(); i++) {
                allCompilations.add(originalModel.get(i));
            }
            logger.debug("Завантажено {} збірок", allCompilations.size());
        } catch (Exception e) {
            logger.error("Помилка при ініціалізації збірок: {}", e.getMessage(), e);
        }
    }

    /**
     * Налаштовує компонування панелі пошуку.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setOpaque(false);

        JLabel searchIcon = createSearchIcon();
        JButton clearButton = createClearButton();

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(clearButton, BorderLayout.EAST);

        searchPanel.add(searchIcon, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(rightPanel, BorderLayout.EAST);

        add(searchPanel, BorderLayout.CENTER);
    }

    /**
     * Створює стилізоване поле пошуку.
     *
     * @return Налаштоване текстове поле.
     */
    private JTextField createSearchField() {
        JTextField field = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 220));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);

                if (getText().isEmpty() && !hasFocus()) {
                    g2.setColor(new Color(150, 150, 150, 150));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    FontMetrics fm = g2.getFontMetrics();
                    int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString("Пошук збірок...", 23, y);
                }
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isFocusOwner() ? new Color(70, 130, 180) : new Color(200, 200, 200));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(50, 50, 50));
        field.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        field.setOpaque(false);
        return field;
    }

    /**
     * Створює іконку пошуку.
     *
     * @return Налаштована іконка.
     */
    private JLabel createSearchIcon() {
        JLabel icon = new JLabel("🔍") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(150, 150, 150));
                g2.setFont(getFont().deriveFont(16f));
                FontMetrics fm = g2.getFontMetrics();
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString("🔍", 0, y);
            }
        };
        icon.setPreferredSize(new Dimension(30, 30));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        icon.setOpaque(false);
        return icon;
    }

    /**
     * Створює кнопку очищення поля пошуку.
     *
     * @return Налаштована кнопка.
     */
    private JButton createClearButton() {
        JButton button = new JButton("×") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight());
                g2.setColor(getModel().isRollover() ? new Color(220, 220, 220) : new Color(240, 240, 240));
                g2.fillOval(0, 5, size - 1, size - 1);
                g2.setColor(new Color(120, 120, 120));
                g2.setFont(getFont().deriveFont(Font.BOLD, 14f));
                FontMetrics fm = g2.getFontMetrics();
                int x = (size - fm.stringWidth("×")) / 2;
                int y = (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString("×", x, y + 5);
            }

            @Override
            protected void paintBorder(Graphics g) {
                // Без рамки
            }
        };
        button.setPreferredSize(new Dimension(24, 24));
        button.setMaximumSize(new Dimension(24, 24));
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.addActionListener(e -> {
            logger.info("Очищення поля пошуку");
            searchField.setText("");
            searchField.requestFocus();
        });
        return button;
    }

    /**
     * Додає слухач для пошуку в реальному часі.
     */
    private void addSearchListener() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterCompilations();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterCompilations();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterCompilations();
            }
        });
    }

    /**
     * Фільтрує список збірок на основі тексту пошуку.
     */
    private void filterCompilations() {
        try {
            String searchText = searchField.getText().toLowerCase();
            filteredModel.clear();

            if (searchText.isEmpty()) {
                compilationList.setModel(originalModel);
                logger.info("Показано всі збірки (порожній пошук)");
            } else {
                for (MusicCompilation compilation : allCompilations) {
                    if (matchesSearch(compilation, searchText)) {
                        filteredModel.addElement(compilation);
                    }
                }
                compilationList.setModel(filteredModel);
                logger.info("Знайдено {} збірок за пошуковим запитом", filteredModel.size());
            }
        } catch (Exception e) {
            logger.error("Помилка при фільтрації збірок: {}", e.getMessage(), e);
        }
    }

    /**
     * Перевіряє, чи відповідає збірка пошуковому запиту.
     *
     * @param compilation Збірка для перевірки.
     * @param searchText  Текст пошукового запиту.
     * @return true, якщо збірка відповідає запиту.
     */
    private boolean matchesSearch(MusicCompilation compilation, String searchText) {
        try {
            return compilation.getTitle().toLowerCase().contains(searchText) ||
                    String.valueOf(compilation.getTracks().size()).contains(searchText);
        } catch (Exception e) {
            logger.error("Помилка при перевірці відповідності збірки: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Оновлює список збірок і застосовує поточний фільтр.
     *
     * @param compilations Новий список збірок.
     */
    public void updateCompilationList(List<MusicCompilation> compilations) {
        try {
            allCompilations = new ArrayList<>(compilations);
            logger.info("Оновлено список збірок, кількість: {}", allCompilations.size());
            filterCompilations();
        } catch (Exception e) {
            logger.error("Помилка при оновленні списку збірок: {}", e.getMessage(), e);
        }
    }
}