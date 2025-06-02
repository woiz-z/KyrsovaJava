package music.Panel;

import music.Models.MusicTrack;
import music.Models.MusicGenre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TrackSearchPanelTest {
    private JList<MusicTrack> trackList;
    private DefaultListModel<MusicTrack> listModel;
    private TrackSearchPanel trackSearchPanel;

    @BeforeEach
    void setUp() {
        trackList = new JList<>();
        listModel = new DefaultListModel<>();
        listModel.addElement(new MusicTrack("Title1", "Artist1", MusicGenre.ROCK, Duration.ofMinutes(3)));
        listModel.addElement(new MusicTrack("Title2", "Artist2", MusicGenre.POP, Duration.ofMinutes(4)));
        trackSearchPanel = new TrackSearchPanel(trackList, listModel);
    }

    @Test
    void testConstructor() {
        assertNotNull(trackSearchPanel.searchField);
        assertEquals(2, trackSearchPanel.allTracks.size());
    }

    @Test
    void testInitializeTracks() {
        trackSearchPanel.initializeTracks();
        assertEquals(2, trackSearchPanel.allTracks.size());
    }

    @Test
    void testCreateSearchField() {
        JTextField field = trackSearchPanel.createSearchField();
        assertNotNull(field);
        assertEquals("Segoe UI", field.getFont().getName());
    }

    @Test
    void testCreateSearchIcon() {
        JLabel icon = trackSearchPanel.createSearchIcon();
        assertNotNull(icon);
        assertEquals(30, icon.getPreferredSize().height);
    }

    @Test
    void testCreateClearButtonPanel() {
        JPanel panel = trackSearchPanel.createClearButtonPanel();
        assertNotNull(panel);
        JButton clearButton = (JButton) panel.getComponent(0);
        clearButton.doClick(); // Симулюємо клік
        assertEquals("", trackSearchPanel.searchField.getText());
    }

    @Test
    void testFilterTracksEmptyQuery() {
        trackSearchPanel.searchField.setText("");
        trackSearchPanel.filterTracks();
        assertEquals(listModel.size(), trackList.getModel().getSize());
    }

    @Test
    void testFilterTracksByTitle() {
        trackSearchPanel.searchField.setText("Title1");
        trackSearchPanel.filterTracks();
        assertEquals(1, trackList.getModel().getSize());
    }

    @Test
    void testFilterTracksByArtist() {
        trackSearchPanel.searchField.setText("Artist2");
        trackSearchPanel.filterTracks();
        assertEquals(1, trackList.getModel().getSize());
    }

    @Test
    void testFilterTracksByGenre() {
        trackSearchPanel.searchField.setText("Pop");
        trackSearchPanel.filterTracks();
        assertEquals(1, trackList.getModel().getSize());
    }

    @Test
    void testMatchesSearch() {
        MusicTrack track = new MusicTrack("Test", "Artist", MusicGenre.JAZZ, Duration.ofMinutes(2));
        assertTrue(trackSearchPanel.matchesSearch(track, "test"));
        assertTrue(trackSearchPanel.matchesSearch(track, "artist"));
        assertTrue(trackSearchPanel.matchesSearch(track, "jazz"));
        assertFalse(trackSearchPanel.matchesSearch(track, "unknown"));
    }

    @Test
    void testUpdateTrackList() {
        List<MusicTrack> newTracks = new ArrayList<>();
        newTracks.add(new MusicTrack("NewTitle", "NewArtist", MusicGenre.CLASSICAL, Duration.ofMinutes(5)));
        trackSearchPanel.updateTrackList(newTracks);
        assertEquals(1, trackSearchPanel.allTracks.size());
    }
}