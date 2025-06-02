package music.Manager;

import music.DatabaseConfig;
import music.Dialog.CompilationDetailsDialog;
import music.Models.MusicCompilation;
import music.Service.MusicCompilationService;
import music.Models.MusicTrack;
import music.Panel.HeaderPanel;
import music.Panel.TrackListPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Клас для управління операціями з базою даних для треків музичних компіляцій.
 * Надає методи для додавання, оновлення, видалення та сортування треків,
 * а також синхронізації даних між UI та базою даних.
 */
public class TrackDatabaseManager {
    private static final Logger logger = LogManager.getLogger(TrackDatabaseManager.class);
    private static final MusicCompilationService compilationService=new MusicCompilationService();


    /**
     * Додає новий трек до компіляції та зберігає його в базі даних.
     *
     * @param parent         Діалогове вікно, що містить UI компіляції
     * @param compilation   Компіляція, до якої додається трек
     * @param trackListPanel Панель зі списком треків
     * @param track         Трек, який потрібно додати
     * @throws RuntimeException Якщо сталася помилка при додаванні треку
     */
    public static void addTrackToCompilation(CompilationDetailsDialog parent, MusicCompilation compilation,
                                             TrackListPanel trackListPanel, MusicTrack track) {
        logger.info("Додавання треку '{}' до компіляції '{}'", track.getTitle(), compilation.getId());
        try {
            trackListPanel.getTrackListModel().addElement(track);
            updateCompilationTracks(compilation, trackListPanel);
            saveTrackToDatabase(parent, track, compilation);
            updateHeaderInfo(parent, compilation, trackListPanel);
            logger.info("Трек '{}' успішно додано до компіляції '{}'", track.getTitle(), compilation.getId());
        } catch (Exception ex) {
            logger.error("Помилка при додаванні треку '{}': {}", track.getTitle(), ex.getMessage(), ex);
            throw new RuntimeException("Не вдалося додати трек: " + ex.getMessage(), ex);
        }
    }

    /**
     * Оновлює інформацію про трек у базі даних та UI.
     *
     * @param parent         Діалогове вікно, що містить UI компіляції
     * @param trackListPanel Панель зі списком треків
     * @param track         Трек, який потрібно оновити
     * @throws RuntimeException Якщо сталася помилка при оновленні треку
     */
    public static void updateTrack(CompilationDetailsDialog parent, TrackListPanel trackListPanel, MusicTrack track) {
        logger.info("Оновлення треку '{}'", track.getTitle());
        try {
            updateTrackInDatabase(parent, track);
            trackListPanel.getTrackListModel().set(trackListPanel.getTrackList().getSelectedIndex(), track);
            updateHeaderInfo(parent, trackListPanel.compilation, trackListPanel);
            logger.info("Трек '{}' успішно оновлено", track.getTitle());
        } catch (Exception ex) {
            logger.error("Помилка при оновленні треку '{}': {}", track.getTitle(), ex.getMessage(), ex);
            throw new RuntimeException("Не вдалося оновити трек: " + ex.getMessage(), ex);
        }
    }

    /**
     * Видаляє вибраний трек із компіляції та бази даних після підтвердження користувача.
     *
     * @param parent         Діалогове вікно, що містить UI компіляції
     * @param trackListPanel Панель зі списком треків
     * @param compilation   Компіляція, з якої видаляється трек
     */
    public static void deleteSelectedTrack(CompilationDetailsDialog parent, TrackListPanel trackListPanel,
                                           MusicCompilation compilation) {
        MusicTrack selectedTrack = trackListPanel.getTrackList().getSelectedValue();
        if (selectedTrack == null) {
            logger.warn("Не вибрано трек для видалення");
            JOptionPane.showMessageDialog(parent,
                    "Будь ласка, виберіть трек для видалення",
                    "Попередження",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        logger.info("Запит на видалення треку '{}'", selectedTrack.getTitle());
        int confirm = JOptionPane.showConfirmDialog(
                parent,
                "Ви впевнені, що хочете видалити трек '" + selectedTrack.getTitle() + "'?",
                "Підтвердження видалення",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                deleteTrackFromDatabase(parent, selectedTrack);
                trackListPanel.getTrackListModel().removeElement(selectedTrack);
                updateCompilationTracks(compilation, trackListPanel);
                JOptionPane.showMessageDialog(parent,
                        "Трек '" + selectedTrack.getTitle() + "' успішно видалено",
                        "Видалення треку",
                        JOptionPane.INFORMATION_MESSAGE);
                updateHeaderInfo(parent, compilation, trackListPanel);
                logger.info("Трек '{}' успішно видалено", selectedTrack.getTitle());
            } catch (Exception ex) {
                logger.error("Помилка при видаленні треку '{}': {}", selectedTrack.getTitle(), ex.getMessage(), ex);
                JOptionPane.showMessageDialog(parent,
                        "Помилка при видаленні треку: " + ex.getMessage(),
                        "Помилка",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            logger.info("Видалення треку '{}' скасовано", selectedTrack.getTitle());
        }
    }

    /**
     * Сортує треки компіляції за жанром та оновлює базу даних.
     *
     * @param trackListPanel Панель зі списком треків
     * @param compilation   Компіляція, треки якої потрібно відсортувати
     * @throws RuntimeException Якщо сталася помилка при сортуванні
     */
    public static void sortTracksByGenre(TrackListPanel trackListPanel, MusicCompilation compilation) {
        logger.info("Сортування треків за жанром для компіляції '{}'", compilation.getId());
        try {
            List<MusicTrack> tracks = new ArrayList<>();
            for (int i = 0; i < trackListPanel.getTrackListModel().getSize(); i++) {
                tracks.add(trackListPanel.getTrackListModel().get(i));
            }


            tracks.sort(Comparator.comparing(track -> track.getGenre().toString()));

            trackListPanel.getTrackListModel().clear();
            tracks.forEach(trackListPanel.getTrackListModel()::addElement);

            updateCompilationTracks(compilation, trackListPanel);
            updateTracksInDatabase((CompilationDetailsDialog) trackListPanel.getParent(), compilation, trackListPanel);

            JOptionPane.showMessageDialog(trackListPanel.getParent(),
                    "Треки успішно відсортовані за жанром",
                    "Сортування",
                    JOptionPane.INFORMATION_MESSAGE);
            logger.info("Треки успішно відсортовані за жанром для компіляції '{}'", compilation.getId());
        } catch (Exception ex) {
            logger.error("Помилка при сортуванні треків: {}", ex.getMessage(), ex);
            throw new RuntimeException("Не вдалося відсортувати треки: " + ex.getMessage(), ex);
        }
    }


    /**
     * Оновлює внутрішній список треків компіляції, використовуючи дані з UI.
     *
     * @param compilation   Компіляція, список треків якої оновлюється
     * @param trackListPanel Панель зі списком треків
     */
    private static void updateCompilationTracks(MusicCompilation compilation, TrackListPanel trackListPanel) {
        List<MusicTrack> updatedTracks = new ArrayList<>();
        for (int i = 0; i < trackListPanel.getTrackListModel().getSize(); i++) {
            updatedTracks.add(trackListPanel.getTrackListModel().get(i));
        }

        try {
            java.lang.reflect.Field tracksField = MusicCompilation.class.getDeclaredField("tracks");
            tracksField.setAccessible(true);
            List<MusicTrack> internalList = (List<MusicTrack>) tracksField.get(compilation);
            internalList.clear();
            internalList.addAll(updatedTracks);
        } catch (Exception ex) {
            logger.error("Помилка при оновленні внутрішнього списку треків: {}", ex.getMessage(), ex);
            throw new RuntimeException("Не вдалося оновити список треків: " + ex.getMessage(), ex);
        }
    }

    /**
     * Зберігає трек у базі даних та встановлює його згенерований ID.
     *
     * @param parent       Діалогове вікно для відображення помилок
     * @param track        Трек, який потрібно зберегти
     * @param compilation Компіляція, до якої належить трек
     */
    private static void saveTrackToDatabase(CompilationDetailsDialog parent, MusicTrack track, MusicCompilation compilation) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO tracks (title, artist, genre, duration, compilation_id) VALUES (?, ?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, track.getTitle());
            statement.setString(2, track.getArtist());
            statement.setString(3, track.getGenre().name());
            statement.setLong(4, track.getDuration().getSeconds());
            statement.setLong(5, compilation.getId());

            int affectedRows = statement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        track.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException ex) {
            logger.error("Помилка при збереженні треку '{}': {}", track.getTitle(), ex.getMessage(), ex);
            JOptionPane.showMessageDialog(parent,
                    "Помилка при збереженні треку: " + ex.getMessage(),
                    "Помилка бази даних",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Не вдалося зберегти трек: " + ex.getMessage(), ex);
        }
    }

    /**
     * Оновлює інформацію про трек у базі даних.
     *
     * @param parent Діалогове вікно для відображення помилок
     * @param track  Трек, який потрібно оновити
     */
    private static void updateTrackInDatabase(CompilationDetailsDialog parent, MusicTrack track) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE tracks SET title = ?, artist = ?, genre = ?, duration = ? WHERE id = ?")) {
            statement.setString(1, track.getTitle());
            statement.setString(2, track.getArtist());
            statement.setString(3, track.getGenre().name());
            statement.setLong(4, track.getDuration().getSeconds());
            statement.setLong(5, track.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Помилка при оновленні треку '{}': {}", track.getTitle(), ex.getMessage(), ex);
            JOptionPane.showMessageDialog(parent,
                    "Помилка при оновленні треку: " + ex.getMessage(),
                    "Помилка бази даних",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Не вдалося оновити трек: " + ex.getMessage(), ex);
        }
    }

    /**
     * Видаляє трек із бази даних.
     *
     * @param parent Діалогове вікно для відображення помилок
     * @param track  Трек, який потрібно видалити
     */
    private static void deleteTrackFromDatabase(CompilationDetailsDialog parent, MusicTrack track) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM tracks WHERE id = ?")) {
            statement.setLong(1, track.getId());
            statement.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Помилка при видаленні треку '{}': {}", track.getTitle(), ex.getMessage(), ex);
            JOptionPane.showMessageDialog(parent,
                    "Помилка при видаленні треку: " + ex.getMessage(),
                    "Помилка бази даних",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Не вдалося видалити трек: " + ex.getMessage(), ex);
        }
    }

    /**
     * Оновлює всі треки компіляції в базі даних, синхронізуючи їх із UI.
     *
     * @param parent         Діалогове вікно для відображення помилок
     * @param compilation   Компіляція, треки якої оновлюються
     * @param trackListPanel Панель зі списком треків
     */
    public static void updateTracksInDatabase(CompilationDetailsDialog parent, MusicCompilation compilation,
                                              TrackListPanel trackListPanel) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement deleteStatement = connection.prepareStatement(
                        "DELETE FROM tracks WHERE compilation_id = ?")) {
                    deleteStatement.setLong(1, compilation.getId());
                    deleteStatement.executeUpdate();
                }

                try (PreparedStatement insertStatement = connection.prepareStatement(
                        "INSERT INTO tracks (title, artist, genre, duration, compilation_id) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < trackListPanel.getTrackListModel().getSize(); i++) {
                        MusicTrack track = trackListPanel.getTrackListModel().get(i);
                        insertStatement.setString(1, track.getTitle());
                        insertStatement.setString(2, track.getArtist());
                        insertStatement.setString(3, track.getGenre().name());
                        insertStatement.setLong(4, track.getDuration().getSeconds());
                        insertStatement.setLong(5, compilation.getId());
                        insertStatement.executeUpdate();

                        try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                track.setId(generatedKeys.getLong(1));
                            }
                        }
                    }
                }

                connection.commit();
            } catch (SQLException ex) {
                connection.rollback();
                logger.error("Помилка при оновленні треків для компіляції '{}': {}", compilation.getId(), ex.getMessage(), ex);
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            logger.error("Помилка при оновленні треків у базі даних: {}", ex.getMessage(), ex);
            JOptionPane.showMessageDialog(parent,
                    "Помилка при оновленні треків у базі даних: " + ex.getMessage(),
                    "Помилка бази даних",
                    JOptionPane.ERROR_MESSAGE);
            throw new RuntimeException("Не вдалося оновити треки: " + ex.getMessage(), ex);
        }
    }

    /**
     * Оновлює інформацію в заголовку UI компіляції (кількість треків, загальна тривалість).
     *
     * @param parent         Діалогове вікно, що містить UI компіляції
     * @param compilation   Компіляція, для якої оновлюється заголовок
     * @param trackListPanel Панель зі списком треків
     */
    private static void updateHeaderInfo(CompilationDetailsDialog parent, MusicCompilation compilation,
                                         TrackListPanel trackListPanel) {
        try {
            JPanel mainPanel = (JPanel) parent.getContentPane().getComponent(0);
            HeaderPanel headerPanel = new HeaderPanel(compilation);
            headerPanel.updateInfo(
                    trackListPanel.getTrackListModel().getSize(),
                    compilationService.calculateTotalDuration(compilation.getTracks()).toMinutes(),
                    compilationService.calculateTotalDuration(compilation.getTracks()).getSeconds()
            );
        } catch (Exception ex) {
            logger.error("Помилка при оновленні інформації заголовка: {}", ex.getMessage(), ex);
            throw new RuntimeException("Не вдалося оновити заголовок: " + ex.getMessage(), ex);
        }
    }
}