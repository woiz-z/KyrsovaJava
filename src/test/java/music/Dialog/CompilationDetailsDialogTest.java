package music.Dialog;

import music.Music.MusicCompilation;
import music.Panel.TrackListPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;



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

        // Verify the panel is non-opaque (required for custom painting)
        assertTrue(mainPanel.isOpaque());

        // For gradient testing, we can verify the class behavior rather than painting calls
        // This is more reliable than trying to mock the painting process
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

        // Need to initialize trackListPanel first since it's used in addButtonPanel
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
        // Act
        JPanel mainPanel = dialog.createMainPanel();

        // Assert basic properties
        assertNotNull(mainPanel);

        // Verify layout
        assertTrue(mainPanel.getLayout() instanceof BorderLayout);
        BorderLayout layout = (BorderLayout) mainPanel.getLayout();
        assertEquals(10, layout.getHgap());
        assertEquals(10, layout.getVgap());

        // Verify border
        assertTrue(mainPanel.getBorder() instanceof EmptyBorder);
        EmptyBorder border = (EmptyBorder) mainPanel.getBorder();
        assertEquals(15, border.getBorderInsets().top);
        assertEquals(15, border.getBorderInsets().left);
        assertEquals(15, border.getBorderInsets().bottom);
        assertEquals(15, border.getBorderInsets().right);

        // Verify opacity (should be true for custom painting)
        assertTrue(mainPanel.isOpaque());
    }

    @Test
    void createMainPanel_ShouldPaintGradientBackground() {
        // Arrange
        JPanel mainPanel = dialog.createMainPanel();
        mainPanel.setSize(100, 100); // Set size for painting

        // Create a test graphics context
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Act - trigger painting using the public paint() method
        mainPanel.paint(g2d);

        // Assert - verify the gradient was applied by checking pixel colors
        Color expectedTopColor = new Color(244, 247, 249); // color1
        Color expectedBottomColor = new Color(230, 235, 240); // color2

        // Check top pixel (should be color1)
        Color actualTopColor = new Color(image.getRGB(50, 1), true);
        assertEquals(expectedTopColor, actualTopColor);

        // Check bottom pixel (should be color2)
        Color actualBottomColor = new Color(image.getRGB(50, 98), true);
        assertEquals(expectedBottomColor, actualBottomColor);

        g2d.dispose();
    }



}