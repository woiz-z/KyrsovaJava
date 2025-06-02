package music.Models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Duration;

class MusicTrackTest {
    private MusicTrack validTrack;
    private final String validTitle = "Test Title";
    private final String validArtist = "Test Artist";
    private final MusicGenre validGenre = MusicGenre.ROCK;
    private final Duration validDuration = Duration.ofMinutes(3);

    @BeforeEach
    void setUp() {
        validTrack = new MusicTrack(validTitle, validArtist, validGenre, validDuration);
    }

    @Test
    @DisplayName("Test valid MusicTrack construction")
    void testValidConstruction() {
        assertNotNull(validTrack);
        assertEquals(validTitle, validTrack.getTitle());
        assertEquals(validArtist, validTrack.getArtist());
        assertEquals(validGenre, validTrack.getGenre());
        assertEquals(validDuration, validTrack.getDuration());
    }

    @Test
    @DisplayName("Test construction with null title")
    void testConstructionWithNullTitle() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new MusicTrack(null, validArtist, validGenre, validDuration));
        assertEquals("Назва треку не може бути порожньою", exception.getMessage());
    }

    @Test
    @DisplayName("Test construction with empty title")
    void testConstructionWithEmptyTitle() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new MusicTrack("", validArtist, validGenre, validDuration));
        assertEquals("Назва треку не може бути порожньою", exception.getMessage());
    }

    @Test
    @DisplayName("Test construction with null artist")
    void testConstructionWithNullArtist() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new MusicTrack(validTitle, null, validGenre, validDuration));
        assertEquals("Виконавець не може бути порожнім", exception.getMessage());
    }

    @Test
    @DisplayName("Test construction with empty artist")
    void testConstructionWithEmptyArtist() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new MusicTrack(validTitle, "", validGenre, validDuration));
        assertEquals("Виконавець не може бути порожнім", exception.getMessage());
    }

    @Test
    @DisplayName("Test construction with null genre")
    void testConstructionWithNullGenre() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new MusicTrack(validTitle, validArtist, null, validDuration));
        assertEquals("Жанр не може бути null", exception.getMessage());
    }

    @Test
    @DisplayName("Test construction with null duration")
    void testConstructionWithNullDuration() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new MusicTrack(validTitle, validArtist, validGenre, null));
        assertEquals("Тривалість має бути додатнім значенням", exception.getMessage());
    }

    @Test
    @DisplayName("Test construction with negative duration")
    void testConstructionWithNegativeDuration() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new MusicTrack(validTitle, validArtist, validGenre, Duration.ofMinutes(-1)));
        assertEquals("Тривалість має бути додатнім значенням", exception.getMessage());
    }

    @Test
    @DisplayName("Test construction with zero duration")
    void testConstructionWithZeroDuration() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new MusicTrack(validTitle, validArtist, validGenre, Duration.ZERO));
        assertEquals("Тривалість має бути додатнім значенням", exception.getMessage());
    }

    @Test
    @DisplayName("Test setId with valid value")
    void testSetIdValid() {
        Long testId = 1L;
        validTrack.setId(testId);
        assertEquals(testId, validTrack.getId());
    }

    @Test
    @DisplayName("Test setId with null")
    void testSetIdNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setId(null));
        assertEquals("ID не може бути null або від'ємним", exception.getMessage());
    }

    @Test
    @DisplayName("Test setId with negative value")
    void testSetIdNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setId(-1L));
        assertEquals("ID не може бути null або від'ємним", exception.getMessage());
    }

    @Test
    @DisplayName("Test setTitle with valid value")
    void testSetTitleValid() {
        String newTitle = "New Title";
        validTrack.setTitle(newTitle);
        assertEquals(newTitle, validTrack.getTitle());
    }

    @Test
    @DisplayName("Test setTitle with null")
    void testSetTitleNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setTitle(null));
        assertEquals("Назва треку не може бути порожньою", exception.getMessage());
    }

    @Test
    @DisplayName("Test setTitle with empty string")
    void testSetTitleEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setTitle(""));
        assertEquals("Назва треку не може бути порожньою", exception.getMessage());
    }

    @Test
    @DisplayName("Test setArtist with valid value")
    void testSetArtistValid() {
        String newArtist = "New Artist";
        validTrack.setArtist(newArtist);
        assertEquals(newArtist, validTrack.getArtist());
    }

    @Test
    @DisplayName("Test setArtist with null")
    void testSetArtistNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setArtist(null));
        assertEquals("Виконавець не може бути порожнім", exception.getMessage());
    }

    @Test
    @DisplayName("Test setArtist with empty string")
    void testSetArtistEmpty() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setArtist(""));
        assertEquals("Виконавець не може бути порожнім", exception.getMessage());
    }

    @Test
    @DisplayName("Test setGenre with valid value")
    void testSetGenreValid() {
        MusicGenre newGenre = MusicGenre.POP;
        validTrack.setGenre(newGenre);
        assertEquals(newGenre, validTrack.getGenre());
    }

    @Test
    @DisplayName("Test setGenre with null")
    void testSetGenreNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setGenre(null));
        assertEquals("Жанр не може бути null", exception.getMessage());
    }

    @Test
    @DisplayName("Test setDuration with valid value")
    void testSetDurationValid() {
        Duration newDuration = Duration.ofMinutes(5);
        validTrack.setDuration(newDuration);
        assertEquals(newDuration, validTrack.getDuration());
    }

    @Test
    @DisplayName("Test setDuration with null")
    void testSetDurationNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setDuration(null));
        assertEquals("Тривалість має бути додатнім значенням", exception.getMessage());
    }

    @Test
    @DisplayName("Test setDuration with negative value")
    void testSetDurationNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setDuration(Duration.ofMinutes(-1)));
        assertEquals("Тривалість має бути додатнім значенням", exception.getMessage());
    }

    @Test
    @DisplayName("Test setDuration with zero value")
    void testSetDurationZero() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> validTrack.setDuration(Duration.ZERO));
        assertEquals("Тривалість має бути додатнім значенням", exception.getMessage());
    }

    @Test
    @DisplayName("Test toString method")
    void testToString() {
        String expected = String.format("%s - %s (%s, %d min)",
                validTitle, validArtist, validGenre, validDuration.toMinutes());
        assertEquals(expected, validTrack.toString());
    }

    @Test
    @DisplayName("Test getters for initialized track")
    void testGetters() {
        assertEquals(validTitle, validTrack.getTitle());
        assertEquals(validArtist, validTrack.getArtist());
        assertEquals(validGenre, validTrack.getGenre());
        assertEquals(validDuration, validTrack.getDuration());
    }
}