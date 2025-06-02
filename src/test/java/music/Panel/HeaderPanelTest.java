package music.Panel;

import music.Models.MusicCompilation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;

class HeaderPanelTest {
    private MusicCompilation compilation;
    private HeaderPanel headerPanel;

    @BeforeEach
    void setUp() {
        compilation = new MusicCompilation("Test Compilation");
        headerPanel = new HeaderPanel(compilation);
    }

    @Test
    void testConstructorWithNullCompilation() {
        assertDoesNotThrow(() -> new HeaderPanel(null));
        HeaderPanel nullPanel = new HeaderPanel(null);
        assertNotNull(nullPanel.getPanel());
    }

    @Test
    void testGetPanel() {
        JPanel panel = headerPanel.getPanel();
        assertNotNull(panel);
        assertEquals(0, panel.getBorder().getBorderInsets(panel).top);
        assertEquals(20, panel.getBorder().getBorderInsets(panel).bottom);
        assertFalse(panel.isOpaque());
        assertEquals(BorderLayout.class, panel.getLayout().getClass());
    }

    @Test
    void testCreateTitleLabel() {
        JLabel titleLabel = headerPanel.createTitleLabel(compilation);
        assertNotNull(titleLabel);
        assertEquals("Test Compilation", titleLabel.getText());
        assertEquals(Font.BOLD, titleLabel.getFont().getStyle());
        assertEquals(24, titleLabel.getFont().getSize());


        JLabel nullLabel = headerPanel.createTitleLabel(null);
        assertEquals("Без назви", nullLabel.getText());
    }

    @Test
    void testCreateInfoLabel() {
        JLabel infoLabel = headerPanel.createInfoLabel(compilation);
        assertNotNull(infoLabel);
        assertEquals(Font.PLAIN, infoLabel.getFont().getStyle());
        assertEquals(14, infoLabel.getFont().getSize());
        assertEquals(new Color(100, 100, 100), infoLabel.getForeground());
        assertEquals(5, infoLabel.getBorder().getBorderInsets(infoLabel).top);
        assertEquals(5, infoLabel.getBorder().getBorderInsets(infoLabel).left);


        JLabel nullLabel = headerPanel.createInfoLabel(null);
        assertEquals("Немає даних компіляції", nullLabel.getText());
    }

    @Test
    void testUpdateInfo() {
        headerPanel.updateInfo(5, 30, 45);
        JLabel infoLabel = (JLabel) headerPanel.getPanel().getComponent(1);
        assertEquals("5 треків • 30 хв 45 сек", infoLabel.getText());


        headerPanel.updateInfo(0, 0, 0);
        assertEquals("0 треків • 0 хв 0 сек", infoLabel.getText());

        headerPanel.updateInfo(1, 59, 59);
        assertEquals("1 треків • 59 хв 59 сек", infoLabel.getText());
    }

    @Test
    void testUpdateFilterInfo() {
        headerPanel.updateFilterInfo(5, 30, 45, 1, 30, 5, 0);
        JLabel infoLabel = (JLabel) headerPanel.getPanel().getComponent(1);
        assertEquals("5 треків • 30 хв 45 сек • Фільтр: 1:30 - 5:00", infoLabel.getText());

        // Test edge cases
        headerPanel.updateFilterInfo(0, 0, 0, 0, 0, 0, 0);
        assertEquals("0 треків • 0 хв 0 сек • Фільтр: 0:00 - 0:00", infoLabel.getText());

        headerPanel.updateFilterInfo(1, 59, 59, 0, 1, 59, 58);
        assertEquals("1 треків • 59 хв 59 сек • Фільтр: 0:01 - 59:58", infoLabel.getText());
    }

    @Test
    void testTitleLabelRendering() {
        JLabel titleLabel = headerPanel.createTitleLabel(compilation);
        JFrame frame = new JFrame();
        frame.add(titleLabel);
        frame.pack();
        frame.setVisible(true);


        assertTrue(titleLabel.isVisible());
        assertEquals("Test Compilation", titleLabel.getText());


        assertDoesNotThrow(() -> titleLabel.paint(titleLabel.getGraphics()));

        frame.dispose();
    }

    @Test
    void testPanelComponents() {
        JPanel panel = headerPanel.getPanel();
        assertEquals(2, panel.getComponentCount());
        assertTrue(panel.getComponent(0) instanceof JLabel);
        assertTrue(panel.getComponent(1) instanceof JLabel);
    }

    @Test
    void testUpdateFilterInfoWithException() {
        JLabel mockInfoLabel = mock(JLabel.class);
        doThrow(new RuntimeException("Label error")).when(mockInfoLabel).setText(anyString());

        try {
            Field field = HeaderPanel.class.getDeclaredField("infoLabel");
            field.setAccessible(true);
            field.set(headerPanel, mockInfoLabel);
        } catch (Exception e) {
            fail("Failed to set mock infoLabel");
        }

        assertDoesNotThrow(() ->
                headerPanel.updateFilterInfo(1, 1, 1, 0, 0, 2, 2));
    }



}