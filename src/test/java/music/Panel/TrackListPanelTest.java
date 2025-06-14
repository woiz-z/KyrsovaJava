package music.Panel;

import music.Dialog.CompilationDetailsDialog;
import music.Models.MusicCompilation;
import music.Service.MusicCompilationService;
import music.Models.MusicTrack;
import music.Models.MusicGenre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrackListPanelTest {

    @Mock
    private CompilationDetailsDialog parentDialog;
    @Mock
    private HeaderPanel headerPanel;
    private static final MusicCompilationService compilationService=new MusicCompilationService();
    private MusicCompilation compilation;
    private TrackListPanel trackListPanel;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        compilation = new MusicCompilation("Test Compilation");
        compilation.addTrack(new MusicTrack("Track 1", "Artist 1", MusicGenre.ROCK, Duration.ofMinutes(3)));
        compilation.addTrack(new MusicTrack("Track 2", "Artist 2", MusicGenre.POP, Duration.ofMinutes(5)));

        when(parentDialog.getParent()).thenReturn(new JFrame());
        trackListPanel = new TrackListPanel(parentDialog, compilation);
    }

    @Test
    void testConstructor() {
        assertNotNull(trackListPanel.getPanel());
        assertNotNull(trackListPanel.getTrackList());
        assertNotNull(trackListPanel.getTrackListModel());
        assertEquals(2, trackListPanel.getTrackListModel().size());
    }

    @Test
    void testGetPanel() {
        JPanel panel = trackListPanel.getPanel();
        assertNotNull(panel);
        assertEquals(BorderLayout.class, panel.getLayout().getClass());
    }

    @Test
    void testGetTrackList() {
        JList<MusicTrack> trackList = trackListPanel.getTrackList();
        assertNotNull(trackList);
        assertEquals(2, trackList.getModel().getSize());
    }

    @Test
    void testGetTrackListModel() {
        DefaultListModel<MusicTrack> model = trackListPanel.getTrackListModel();
        assertNotNull(model);
        assertEquals(2, model.size());
    }

    @Test
    void testGetParent() {
        assertEquals(parentDialog, trackListPanel.getParent());
    }

    @Test
    void testShowTrackDetails() {
        MusicTrack track = new MusicTrack("Test Track", "Test Artist", MusicGenre.JAZZ, Duration.ofMinutes(4));
        trackListPanel.showTrackDetails(track);

    }

    @Test
    void testShowTrackDetailsWithNullTrack() {
        trackListPanel.showTrackDetails(null);

    }

    @Test
    void testFilterTracksByDuration() {
        Duration min = Duration.ofMinutes(2);
        Duration max = Duration.ofMinutes(4);

        try (MockedStatic<JOptionPane> mocked = Mockito.mockStatic(JOptionPane.class)) {

            mocked.when(() -> JOptionPane.showMessageDialog(
                    any(Component.class),
                    anyString(),
                    anyString(),
                    anyInt()
            )).thenAnswer(invocation -> null);

            trackListPanel.filterTracksByDuration(min, max, headerPanel);

            assertEquals(1, trackListPanel.getTrackListModel().size());
        }
    }

    @Test
    void testFilterTracksByDurationNoResults() {
        Duration min = Duration.ofMinutes(10);
        Duration max = Duration.ofMinutes(15);

        try (MockedStatic<JOptionPane> mocked = Mockito.mockStatic(JOptionPane.class)) {

            mocked.when(() -> JOptionPane.showMessageDialog(
                    any(Component.class),
                    anyString(),
                    anyString(),
                    anyInt()
            )).thenAnswer(invocation -> null);


            doNothing().when(headerPanel).updateFilterInfo(
                    anyInt(), anyLong(), anyLong(), anyLong(), anyLong(), anyLong(), anyLong());

            trackListPanel.filterTracksByDuration(min, max, headerPanel);

            assertEquals(0, trackListPanel.getTrackListModel().size());
        }
    }

    @Test
    void testFilterTracksByDurationWithNullAllTracks() {
        trackListPanel.allTracks = null;
        Duration min = Duration.ofMinutes(2);
        Duration max = Duration.ofMinutes(4);

        try (MockedStatic<JOptionPane> mocked = Mockito.mockStatic(JOptionPane.class)) {

            mocked.when(() -> JOptionPane.showMessageDialog(
                    any(Component.class),
                    anyString(),
                    anyString(),
                    anyInt()
            )).thenAnswer(invocation -> null);


            doNothing().when(headerPanel).updateFilterInfo(
                    anyInt(), anyLong(), anyLong(), anyLong(), anyLong(), anyLong(), anyLong());

            trackListPanel.filterTracksByDuration(min, max, headerPanel);

            assertEquals(1, trackListPanel.getTrackListModel().size());
        }
    }



    @Test
    void testResetFilterWithEmptyAllTracks() {
        trackListPanel.allTracks = new ArrayList<>();
        trackListPanel.resetFilter(headerPanel);

    }

    @Test
    void testLoadTracksFromCompilation() {
        trackListPanel.loadTracksFromCompilation();
        assertEquals(2, trackListPanel.getTrackListModel().size());
    }

    @Test
    void testCalculateFilteredTotalDuration() {
        Duration duration = trackListPanel.calculateFilteredTotalDuration();
        assertEquals(Duration.ofMinutes(8).getSeconds(), duration.getSeconds());
    }

    @Test
    void testCalculateFilteredTotalDurationEmptyList() {
        trackListPanel.getTrackListModel().clear();
        Duration duration = trackListPanel.calculateFilteredTotalDuration();
        assertEquals(Duration.ZERO, duration);
    }

    @Test
    void testMouseDoubleClickOnTrack() {
        JList<MusicTrack> trackList = trackListPanel.getTrackList();


        trackList.setSelectedIndex(0);
        for (MouseListener listener : trackList.getMouseListeners()) {
            listener.mouseClicked(new MouseEvent(trackList, MouseEvent.MOUSE_CLICKED,
                    System.currentTimeMillis(), 0, 0, 0, 2, false));
        }


    }

    @Test
    void testInitializePanel() {

        JPanel panel = trackListPanel.getPanel();
        Component[] components = panel.getComponents();
        assertEquals(2, components.length);
    }

    @Test
    void testInitializeTrackList() {

        JList<MusicTrack> trackList = trackListPanel.getTrackList();
        assertFalse(trackList.isOpaque());
        assertEquals(ListSelectionModel.SINGLE_SELECTION, trackList.getSelectionMode());
    }

    @Test
    void testResetFilterWithNullAllTracks() {

        trackListPanel.allTracks = null;


        trackListPanel.resetFilter(headerPanel);


        assertEquals(2, trackListPanel.getTrackListModel().size());
        verify(headerPanel, never()).updateInfo(anyInt(), anyLong(), anyLong());
    }

    @Test
    void testResetFilterWithException() {

        CompilationDetailsDialog parentDialog = mock(CompilationDetailsDialog.class);
        HeaderPanel headerPanel = mock(HeaderPanel.class);
        MusicCompilation compilation = mock(MusicCompilation.class);
        TrackListPanel trackListPanel = new TrackListPanel(parentDialog, compilation);

        trackListPanel.allTracks = List.of(
                new MusicTrack("Track 1", "Artist 1", MusicGenre.ROCK, Duration.ofMinutes(3))
        );

        when(compilationService.calculateTotalDuration(compilation.getTracks())).thenThrow(new RuntimeException("Test exception"));


        try (MockedStatic<JOptionPane> mocked = Mockito.mockStatic(JOptionPane.class)) {

            trackListPanel.resetFilter(headerPanel);


            mocked.verify(() -> JOptionPane.showMessageDialog(
                    eq(parentDialog),
                    eq("Не вдалося скинути фільтр"),
                    eq("Помилка"),
                    eq(JOptionPane.ERROR_MESSAGE)
            ));
        }
    }

    @Test
    void testShowTrackDetailsWithException() {
        try (MockedStatic<JOptionPane> mocked = Mockito.mockStatic(JOptionPane.class)) {
            MusicTrack track = new MusicTrack("Test Track", "Test Artist", MusicGenre.JAZZ, Duration.ofMinutes(4));

            // Симулюємо виняток при створенні діалогу
            when(parentDialog.getParent()).thenThrow(new RuntimeException("Test exception"));

            trackListPanel.showTrackDetails(track);

            // Перевіряємо, що було показано повідомлення про помилку
            mocked.verify(() -> JOptionPane.showMessageDialog(
                    eq(parentDialog),
                    eq("Не вдалося відкрити деталі треку"),
                    eq("Помилка"),
                    eq(JOptionPane.ERROR_MESSAGE)
            ));
        }
    }



    @Test
    void testResetFilterWithEmptyTracks() {
        trackListPanel.allTracks = new ArrayList<>();

        try (MockedStatic<JOptionPane> mocked = Mockito.mockStatic(JOptionPane.class)) {
            trackListPanel.resetFilter(headerPanel);

            // Перевіряємо, що було залоговано попередження
            verify(headerPanel, never()).updateInfo(anyInt(), anyLong(), anyLong());
        }
    }



    @Test
    void testMouseClickWithNullSelectedValue() {
        JList<MusicTrack> trackList = trackListPanel.getTrackList();

        // Симулюємо клік, коли нічого не вибрано
        trackList.clearSelection();
        for (MouseListener listener : trackList.getMouseListeners()) {
            listener.mouseClicked(new MouseEvent(trackList, MouseEvent.MOUSE_CLICKED,
                    System.currentTimeMillis(), 0, 0, 0, 2, false));
        }

        // Перевіряємо, що не було винятків
        assertTrue(true);
    }

    @Test
    void testConstructorWithInitializationException() {
        // Створюємо mock компіляції, яка кидатиме виняток при отриманні треків
        MusicCompilation mockCompilation = mock(MusicCompilation.class);
        when(mockCompilation.getTracks()).thenThrow(new RuntimeException("Test exception"));

        assertThrows(RuntimeException.class, () -> {
            new TrackListPanel(parentDialog, mockCompilation);
        });
    }



}