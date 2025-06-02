package music.Factory;

import music.Manager.DiscManager;
import music.Models.MusicCompilation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DialogFactoryTest {

    private JFrame parentFrame;
    private DiscManager discManager;
    private DefaultListModel<MusicCompilation> listModel;
    private JLabel statusBar;
    private MusicCompilation testCompilation;

    @BeforeEach
    void setUp() {
        parentFrame = new JFrame();
        discManager = mock(DiscManager.class);
        listModel = new DefaultListModel<>();
        statusBar = new JLabel();
        testCompilation = new MusicCompilation("Test Compilation");
    }

    @Test
    void testCreateStyledPanel() {
        JPanel panel = DialogFactory.createStyledPanel("Test Title");
        assertNotNull(panel);
        assertEquals(1, panel.getComponentCount());
        assertTrue(panel.getComponent(0) instanceof JLabel);
    }

    @Test
    void testCreateGradientPanel() {
        JPanel panel = DialogFactory.createGradientPanel();
        assertNotNull(panel);
        assertTrue(panel.getLayout() instanceof BorderLayout);
    }

    @Test
    void testCreateTextField() {
        JTextField textField = DialogFactory.createTextField();
        assertNotNull(textField);
        assertNotNull(textField.getFont());
        assertNotNull(textField.getBorder());
    }

    @Test
    void testCreateModernButton() {
        JButton button = DialogFactory.createModernButton("Test", Color.BLUE);
        assertNotNull(button);
        assertEquals("Test", button.getText());
        assertEquals(Color.WHITE, button.getForeground());
        assertNotNull(button.getFont());
    }

    @Test
    void testCreateButtonPanel() {
        JPanel panel = DialogFactory.createButtonPanel();
        assertNotNull(panel);
        assertFalse(panel.isOpaque());
        assertTrue(panel.getLayout() instanceof FlowLayout);
    }

    @Test
    void testCreateDeleteMessage() {
        String message = DialogFactory.createDeleteMessage(testCompilation);
        assertNotNull(message);
        assertTrue(message.contains(testCompilation.getTitle()));
        assertTrue(message.contains(String.valueOf(testCompilation.getTracks().size())));
    }

    @Test
    void testShowAddCompilationDialog() {
        DialogFactory.showAddCompilationDialog(parentFrame, discManager, listModel, statusBar);

        assertTrue(parentFrame.getOwnedWindows().length > 0);
    }

    @Test
    void testShowRenameCompilationDialog() {
        DialogFactory.showRenameCompilationDialog(parentFrame, discManager, listModel, statusBar, testCompilation);

        assertTrue(parentFrame.getOwnedWindows().length > 0);
    }

    @Test
    void testShowDeleteCompilationDialog() {
        DialogFactory.showDeleteCompilationDialog(parentFrame, discManager, listModel, statusBar, testCompilation);

        assertTrue(parentFrame.getOwnedWindows().length > 0);
    }

    @Test
    void testHandleAddActionWithValidInput() {
        JTextField textField = new JTextField("New Compilation");
        JPanel panel = new JPanel();

        DialogFactory.handleAddAction(textField, discManager, listModel, statusBar, panel);

        verify(discManager).addCompilation(any(MusicCompilation.class));
        assertTrue(panel.getTopLevelAncestor() == null || !panel.getTopLevelAncestor().isVisible());
    }

    @Test
    void testHandleAddActionWithEmptyInput() {
        JTextField textField = new JTextField("");
        JPanel panel = new JPanel();

        DialogFactory.handleAddAction(textField, discManager, listModel, statusBar, panel);

        verify(discManager, never()).addCompilation(any());
    }

    @Test
    void testHandleRenameActionWithValidInput() {
        JTextField textField = new JTextField("New Name");
        JPanel panel = new JPanel();

        DialogFactory.handleRenameAction(textField, testCompilation, discManager, listModel, statusBar, panel);

        verify(discManager).updateCompilationTitle(testCompilation, "New Name");
        assertTrue(panel.getTopLevelAncestor() == null || !panel.getTopLevelAncestor().isVisible());
    }

    @Test
    void testHandleRenameActionWithEmptyInput() {
        JTextField textField = new JTextField("");
        JPanel panel = new JPanel();

        DialogFactory.handleRenameAction(textField, testCompilation, discManager, listModel, statusBar, panel);

        verify(discManager, never()).updateCompilationTitle(any(), any());
    }

    @Test
    void testHandleDeleteAction() {
        JPanel panel = new JPanel();

        DialogFactory.handleDeleteAction(testCompilation, discManager, listModel, statusBar, panel);

        verify(discManager).removeCompilation(testCompilation);
        assertTrue(panel.getTopLevelAncestor() == null || !panel.getTopLevelAncestor().isVisible());
    }



    @Test
    void testCloseDialog() {
        JDialog dialog = new JDialog();
        JPanel panel = new JPanel();
        dialog.add(panel);
        dialog.setVisible(true);

        DialogFactory.closeDialog(panel);

        assertFalse(dialog.isVisible());
    }

    @Test
    void testShowDialog() {
        JPanel panel = new JPanel();
        DialogFactory.showDialog(parentFrame, panel, new Dimension(100, 100));

        assertTrue(parentFrame.getOwnedWindows().length > 0);
    }
}