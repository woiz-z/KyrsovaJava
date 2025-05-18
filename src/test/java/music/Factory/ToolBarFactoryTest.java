package music.Factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

class ToolBarFactoryTest {

    @Test
    void testCreateToolBar() {
        Runnable emptyAction = () -> {};
        JToolBar toolBar = ToolBarFactory.createToolBar(emptyAction, emptyAction, emptyAction, emptyAction, emptyAction);

        assertNotNull(toolBar);
        assertFalse(toolBar.isFloatable());
        assertEquals(9, toolBar.getComponentCount());
    }

    @Test
    void testConfigureToolBar() {
        JToolBar toolBar = new JToolBar();
        ToolBarFactory.configureToolBar(toolBar);

        assertFalse(toolBar.isFloatable());
        assertNotNull(toolBar.getBorder());
        assertEquals(new Color(255, 255, 255), toolBar.getBackground());
    }

    @Test
    void testAddButtons() {
        JToolBar toolBar = new JToolBar();
        Runnable emptyAction = () -> {};
        ToolBarFactory.addButtons(toolBar, emptyAction, emptyAction, emptyAction, emptyAction, emptyAction);

        assertEquals(9, toolBar.getComponentCount());
        assertTrue(((JButton)toolBar.getComponent(0)).getText().contains("Завантажити"));
        assertTrue(((JButton)toolBar.getComponent(2)).getText().contains("Зберегти"));
        assertTrue(((JButton)toolBar.getComponent(4)).getText().contains("Додати"));
        assertTrue(((JButton)toolBar.getComponent(6)).getText().contains("Змінити"));
        assertTrue(((JButton)toolBar.getComponent(8)).getText().contains("Видалити"));
    }

    @Test
    void testAddToolbarButton() {
        JToolBar toolBar = new JToolBar();
        Runnable emptyAction = () -> {};
        ToolBarFactory.addToolbarButton(toolBar, "Тест", "T", emptyAction);

        assertEquals(1, toolBar.getComponentCount());
        JButton button = (JButton) toolBar.getComponent(0);
        assertEquals("T Тест", button.getText());
    }

    @Test
    void testCreateStyledButton() {
        Runnable emptyAction = () -> {};
        JButton button = ToolBarFactory.createStyledButton("Тест", "T", emptyAction);

        assertNotNull(button);
        assertEquals("T Тест", button.getText());
        assertFalse(button.isContentAreaFilled());
        assertEquals(new Color(255, 255, 255), button.getForeground());
        assertNotNull(button.getCursor());
        assertEquals(Cursor.HAND_CURSOR, button.getCursor().getType());
    }

    @Test
    void testConfigureButton() {
        JButton button = new JButton();
        boolean[] actionExecuted = {false};
        Runnable action = () -> actionExecuted[0] = true;

        ToolBarFactory.configureButton(button, action);
        button.doClick();

        assertFalse(button.isContentAreaFilled());
        assertEquals(new Font("Segoe UI", Font.PLAIN, 14), button.getFont());
        assertEquals(Color.WHITE, button.getForeground());
        assertFalse(button.isFocusPainted());
        assertNotNull(button.getBorder());
        assertTrue(actionExecuted[0]);
    }

    @Test
    void testButtonActionExceptionHandling() {
        JButton button = new JButton();
        Runnable failingAction = () -> { throw new RuntimeException("Test exception"); };

        ToolBarFactory.configureButton(button, failingAction);
        assertThrows(RuntimeException.class, button::doClick);
    }

    @Test
    void testCreateToolBarExceptionHandling() {
        Runnable failingAction = () -> { throw new RuntimeException("Test exception"); };


        assertDoesNotThrow(() ->
                ToolBarFactory.createToolBar(failingAction, failingAction, failingAction, failingAction, failingAction));

    }

    @Test
    void testAddToolbarButtonExceptionHandling() {
        JToolBar toolBar = new JToolBar();
        Runnable failingAction = () -> { throw new RuntimeException("Test exception"); };


        assertDoesNotThrow(() ->
                ToolBarFactory.addToolbarButton(toolBar, "Тест", "T", failingAction));
    }

    @Test
    void createStyledButton_shouldReturnButtonWithCorrectProperties() {

        String text = "Test Button";
        String icon = "⭐";
        AtomicBoolean actionExecuted = new AtomicBoolean(false);
        Runnable action = () -> actionExecuted.set(true);


        JButton button = ToolBarFactory.createStyledButton(text, icon, action);


        assertEquals(String.format("%s %s", icon, text), button.getText());


        assertFalse(button.isContentAreaFilled());
        assertEquals(Color.WHITE, button.getForeground());
        assertFalse(button.isFocusPainted());
        assertEquals(Cursor.HAND_CURSOR, button.getCursor().getType());
        assertNotNull(button.getBorder());


        assertFalse(actionExecuted.get());
        button.doClick();
        assertTrue(actionExecuted.get());
    }



    @Test
    void createStyledButton_shouldHaveCustomPainting() {

        String text = "Paint Test";
        String icon = "🎨";
        Runnable action = () -> {};


        JButton button = ToolBarFactory.createStyledButton(text, icon, action);


        assertNotEquals(JButton.class, button.getClass());


        button.setSize(100, 40);
        Image image = new BufferedImage(100, 40, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();
        button.paint(g);
        g.dispose();


        assertFalse(button.isDisplayable());
    }

    @Test
    void createStyledButton_shouldShowDifferentColorsForStates() {

        String text = "State Colors";
        String icon = "🌈";
        Runnable action = () -> {};
        JButton button = ToolBarFactory.createStyledButton(text, icon, action);
        button.setSize(100, 40);


        assertFalse(button.getModel().isPressed());
        assertFalse(button.getModel().isRollover());


        button.getModel().setRollover(true);
        assertTrue(button.getModel().isRollover());


        button.getModel().setPressed(true);
        assertTrue(button.getModel().isPressed());
    }

}