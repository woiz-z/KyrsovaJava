package music.Renderer;

import music.Music.MusicTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.Duration;

import static music.Music.MusicGenre.*;
import static org.junit.jupiter.api.Assertions.*;

public class ModernTrackListRendererTest {

    private ModernTrackListRenderer renderer;
    private JList<Object> list;
    private MusicTrack track;

    @BeforeEach
    void setUp() {
        renderer = new ModernTrackListRenderer();
        list = new JList<>();
        track = new MusicTrack("Test Title", "Test Artist", ROCK, Duration.ofSeconds(125));
    }

    @Test
    void testGetListCellRendererComponent_NonMusicTrack() {

        Component component = renderer.getListCellRendererComponent(list, "Not a track", 0, false, false);
        assertEquals(renderer, component);
        assertTrue(renderer.getBorder() instanceof EmptyBorder);
        EmptyBorder border = (EmptyBorder) renderer.getBorder();
        assertEquals(5, border.getBorderInsets().top);
        assertEquals(10, border.getBorderInsets().left);
        assertEquals(5, border.getBorderInsets().bottom);
        assertEquals(10, border.getBorderInsets().right);
    }

    @Test
    void testGetListCellRendererComponent_MusicTrack_Selected() {
        Component component = renderer.getListCellRendererComponent(list, track, 0, true, true);
        assertEquals(renderer, component);


        assertEquals(new Color(220, 240, 255), renderer.getBackground());


        Border border = renderer.getBorder();
        assertTrue(border instanceof CompoundBorder);
        CompoundBorder compoundBorder = (CompoundBorder) border;
        assertTrue(compoundBorder.getOutsideBorder() instanceof MatteBorder);
        assertTrue(compoundBorder.getInsideBorder() instanceof EmptyBorder);
        MatteBorder matteBorder = (MatteBorder) compoundBorder.getOutsideBorder();
        assertEquals(new Color(180, 220, 255), matteBorder.getMatteColor());
        assertEquals(1, matteBorder.getBorderInsets().top);


        String expectedText = "<html><div style='padding:5px;'>" +
                "<b style='font-size:14px; color:#333;'>Test Title</b><br>" +
                "<span style='color:#666; font-size:12px;'>Test Artist • <span style='color:#5e8c31;'>Rock</span> • 2:05</span>" +
                "</div></html>";
        assertEquals(expectedText, renderer.getText());
    }

    @Test
    void testGetListCellRendererComponent_MusicTrack_Unselected_EvenIndex() {
        Component component = renderer.getListCellRendererComponent(list, track, 0, false, false);
        assertEquals(renderer, component);


        assertEquals(new Color(255, 255, 255, 200), renderer.getBackground());


        assertTrue(renderer.getBorder() instanceof EmptyBorder);
        EmptyBorder border = (EmptyBorder) renderer.getBorder();
        assertEquals(5, border.getBorderInsets().top);
        assertEquals(10, border.getBorderInsets().left);
        assertEquals(5, border.getBorderInsets().bottom);
        assertEquals(10, border.getBorderInsets().right);


        String expectedText = "<html><div style='padding:5px;'>" +
                "<b style='font-size:14px; color:#333;'>Test Title</b><br>" +
                "<span style='color:#666; font-size:12px;'>Test Artist • <span style='color:#5e8c31;'>Rock</span> • 2:05</span>" +
                "</div></html>";
        assertEquals(expectedText, renderer.getText());
    }

    @Test
    void testGetListCellRendererComponent_MusicTrack_Unselected_OddIndex() {
        Component component = renderer.getListCellRendererComponent(list, track, 1, false, false);
        assertEquals(renderer, component);


        assertEquals(new Color(245, 248, 250, 200), renderer.getBackground());

        assertTrue(renderer.getBorder() instanceof EmptyBorder);
        EmptyBorder border = (EmptyBorder) renderer.getBorder();
        assertEquals(5, border.getBorderInsets().top);
        assertEquals(10, border.getBorderInsets().left);
        assertEquals(5, border.getBorderInsets().bottom);
        assertEquals(10, border.getBorderInsets().right);


        String expectedText = "<html><div style='padding:5px;'>" +
                "<b style='font-size:14px; color:#333;'>Test Title</b><br>" +
                "<span style='color:#666; font-size:12px;'>Test Artist • <span style='color:#5e8c31;'>Rock</span> • 2:05</span>" +
                "</div></html>";
        assertEquals(expectedText, renderer.getText());
    }

    @Test
    void testFormatTrackText_ZeroSeconds() {
        MusicTrack shortTrack = new MusicTrack("Short Track", "Artist", POP, Duration.ofMinutes(3));
        Component component = renderer.getListCellRendererComponent(list, shortTrack, 0, false, false);

        String expectedText = "<html><div style='padding:5px;'>" +
                "<b style='font-size:14px; color:#333;'>Short Track</b><br>" +
                "<span style='color:#666; font-size:12px;'>Artist • <span style='color:#5e8c31;'>Pop</span> • 3:00</span>" +
                "</div></html>";
        assertEquals(expectedText, renderer.getText());
    }

    @Test
    void testFormatTrackText_LongDuration() {
        MusicTrack longTrack = new MusicTrack("Long Track", "Artist", CLASSICAL, Duration.ofSeconds(605));
        Component component = renderer.getListCellRendererComponent(list, longTrack, 0, false, false);


        String expectedText = "<html><div style='padding:5px;'>" +
                "<b style='font-size:14px; color:#333;'>Long Track</b><br>" +
                "<span style='color:#666; font-size:12px;'>Artist • <span style='color:#5e8c31;'>Classical</span> • 10:05</span>" +
                "</div></html>";
        assertEquals(expectedText, renderer.getText());
    }
}