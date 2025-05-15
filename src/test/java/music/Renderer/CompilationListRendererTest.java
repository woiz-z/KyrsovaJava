package music.Renderer;

import music.Music.MusicCompilation;
import music.Music.MusicGenre;
import music.Music.MusicTrack;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class CompilationListRendererTest {

    @Test
    void testGetListCellRendererComponentWithValidCompilation() {

        CompilationListRenderer renderer = new CompilationListRenderer();
        JList<MusicCompilation> list = new JList<>();
        MusicCompilation compilation = new MusicCompilation("Test Compilation");
        compilation.addTrack(new MusicTrack("Track 1", "Artist 1", MusicGenre.POP, Duration.ofMinutes(3)));
        compilation.addTrack(new MusicTrack("Track 2", "Artist 2", MusicGenre.ROCK, Duration.ofMinutes(4)));


        Component result = renderer.getListCellRendererComponent(
                list, compilation, 0, false, false);


        assertTrue(result instanceof JLabel);
        JLabel label = (JLabel) result;
        assertTrue(label.getText().contains("Test Compilation"));
        assertTrue(label.getText().contains("2 треків"));
        assertTrue(label.getText().contains("7 хв"));
        assertEquals(new Color(255, 255, 255), label.getBackground());
    }

    @Test
    void testGetListCellRendererComponentWithSelectedItem() {

        CompilationListRenderer renderer = new CompilationListRenderer();
        JList<MusicCompilation> list = new JList<>();
        MusicCompilation compilation = new MusicCompilation("Selected Compilation");


        Component result = renderer.getListCellRendererComponent(
                list, compilation, 1, true, true);

        JLabel label = (JLabel) result;
        assertEquals(new Color(220, 240, 255), label.getBackground());
    }

    @Test
    void testGetListCellRendererComponentWithEvenIndex() {

        CompilationListRenderer renderer = new CompilationListRenderer();
        JList<MusicCompilation> list = new JList<>();
        MusicCompilation compilation = new MusicCompilation("Even Index Compilation");


        Component result = renderer.getListCellRendererComponent(
                list, compilation, 2, false, false);


        JLabel label = (JLabel) result;
        assertEquals(new Color(255, 255, 255), label.getBackground());
    }

    @Test
    void testGetListCellRendererComponentWithOddIndex() {

        CompilationListRenderer renderer = new CompilationListRenderer();
        JList<MusicCompilation> list = new JList<>();
        MusicCompilation compilation = new MusicCompilation("Odd Index Compilation");


        Component result = renderer.getListCellRendererComponent(
                list, compilation, 3, false, false);


        JLabel label = (JLabel) result;
        assertEquals(new Color(245, 248, 250), label.getBackground());
    }

    @Test
    void testConfigureText() {

        CompilationListRenderer renderer = new CompilationListRenderer();
        MusicCompilation compilation = new MusicCompilation("Test Compilation");
        compilation.addTrack(new MusicTrack("Track 1", "Artist", MusicGenre.CLASSICAL, Duration.ofMinutes(2)));


        renderer.configureText(compilation);
        String text = ((JLabel) renderer).getText();


        assertTrue(text.contains("Test Compilation"));
        assertTrue(text.contains("1 треків"));
        assertTrue(text.contains("2 хв"));
        assertTrue(text.contains("font-size:14px"));
        assertTrue(text.contains("font-size:12px"));
    }

    @Test
    void testConfigureBordersWhenSelected() {

        CompilationListRenderer renderer = new CompilationListRenderer();


        renderer.configureBorders(true);
        Border border = ((JLabel) renderer).getBorder();


        assertNotNull(border);
        assertTrue(border instanceof CompoundBorder);
    }

    @Test
    void testConfigureBordersWhenNotSelected() {

        CompilationListRenderer renderer = new CompilationListRenderer();


        renderer.configureBorders(false);
        Border border = ((JLabel) renderer).getBorder();


        assertNotNull(border);
        assertTrue(border instanceof EmptyBorder);
    }

    @Test
    void testConfigureBackgroundForSelected() {

        CompilationListRenderer renderer = new CompilationListRenderer();


        renderer.configureBackground(0, true);
        Color bgColor = ((JLabel) renderer).getBackground();


        assertEquals(new Color(220, 240, 255), bgColor);
    }

    @Test
    void testConfigureBackgroundForEvenIndex() {

        CompilationListRenderer renderer = new CompilationListRenderer();


        renderer.configureBackground(2, false);
        Color bgColor = ((JLabel) renderer).getBackground();


        assertEquals(new Color(255, 255, 255), bgColor);
    }

    @Test
    void testConfigureBackgroundForOddIndex() {

        CompilationListRenderer renderer = new CompilationListRenderer();


        renderer.configureBackground(1, false);
        Color bgColor = ((JLabel) renderer).getBackground();


        assertEquals(new Color(245, 248, 250), bgColor);
    }

    @Test
    void testGetListCellRendererComponentWithNonCompilationObject() {

        CompilationListRenderer renderer = new CompilationListRenderer();
        JList<String> list = new JList<>();


        Component result = renderer.getListCellRendererComponent(
                list, "Not a compilation", 0, false, false);

        assertTrue(result instanceof JLabel);
        assertEquals("Not a compilation", ((JLabel) result).getText());
    }
}