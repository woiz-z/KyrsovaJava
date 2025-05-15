package music.Dialog;

import music.Music.MusicCompilation;
import music.Panel.TrackListPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;

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

        // Test gradient painting (indirectly)
        Graphics2D mockGraphics = mock(Graphics2D.class);
        mainPanel.printComponents(mockGraphics);
        verify(mockGraphics, atLeastOnce()).fillRect(anyInt(), anyInt(), anyInt(), anyInt());
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



}