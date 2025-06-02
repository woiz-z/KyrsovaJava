package music.Dialog;

import music.Models.MusicCompilation;
import music.Panel.TrackListPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompilationDetailsDialogTest {

    private JFrame parentFrame;
    private MusicCompilation compilation;
    private CompilationDetailsDialog dialog;

    @BeforeEach
    void setUp() {
        parentFrame = new JFrame();
        compilation = new MusicCompilation("Test Compilation");
        dialog = new CompilationDetailsDialog(parentFrame, compilation);
    }

    @Test
    void constructor_ShouldInitializeCorrectly() {
        assertNotNull(dialog);
        assertEquals("Деталі збірки: Test Compilation", dialog.getTitle());
        assertTrue(dialog.isModal());
        assertEquals(parentFrame, dialog.getParent());
    }

    @Test
    void initializeUI_ShouldSetCorrectProperties() {
        assertTrue(dialog.isResizable());
        assertEquals(1400, dialog.getWidth());
        assertEquals(750, dialog.getHeight());
        assertNotNull(dialog.getContentPane());
    }

    @Test
    void createMainPanel_ShouldReturnPanelWithGradientBackground() {
        JPanel mainPanel = dialog.createMainPanel();
        assertNotNull(mainPanel);
        assertTrue(mainPanel.getLayout() instanceof BorderLayout);
        assertNotNull(mainPanel.getBorder());


        assertTrue(mainPanel.isOpaque());


    }

    @Test
    void addHeaderPanel_ShouldAddComponentToNorth() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        dialog.addHeaderPanel(mainPanel);

        Component northComponent = ((BorderLayout)mainPanel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        assertNotNull(northComponent);
    }

    @Test
    void addTrackListPanel_ShouldAddComponentToCenter() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        dialog.addTrackListPanel(mainPanel);

        Component centerComponent = ((BorderLayout)mainPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        assertNotNull(centerComponent);
        assertNotNull(dialog.trackListPanel);
    }

    @Test
    void addButtonPanel_ShouldAddComponentToSouth() {
        JPanel mainPanel = new JPanel(new BorderLayout());


        dialog.trackListPanel = mock(TrackListPanel.class);
        when(dialog.trackListPanel.getPanel()).thenReturn(new JPanel());

        dialog.addButtonPanel(mainPanel);

        Component southComponent = ((BorderLayout)mainPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        assertNotNull(southComponent);
    }

    @Test
    void compilationField_ShouldBeAccessible() {
        assertSame(compilation, dialog.compilation);
    }

    @Test
    void createMainPanel_ShouldCreatePanelWithCorrectProperties() {

        JPanel mainPanel = dialog.createMainPanel();


        assertNotNull(mainPanel);


        assertTrue(mainPanel.getLayout() instanceof BorderLayout);
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        assertEquals(10, layout.getHgap());
        assertEquals(10, layout.getVgap());


        assertTrue(mainPanel.getBorder() instanceof EmptyBorder);
        EmptyBorder border = (EmptyBorder) mainPanel.getBorder();
        assertEquals(15, border.getBorderInsets().top);
        assertEquals(15, border.getBorderInsets().left);
        assertEquals(15, border.getBorderInsets().bottom);
        assertEquals(15, border.getBorderInsets().right);


        assertTrue(mainPanel.isOpaque());
    }

    @Test
    void createMainPanel_ShouldPaintGradientBackground() {

        JPanel mainPanel = dialog.createMainPanel();
        mainPanel.setSize(100, 100);


        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();


        mainPanel.paint(g2d);


        Color expectedTopColor = new Color(244, 247, 249);
        Color expectedBottomColor = new Color(230, 235, 240);


        Color actualTopColor = new Color(image.getRGB(50, 1), true);
        assertEquals(expectedTopColor, actualTopColor);


        Color actualBottomColor = new Color(image.getRGB(50, 98), true);
        assertEquals(expectedBottomColor, actualBottomColor);

        g2d.dispose();
    }



}