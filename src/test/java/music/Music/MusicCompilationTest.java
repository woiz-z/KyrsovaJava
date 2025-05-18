package music.Music;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MusicCompilationTest {
    private MusicCompilation compilation;
    private MusicTrack track1;
    private MusicTrack track2;
    private MusicTrack track3;

    @BeforeEach
    void setUp() {
        compilation = new MusicCompilation("Test Compilation");
        track1 = mock(MusicTrack.class);
        when(track1.getDuration()).thenReturn(Duration.ofMinutes(3));
        when(track1.getGenre()).thenReturn(MusicGenre.POP);
        when(track1.toString()).thenReturn("Pop Track");

        track2 = mock(MusicTrack.class);
        when(track2.getDuration()).thenReturn(Duration.ofMinutes(5));
        when(track2.getGenre()).thenReturn(MusicGenre.ROCK);
        when(track2.toString()).thenReturn("Rock Track");

        track3 = mock(MusicTrack.class);
        when(track3.getDuration()).thenReturn(Duration.ofMinutes(7));
        when(track3.getGenre()).thenReturn(MusicGenre.JAZZ);
        when(track3.toString()).thenReturn("Jazz Track");
    }

    @Test
    void constructor_ShouldCreateCompilationWithTitle() {
        MusicCompilation mc = new MusicCompilation("New Compilation");
        assertEquals("New Compilation", mc.getTitle());
        assertTrue(mc.getTracks().isEmpty());
    }

    @Test
    void constructor_ShouldThrowExceptionWhenTitleIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new MusicCompilation(null));
    }

    @Test
    void constructor_ShouldThrowExceptionWhenTitleIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> new MusicCompilation(""));
    }

    @Test
    void setId_ShouldSetIdWhenValid() {
        compilation.setId(1L);
        assertEquals(1L, compilation.getId());
    }

    @Test
    void setId_ShouldThrowExceptionWhenNegative() {
        assertThrows(IllegalArgumentException.class, () -> compilation.setId(-1L));
    }

    @Test
    void setTitle_ShouldChangeTitleWhenValid() {
        compilation.setTitle("New Title");
        assertEquals("New Title", compilation.getTitle());
    }

    @Test
    void setTitle_ShouldThrowExceptionWhenNull() {
        assertThrows(IllegalArgumentException.class, () -> compilation.setTitle(null));
    }

    @Test
    void setTitle_ShouldThrowExceptionWhenEmpty() {
        assertThrows(IllegalArgumentException.class, () -> compilation.setTitle(""));
    }

    @Test
    void getTracks_ShouldReturnCopyOfTracks() {
        compilation.addTrack(track1);
        List<MusicTrack> tracks = compilation.getTracks();
        assertEquals(1, tracks.size());
        tracks.clear();
        assertEquals(1, compilation.getTracks().size());
    }

    @Test
    void addTrack_ShouldAddTrackToCompilation() {
        compilation.addTrack(track1);
        assertEquals(1, compilation.getTracks().size());
        assertEquals(track1, compilation.getTracks().get(0));
    }

    @Test
    void addTrack_ShouldThrowExceptionWhenTrackIsNull() {
        assertThrows(IllegalArgumentException.class, () -> compilation.addTrack(null));
    }

    @Test
    void calculateTotalDuration_ShouldReturnZeroForEmptyCompilation() {
        assertEquals(Duration.ZERO, compilation.calculateTotalDuration());
    }

    @Test
    void calculateTotalDuration_ShouldReturnSumOfTrackDurations() {
        compilation.addTrack(track1);
        compilation.addTrack(track2);
        assertEquals(Duration.ofMinutes(8), compilation.calculateTotalDuration());
    }

    @Test
    void sortByGenre_ShouldSortTracksByGenre() {
        compilation.addTrack(track3);
        compilation.addTrack(track1);
        compilation.addTrack(track2);

        compilation.sortByGenre();

        List<MusicTrack> sortedTracks = compilation.getTracks();
        assertEquals(MusicGenre.JAZZ, sortedTracks.get(0).getGenre());
        assertEquals(MusicGenre.POP, sortedTracks.get(1).getGenre());
        assertEquals(MusicGenre.ROCK, sortedTracks.get(2).getGenre());
    }

    @Test
    void findTracksByDurationRange_ShouldReturnTracksInRange() {
        compilation.addTrack(track1);
        compilation.addTrack(track2);
        compilation.addTrack(track3);

        List<MusicTrack> result = compilation.findTracksByDurationRange(
                Duration.ofMinutes(4),
                Duration.ofMinutes(6)
        );

        assertEquals(1, result.size());
        assertEquals(track2, result.get(0));
    }

    @Test
    void findTracksByDurationRange_ShouldThrowExceptionWhenMinIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                compilation.findTracksByDurationRange(null, Duration.ofMinutes(5))
        );
    }

    @Test
    void findTracksByDurationRange_ShouldThrowExceptionWhenMaxIsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                compilation.findTracksByDurationRange(Duration.ofMinutes(2), null)
        );
    }

    @Test
    void findTracksByDurationRange_ShouldThrowExceptionWhenMinIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                compilation.findTracksByDurationRange(Duration.ofMinutes(-1), Duration.ofMinutes(5))
        );
    }

    @Test
    void findTracksByDurationRange_ShouldThrowExceptionWhenMaxIsNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                compilation.findTracksByDurationRange(Duration.ofMinutes(2), Duration.ofMinutes(-1))
        );
    }

    @Test
    void findTracksByDurationRange_ShouldThrowExceptionWhenMinGreaterThanMax() {
        assertThrows(IllegalArgumentException.class, () ->
                compilation.findTracksByDurationRange(Duration.ofMinutes(5), Duration.ofMinutes(2))
        );
    }

    @Test
    void toString_ShouldReturnFormattedString() {
        compilation.addTrack(track1);
        compilation.addTrack(track2);
        String expected = "Test Compilation (2 треків, 8 хв)";
        assertEquals(expected, compilation.toString());
    }

    @Test
    void getName_ShouldReturnNull() {
        assertNull(compilation.getName());
    }
}