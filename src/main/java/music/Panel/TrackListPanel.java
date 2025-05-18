package music.Panel;

import music.Dialog.CompilationDetailsDialog;
import music.Dialog.TrackDetailsDialog;
import music.Music.MusicCompilation;
import music.Music.MusicTrack;
import music.Music.TrackListDragAndDropHandler;
import music.Renderer.ModernTrackListRenderer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Панель для відображення та управління списком треків у музичній збірці.
 * Забезпечує функціонал для відображення треків, фільтрації за тривалістю,
 * перетягування треків (drag-and-drop) та перегляду деталей треку.
 */
public class TrackListPanel {
    private static final Logger logger = LogManager.getLogger(TrackListPanel.class);
    private final JPanel panel;
    private final DefaultListModel<MusicTrack> trackListModel;
    private final JList<MusicTrack> trackList;
    List<MusicTrack> allTracks;
    private final TrackSearchPanel searchPanel;
    private final CompilationDetailsDialog parent;
    public MusicCompilation compilation;

    /**
     * Конструктор панелі списку треків.
     *
     * @param parent       Діалогове вікно, що містить цю панель.
     * @param compilation  Музична збірка, треки якої відображаються.
     * @throws RuntimeException Якщо ініціалізація панелі завершується з помилкою.
     */
    public TrackListPanel(CompilationDetailsDialog parent, MusicCompilation compilation) {
        this.parent = parent;
        this.compilation = compilation;
        this.panel = new JPanel(new BorderLayout());
        this.trackListModel = new DefaultListModel<>();
        this.trackList = new JList<>(trackListModel);
        this.searchPanel = new TrackSearchPanel(trackList, trackListModel);
        this.allTracks = new ArrayList<>();

        try {
            initializePanel();
            initializeTrackList();
            loadTracksFromCompilation();
            logger.info("Панель списку треків ініціалізовано для компіляції: {}", compilation.getName());
        } catch (Exception e) {
            logger.error("Помилка ініціалізації панелі списку треків: {}", e.getMessage(), e);
            throw new RuntimeException("Не вдалося ініціалізувати панель списку треків", e);
        }
    }

    /**
     * Ініціалізує основну панель з рамкою та компонентами.
     */
    private void initializePanel() {
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createEmptyBorder(),
                        "Треки",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Segoe UI", Font.BOLD, 14),
                        new Color(70, 70, 70)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panel.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(trackList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Ініціалізує список треків із налаштуваннями стилю та поведінки.
     */
    private void initializeTrackList() {
        trackList.setOpaque(false);
        trackList.setCellRenderer(new ModernTrackListRenderer());
        trackList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trackList.setBackground(new Color(255, 255, 255, 200));

        new TrackListDragAndDropHandler(trackList, trackListModel, this, parent);

        trackList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    showTrackDetails(trackList.getSelectedValue());
                }
            }
        });
    }

    /**
     * Завантажує треки зі збірки до моделі списку.
     *
     * @throws RuntimeException Якщо завантаження треків завершується з помилкою.
     */
    void loadTracksFromCompilation() {
        try {
            trackListModel.clear();
            compilation.getTracks().forEach(trackListModel::addElement);
            List<MusicTrack> tracks = new ArrayList<>();
            for (int i = 0; i < trackListModel.size(); i++) {
                tracks.add(trackListModel.get(i));
            }
            allTracks = new ArrayList<>(tracks); // Оновлення allTracks
            searchPanel.updateTrackList(tracks); // Синхронізація з TrackSearchPanel
            logger.info("Завантажено {} треків із компіляції: {}", trackListModel.size(), compilation.getName());
        } catch (Exception e) {
            logger.error("Помилка завантаження треків: {}", e.getMessage(), e);
            throw new RuntimeException("Не вдалося завантажити треки", e);
        }
    }

    /**
     * Відображає діалогове вікно з деталями вибраного треку.
     *
     * @param track Трек, деталі якого потрібно відобразити.
     */
    void showTrackDetails(MusicTrack track) {
        try {
            if (track != null) {
                TrackDetailsDialog dialog = new TrackDetailsDialog((JFrame) parent.getParent(), track);
                dialog.setVisible(true);
                logger.info("Відображено деталі треку: {}", track.getTitle());
            } else {
                logger.warn("Спроба відкрити деталі невибраного треку");
            }
        } catch (Exception e) {
            logger.error("Помилка відображення деталей треку: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(parent, "Не вдалося відкрити деталі треку", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Фільтрує треки за тривалістю та оновлює відображення.
     *
     * @param minDuration  Мінімальна тривалість треку.
     * @param maxDuration  Максимальна тривалість треку.
     * @param headerPanel  Панель заголовка для оновлення інформації.
     */
    public void filterTracksByDuration(Duration minDuration, Duration maxDuration, HeaderPanel headerPanel) {
        try {
            if (allTracks == null || allTracks.isEmpty()) {
                allTracks = new ArrayList<>();
                for (int i = 0; i < trackListModel.getSize(); i++) {
                    allTracks.add(trackListModel.get(i));
                }
            }

            List<MusicTrack> filteredTracks = allTracks.stream()
                    .filter(track -> {
                        Duration duration = track.getDuration();
                        return duration.compareTo(minDuration) >= 0 && duration.compareTo(maxDuration) <= 0;
                    })
                    .toList();

            trackListModel.clear();
            filteredTracks.forEach(trackListModel::addElement);
            searchPanel.updateTrackList(filteredTracks); // Синхронізація з TrackSearchPanel

            Duration totalDuration = calculateFilteredTotalDuration();
            headerPanel.updateFilterInfo(
                    trackListModel.getSize(),
                    totalDuration.toMinutes(),
                    totalDuration.getSeconds(),
                    minDuration.toMinutes(),
                    minDuration.getSeconds(),
                    maxDuration.toMinutes(),
                    maxDuration.getSeconds()
            );

            logger.info("Фільтрація завершена: знайдено {} треків у діапазоні від {} до {} хвилин",
                    filteredTracks.size(), minDuration.toMinutes(), maxDuration.toMinutes());

            JOptionPane.showMessageDialog(parent,
                    filteredTracks.isEmpty() ? "Не знайдено треків у вказаному діапазоні тривалості" :
                            "Знайдено " + filteredTracks.size() + " треків у вказаному діапазоні",
                    "Результат фільтрації",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            logger.error("Помилка фільтрації треків: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(parent, "Не вдалося виконати фільтрацію треків", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Обчислює загальну тривалість відфільтрованих треків.
     *
     * @return Загальна тривалість у форматі Duration.
     * @throws RuntimeException Якщо обчислення завершується з помилкою.
     */
    Duration calculateFilteredTotalDuration() {
        try {
            long totalSeconds = 0;
            for (int i = 0; i < trackListModel.getSize(); i++) {
                totalSeconds += trackListModel.get(i).getDuration().getSeconds();
            }
            return Duration.ofSeconds(totalSeconds);
        } catch (Exception e) {
            logger.error("Помилка розрахунку загальної тривалості: {}", e.getMessage(), e);
            throw new RuntimeException("Не вдалося розрахувати загальну тривалість", e);
        }
    }

    /**
     * Скидає фільтр і відновлює повний список треків.
     *
     * @param headerPanel Панель заголовка для оновлення інформації.
     */
    public void resetFilter(HeaderPanel headerPanel) {
        try {
            if (allTracks != null && !allTracks.isEmpty()) {
                trackListModel.clear();
                allTracks.forEach(trackListModel::addElement);
                searchPanel.updateTrackList(allTracks); // Синхронізація з TrackSearchPanel

                headerPanel.updateInfo(
                        trackListModel.getSize(),
                        compilation.calculateTotalDuration().toMinutes(),
                        compilation.calculateTotalDuration().getSeconds()
                );

                logger.info("Фільтр скинуто, відновлено {} треків", trackListModel.getSize());
                JOptionPane.showMessageDialog(parent, "Фільтр скинуто", "Інформація", JOptionPane.INFORMATION_MESSAGE);
            } else {
                logger.warn("Спроба скинути фільтр, але список треків порожній або не ініціалізований");
            }
        } catch (Exception e) {
            logger.error("Помилка скидання фільтру: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(parent, "Не вдалося скинути фільтр", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Повертає основну панель.
     *
     * @return Об'єкт JPanel.
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * Повертає список треків.
     *
     * @return Об'єкт JList<MusicTrack>.
     */
    public JList<MusicTrack> getTrackList() {
        return trackList;
    }

    /**
     * Повертає модель списку треків.
     *
     * @return Об'єкт DefaultListModel<MusicTrack>.
     */
    public DefaultListModel<MusicTrack> getTrackListModel() {
        return trackListModel;
    }

    /**
     * Повертає батьківське діалогове вікно.
     *
     * @return Об'єкт CompilationDetailsDialog.
     */
    public CompilationDetailsDialog getParent() {
        return parent;
    }
}