package music.Manager;

import music.DatabaseConfig;
import music.Models.MusicCompilation;
import music.Models.MusicGenre;
import music.Models.MusicTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Клас DiscManager відповідає за управління музичними збірками, включаючи їх створення, видалення, оновлення,
 * збереження та завантаження з бази даних або файлу. Забезпечує взаємодію з базою даних та серіалізацію даних.
 */
public class DiscManager {
    private static final Logger logger = LogManager.getLogger(DiscManager.class);
    private final List<MusicCompilation> compilations;


    /**
     * Конструктор ініціалізує список збірок та завантажує дані з бази даних.
     */
    public DiscManager() {
        this.compilations = new ArrayList<>();
        logger.info("Ініціалізація DiscManager");
        loadFromDatabaseSilently();
    }

    /**
     * Додає нову музичну збірку до списку та зберігає її в базі даних.
     *
     * @param compilation Музична збірка для додавання.
     */
    public void addCompilation(MusicCompilation compilation) {
        try {
            compilations.add(compilation);
            saveCompilationToDatabase(compilation);
            logger.info("Додано нову збірку: {}", compilation.getTitle());
        } catch (Exception e) {
            logger.error("Помилка при додаванні збірки {}: {}", compilation.getTitle(), e.getMessage());
        }
    }

    /**
     * Видаляє музичну збірку зі списку та бази даних.
     *
     * @param compilation Музична збірка для видалення.
     * @return true, якщо збірку успішно видалено; false у разі помилки.
     */
    public boolean removeCompilation(MusicCompilation compilation) {
        try {
            boolean removed = compilations.remove(compilation);
            if (removed && compilation.getId() > 0) {
                deleteCompilationFromDatabase(compilation.getId());
                logger.info("Видалено збірку: {}", compilation.getTitle());
            }
            return removed;
        } catch (Exception e) {
            logger.error("Помилка при видаленні збірки {}: {}", compilation.getTitle(), e.getMessage());
            return false;
        }
    }

    /**
     * Оновлює назву музичної збірки в списку та базі даних.
     *
     * @param compilation Музична збірка, назву якої потрібно оновити.
     * @param newTitle Нова назва збірки.
     */
    public void updateCompilationTitle(MusicCompilation compilation, String newTitle) {
        try {
            compilation.setTitle(newTitle);
            if (compilation.getId() > 0) {
                updateCompilationInDatabase(compilation);
                logger.info("Оновлено назву збірки, ID {}: {}", compilation.getId(), newTitle);
            }
        } catch (Exception e) {
            logger.error("Помилка при оновленні назви збірки, ID {}: {}", compilation.getId(), e.getMessage());
        }
    }

    /**
     * Зберігає список збірок у файл через серіалізацію.
     *
     * @param filePath Шлях до файлу для збереження.
     * @throws IOException У разі помилки вводу-виводу.
     */
    public void saveToFile(String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(compilations);
            logger.info("Дані збережено у файл: {}", filePath);
        } catch (IOException e) {
            logger.error("Помилка збереження у файл {}: {}", filePath, e.getMessage());
            throw e;
        }
    }

    /**
     * Завантажує список збірок з файлу через десеріалізацію.
     *
     * @param filePath Шлях до файлу для завантаження.
     * @throws IOException У разі помилки вводу-виводу.
     * @throws ClassNotFoundException У разі відсутності класу для десеріалізації.
     */
    @SuppressWarnings("unchecked")
    public void loadFromFile(String filePath) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            compilations.clear();
            compilations.addAll((List<MusicCompilation>) ois.readObject());
            logger.info("Дані завантажено з файлу: {}", filePath);
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Помилка завантаження з файлу {}: {}", filePath, e.getMessage());
            throw e;
        }
    }

    /**
     * Завантажує дані з бази даних без викидання виключень.
     */
    private void loadFromDatabaseSilently() {
        try {
            loadFromDatabase();
            logger.info("Дані успішно завантажено з бази даних");
        } catch (SQLException e) {
            logger.error("Помилка завантаження даних з бази даних: {}", e.getMessage());
        }
    }

    /**
     * Завантажує всі збірки та їх треки з бази даних.
     *
     * @throws SQLException У разі помилки доступу до бази даних.
     */
    public void loadFromDatabase() throws SQLException {
        try (Connection connection = DatabaseConfig.getConnection()) {
            compilations.clear();
            String selectCompilationsSQL = "SELECT * FROM compilations";
            try (Statement compilationStatement = connection.createStatement();
                 ResultSet compilationResult = compilationStatement.executeQuery(selectCompilationsSQL)) {

                while (compilationResult.next()) {
                    MusicCompilation compilation = new MusicCompilation(compilationResult.getString("title"));
                    compilation.setId(compilationResult.getLong("id"));

                    String selectTracksSQL = "SELECT * FROM tracks WHERE compilation_id = ?";
                    try (PreparedStatement trackStatement = connection.prepareStatement(selectTracksSQL)) {
                        trackStatement.setLong(1, compilation.getId());
                        try (ResultSet trackResult = trackStatement.executeQuery()) {
                            while (trackResult.next()) {
                                MusicTrack track = new MusicTrack(
                                        trackResult.getString("title"),
                                        trackResult.getString("artist"),
                                        MusicGenre.valueOf(trackResult.getString("genre")),
                                        Duration.ofSeconds(trackResult.getLong("duration"))
                                );
                                track.setId(trackResult.getLong("id"));
                                compilation.addTrack(track);
                            }
                        }
                    }
                    compilations.add(compilation);
                }
            }
            logger.info("Завантажено {} збірок з бази даних", compilations.size());
        } catch (SQLException e) {
            logger.error("Помилка завантаження даних з бази даних: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Зберігає музичну збірку та її треки в базі даних.
     *
     * @param compilation Музична збірка для збереження.
     */
    private void saveCompilationToDatabase(MusicCompilation compilation) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            String insertCompilationSQL = "INSERT INTO compilations (title) VALUES (?)";
            try (PreparedStatement compilationStatement = connection.prepareStatement(insertCompilationSQL, Statement.RETURN_GENERATED_KEYS)) {
                compilationStatement.setString(1, compilation.getTitle());
                compilationStatement.executeUpdate();

                try (ResultSet generatedKeys = compilationStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        long compilationId = generatedKeys.getLong(1);
                        compilation.setId(compilationId);

                        String insertTrackSQL = "INSERT INTO tracks (title, artist, genre, duration, compilation_id) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement trackStatement = connection.prepareStatement(insertTrackSQL)) {
                            for (MusicTrack track : compilation.getTracks()) {
                                trackStatement.setString(1, track.getTitle());
                                trackStatement.setString(2, track.getArtist());
                                trackStatement.setString(3, track.getGenre().name());
                                trackStatement.setLong(4, track.getDuration().toSeconds());
                                trackStatement.setLong(5, compilationId);
                                trackStatement.executeUpdate();
                            }
                        }
                        logger.info("Збережено збірку в базу даних: {}", compilation.getTitle());
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Помилка збереження збірки {} в базу даних: {}", compilation.getTitle(), e.getMessage());
        }
    }

    /**
     * Видаляє музичну збірку та її треки з бази даних.
     *
     * @param compilationId Ідентифікатор збірки для видалення.
     */
    void deleteCompilationFromDatabase(long compilationId) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            String deleteTracksSQL = "DELETE FROM tracks WHERE compilation_id = ?";
            try (PreparedStatement deleteTracksStatement = connection.prepareStatement(deleteTracksSQL)) {
                deleteTracksStatement.setLong(1, compilationId);
                deleteTracksStatement.executeUpdate();
            }

            String deleteCompilationSQL = "DELETE FROM compilations WHERE id = ?";
            try (PreparedStatement deleteCompilationStatement = connection.prepareStatement(deleteCompilationSQL)) {
                deleteCompilationStatement.setLong(1, compilationId);
                deleteCompilationStatement.executeUpdate();
                logger.info("Видалено збірку з бази даних, ID: {}", compilationId);
            }
        } catch (SQLException e) {
            logger.error("Помилка видалення збірки з бази даних, ID {}: {}", compilationId, e.getMessage());
        }
    }

    /**
     * Оновлює дані музичної збірки в базі даних.
     *
     * @param compilation Музична збірка для оновлення.
     */
    void updateCompilationInDatabase(MusicCompilation compilation) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            String updateSQL = "UPDATE compilations SET title = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateSQL)) {
                statement.setString(1, compilation.getTitle());
                statement.setLong(2, compilation.getId());
                statement.executeUpdate();
                logger.info("Оновлено збірку в базі даних, ID: {}", compilation.getId());
            }
        } catch (SQLException e) {
            logger.error("Помилка оновлення збірки в базі даних, ID {}: {}", compilation.getId(), e.getMessage());
        }
    }

    /**
     * Повертає копію списку всіх музичних збірок.
     *
     * @return Список музичних збірок.
     */
    public List<MusicCompilation> getCompilations() {
        logger.debug("Отримано список збірок, кількість: {}", compilations.size());
        return new ArrayList<>(compilations);
    }
}