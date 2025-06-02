package music.Panel;

import music.Models.MusicTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Панель пошуку треків із текстовим полем та іконками для фільтрації списку музичних треків.
 * Підтримує пошук за назвою, виконавцем або жанром у реальному часі.
 */
public class TrackSearchPanel extends JPanel {
    private static final Logger logger = LogManager.getLogger(TrackSearchPanel.class);
    final JTextField searchField;
    private final DefaultListModel<MusicTrack> originalModel;
    private final DefaultListModel<MusicTrack> filteredModel;
    private final JList<MusicTrack> trackList;
    List<MusicTrack> allTracks;

    /**
     * Конструктор панелі пошуку треків.
     *
     * @param trackList JList, який відображає список треків
     * @param listModel Модель даних для списку треків
     */
    public TrackSearchPanel(JList<MusicTrack> trackList, DefaultListModel<MusicTrack> listModel) {
        logger.info("Ініціалізація TrackSearchPanel");
        this.trackList = trackList;
        this.originalModel = listModel;
        this.filteredModel = new DefaultListModel<>();
        this.allTracks = new ArrayList<>();
        this.searchField = createSearchField();

        initializeTracks();
        setupLayout();
        setupSearchListener();
        logger.debug("originalModel size: {}, trackList model: {}", originalModel.size(), trackList.getModel().getSize());
    }

    /**
     * Ініціалізація списку треків із моделі.
     */
    void initializeTracks() {
        try {
            allTracks.clear();
            for (int i = 0; i < originalModel.getSize(); i++) {
                allTracks.add(originalModel.get(i));
            }
            logger.debug("Завантажено {} треків", allTracks.size());
        } catch (Exception e) {
            logger.error("Помилка ініціалізації треків: {}", e.getMessage(), e);
        }
    }

    /**
     * Налаштування компонування панелі пошуку.
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setOpaque(false);

        searchPanel.add(createSearchIcon(), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(createClearButtonPanel(), BorderLayout.EAST);

        add(searchPanel, BorderLayout.CENTER);
    }

    /**
     * Створення текстового поля пошуку з кастомним стилем.
     *
     * @return Налаштоване текстове поле
     */
    JTextField createSearchField() {
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
                    g2.drawString("Пошук треків...", 23, y);
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
     * Створення іконки пошуку.
     *
     * @return JLabel з іконкою пошуку
     */
    JLabel createSearchIcon() {
        JLabel searchIcon = new JLabel("🔍") {
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
        searchIcon.setPreferredSize(new Dimension(30, 30));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        searchIcon.setOpaque(false);
        return searchIcon;
    }

    /**
     * Створення кнопки очищення поля пошуку.
     *
     * @return JPanel із кнопкою очищення
     */
    JPanel createClearButtonPanel() {
        JButton clearButton = new JButton("×") {
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

        clearButton.setPreferredSize(new Dimension(24, 24));
        clearButton.setMaximumSize(new Dimension(24, 24));
        clearButton.setContentAreaFilled(false);
        clearButton.setOpaque(false);
        clearButton.setFocusPainted(false);
        clearButton.setBorder(BorderFactory.createEmptyBorder());
        clearButton.addActionListener(e -> {
            logger.info("Очищення поля пошуку");
            searchField.setText("");
            searchField.requestFocus();
        });

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.add(clearButton, BorderLayout.EAST);
        return rightPanel;
    }

    /**
     * Налаштування слухача подій для пошуку в реальному часі.
     */
    void setupSearchListener() {
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTracks();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTracks();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTracks();
            }
        });
    }

    /**
     * Фільтрація треків на основі тексту пошуку.
     */
    void filterTracks() {
        try {
            String searchText = searchField.getText().trim().toLowerCase();
            filteredModel.clear();
            logger.debug("Фільтрація треків за запитом: '{}', кількість треків у allTracks: {}", searchText, allTracks.size());

            if (searchText.isEmpty()) {
                trackList.setModel(originalModel);
                trackList.repaint();
                trackList.revalidate();
                logger.info("Порожній запит, відображено всі треки: {}", originalModel.size());
            } else {
                for (MusicTrack track : allTracks) {
                    if (matchesSearch(track, searchText)) {
                        filteredModel.addElement(track);
                    }
                }
                trackList.setModel(filteredModel);
                trackList.repaint();
                trackList.revalidate();
                logger.info("Знайдено {} треків", filteredModel.size());
            }
        } catch (Exception e) {
            logger.error("Помилка фільтрації: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(null, "Помилка під час пошуку треків", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Перевірка, чи відповідає трек пошуковому запиту.
     *
     * @param track      Трек для перевірки
     * @param searchText Текст пошукового запиту
     * @return true, якщо трек відповідає запиту
     */
    boolean matchesSearch(MusicTrack track, String searchText) {
        try {
            if (track == null) {
                logger.warn("Трек є null");
                return false;
            }
            String title = track.getTitle() != null ? track.getTitle().toLowerCase() : "";
            String artist = track.getArtist() != null ? track.getArtist().toLowerCase() : "";
            String genre = track.getGenre() != null ? track.getGenre().toString().toLowerCase() : "";
            boolean matches = title.contains(searchText) ||
                    artist.contains(searchText) ||
                    genre.contains(searchText);
            logger.trace("Трек {}: title='{}', artist='{}', genre='{}', збіг={}",
                    track.getTitle(), title, artist, genre, matches);
            return matches;
        } catch (Exception e) {
            logger.error("Помилка перевірки треку {}: {}", track != null ? track.getTitle() : "null", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Оновлення списку треків.
     *
     * @param tracks Новий список треків
     */
    public void updateTrackList(List<MusicTrack> tracks) {
        try {
            logger.info("Оновлення списку треків: {}", tracks.size());
            allTracks = new ArrayList<>(tracks);
            filterTracks();
        } catch (Exception e) {
            logger.error("Помилка оновлення треків: {}", e.getMessage(), e);
        }
    }
}