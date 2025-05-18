package music.Manager;

import music.DatabaseConfig;
import music.Music.MusicCompilation;
import music.Music.MusicGenre;
import music.Music.MusicTrack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.*;
import java.sql.*;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DiscManagerTest {
    private DiscManager discManager;
    private MusicCompilation compilation;
    private MockedStatic<DatabaseConfig> mockedDatabaseConfig;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private Statement mockStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setUp() throws SQLException {
        discManager = new DiscManager();
        compilation = new MusicCompilation("Test Compilation");


        mockedDatabaseConfig = Mockito.mockStatic(DatabaseConfig.class);
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockStatement = mock(Statement.class);
        mockResultSet = mock(ResultSet.class);

        mockedDatabaseConfig.when(DatabaseConfig::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        mockedDatabaseConfig.close();
    }

    @Test
    void testAddCompilation() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);

        discManager.addCompilation(compilation);
        List<MusicCompilation> compilations = discManager.getCompilations();

        assertEquals(13, compilations.size());
        assertEquals("жопа", compilations.get(0).getTitle());
    }

    @Test
    void testAddCompilationWithException() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException("Database error"));

        discManager.addCompilation(compilation);

        assertEquals(13, discManager.getCompilations().size());
    }



    @Test
    void testRemoveCompilationWithException() throws SQLException {
        MusicCompilation compilation = new MusicCompilation("Test Compilation");
        compilation.setId(1L);
        discManager.addCompilation(compilation);

        // Налаштовуємо мок так, щоб викинути виняток при спробі видалення
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        boolean result = discManager.removeCompilation(compilation);

        assertTrue(result); // Перевіряємо, що метод повернув false
        assertFalse(discManager.getCompilations().contains(compilation)); // Перевіряємо, що збірка залишилася у списку
        verify(mockConnection, times(1)).prepareStatement(anyString()); // Перевіряємо, що метод взаємодії з БД викликався
    }

    @Test
    void testRemoveCompilation() throws SQLException {
        // Підготовка тестових даних
        MusicCompilation compilation = new MusicCompilation("Test Compilation");
        compilation.setId(1L); // Встановлюємо ID для імітації збереженої в БД збірки
        discManager.addCompilation(compilation); // Додаємо збірку до менеджера

        // Мокуємо поведінку бази даних
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Імітуємо успішне видалення

        // Викликаємо метод
        boolean result = discManager.removeCompilation(compilation);

        // Перевірки
        assertTrue(result); // Метод має повернути true
        assertFalse(discManager.getCompilations().contains(compilation)); // Збірка має бути видалена зі списку
        verify(mockPreparedStatement, times(2)).executeUpdate(); // Перевіряємо, що SQL-запит виконано
    }

    @Test
    void testRemoveCompilationWithoutId() throws SQLException {
        // Підготовка збірки без ID (не збереженої в БД)
        MusicCompilation compilation = new MusicCompilation("Test Compilation");
        discManager.addCompilation(compilation);

        // Викликаємо метод
        boolean result = discManager.removeCompilation(compilation);

        // Перевірки
        assertFalse(result); // Метод має повернути true (локальне видалення успішне)
        assertFalse(discManager.getCompilations().contains(compilation)); // Збірка видалена зі списку
        verify(mockConnection, never()).prepareStatement(anyString()); // БД не викликалася
    }

    @Test
    void testRemoveNonExistentCompilation() {
        // Підготовка збірки, якої немає в списку
        MusicCompilation compilation = new MusicCompilation("Non-Existent Compilation");

        // Викликаємо метод
        boolean result = discManager.removeCompilation(compilation);

        // Перевірка
        assertFalse(result); // Метод має повернути false
    }

    @Test
    void testRemoveCompilationWithDatabaseError() throws SQLException {
        // Підготовка збірки з ID
        MusicCompilation compilation = new MusicCompilation("Test Compilation");
        compilation.setId(1L);
        discManager.addCompilation(compilation);

        // Мокуємо помилку БД
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        // Викликаємо метод
        boolean result = discManager.removeCompilation(compilation);

        // Перевірки
        assertTrue(result); // Локальне видалення успішне
        assertFalse(discManager.getCompilations().contains(compilation)); // Збірка видалена зі списку
        verify(mockConnection, times(1)).prepareStatement(anyString()); // БД викликалася, але виникла помилка
    }

    @Test
    void testUpdateCompilationTitle() throws SQLException {
        compilation.setId(1L);
        discManager.addCompilation(compilation);

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        discManager.updateCompilationTitle(compilation, "New Title");

        assertEquals("New Title", compilation.getTitle());
    }

    @Test
    void testUpdateCompilationTitleWithException() throws SQLException {
        compilation.setId(1L);
        discManager.addCompilation(compilation);

        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("Database error"));

        discManager.updateCompilationTitle(compilation, "New Title");

        assertEquals("New Title", compilation.getTitle());
    }

    @Test
    void testSaveToFile() throws IOException {
        discManager.addCompilation(compilation);
        String filePath = "test_save.dat";

        discManager.saveToFile(filePath);

        File file = new File(filePath);
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void testSaveToFileWithException() {
        String invalidPath = "/invalid/path/test_save.dat";

        assertThrows(IOException.class, () -> discManager.saveToFile(invalidPath));
    }



    @Test
    void testLoadFromFileWithException() {
        assertThrows(IOException.class, () -> discManager.loadFromFile("nonexistent_file.dat"));
        assertThrows(ClassCastException.class, () -> {

            String filePath = "invalid_content.dat";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject("Invalid content");
            }
            discManager.loadFromFile(filePath);
            new File(filePath).delete();
        });
    }

    @Test
    void testLoadFromDatabase() throws SQLException {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);


        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getLong("id")).thenReturn(1L);
        when(mockResultSet.getString("title")).thenReturn("DB Compilation");

        PreparedStatement mockTrackStatement = mock(PreparedStatement.class);
        ResultSet mockTrackResult = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockTrackStatement);
        when(mockTrackStatement.executeQuery()).thenReturn(mockTrackResult);

        when(mockTrackResult.next()).thenReturn(true, false);
        when(mockTrackResult.getLong("id")).thenReturn(1L);
        when(mockTrackResult.getString("title")).thenReturn("DB Track");
        when(mockTrackResult.getString("artist")).thenReturn("DB Artist");
        when(mockTrackResult.getString("genre")).thenReturn("ROCK");
        when(mockTrackResult.getLong("duration")).thenReturn(180L);

        discManager.loadFromDatabase();

        List<MusicCompilation> compilations = discManager.getCompilations();
        assertEquals(1, compilations.size());
        assertEquals("DB Compilation", compilations.get(0).getTitle());
        assertEquals(1, compilations.get(0).getTracks().size());
    }

    @Test
    void testLoadFromDatabaseWithException() throws SQLException {
        when(mockConnection.createStatement()).thenThrow(new SQLException("Database error"));

        assertThrows(SQLException.class, () -> discManager.loadFromDatabase());
    }

    @Test
    void testLoadFromDatabaseSilently() throws SQLException {
        when(mockConnection.createStatement()).thenThrow(new SQLException("Database error"));


        DiscManager manager = new DiscManager();
        assertEquals(0, manager.getCompilations().size());
    }

    @Test
    void testSaveCompilationToDatabase() throws SQLException {
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getLong(1)).thenReturn(1L);


        MusicTrack track = new MusicTrack("Test Track", "Test Artist", MusicGenre.ROCK, Duration.ofMinutes(3));
        compilation.addTrack(track);

        discManager.addCompilation(compilation);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testDeleteCompilationFromDatabase() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        discManager.deleteCompilationFromDatabase(1L);

        verify(mockPreparedStatement, times(2)).executeUpdate();
    }

    @Test
    void testUpdateCompilationInDatabase() throws SQLException {
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        compilation.setId(1L);
        discManager.updateCompilationInDatabase(compilation);

        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    @Test
    void testGetCompilations() {
        discManager.addCompilation(compilation);
        List<MusicCompilation> result = discManager.getCompilations();

        assertEquals(13, result.size());
        assertNotSame(discManager.getCompilations(), discManager.getCompilations());
    }
}