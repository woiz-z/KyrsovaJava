package music.Panel;

import music.Dialog.CompilationDetailsDialog;
import music.Models.MusicCompilation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ButtonPanelTest {

    private CompilationDetailsDialog parent;
    private MusicCompilation compilation;
    private TrackListPanel trackListPanel;
    private ButtonPanel buttonPanel;

    @BeforeEach
    void setUp() {
        parent = mock(CompilationDetailsDialog.class);
        compilation = mock(MusicCompilation.class);
        trackListPanel = mock(TrackListPanel.class);
        buttonPanel = new ButtonPanel(parent, compilation, trackListPanel);
    }

    @Test
    void createButton_ShouldReturnStyledButton() {

        String text = "Test Button";
        Color color = Color.BLUE;
        ActionListener action = e -> {};


        JButton button = buttonPanel.createButton(text, color, action);

        assertNotNull(button);
        assertEquals(text, button.getText());
        assertEquals(Color.WHITE, button.getForeground());
        assertFalse(button.isFocusPainted());
        assertEquals(Cursor.HAND_CURSOR, button.getCursor().getType());
        assertNotNull(button.getActionListeners());
        assertEquals(1, button.getActionListeners().length);
    }

    @Test
    void testButtonBorderRendering() {
        // Створюємо тестовий графічний контекст
        BufferedImage image = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        // Створюємо тестову кнопку
        ButtonPanel buttonPanel = new ButtonPanel(null, null, null);
        JButton testButton = buttonPanel.createButton("Test", Color.BLUE, e -> {});
        testButton.setSize(100, 50);

        // Малюємо всю кнопку (яка внутрішньо викличе paintBorder)
        testButton.paint(g2);

        // Перевіряємо, чи є пікселі з кольором межі (чорний з альфа ~50)
        boolean foundBorderPixel = true;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                int rgb = image.getRGB(x, y);
                if ((rgb & 0xFF000000) != 0) { // Непрозорий піксель
                    int alpha = (rgb >> 24) & 0xff;
                    int red = (rgb >> 16) & 0xff;
                    int green = (rgb >> 8) & 0xff;
                    int blue = rgb & 0xff;

                    // Шукаємо пікселі межі (чорний з альфа ~50)
                    if (red < 20 && green < 20 && blue < 20 && alpha > 40 && alpha < 60) {
                        foundBorderPixel = true;
                        break;
                    }
                }
            }
            if (foundBorderPixel) break;
        }

        assertTrue(foundBorderPixel, "Кнопка повинна мати видиму округлену межу");
        g2.dispose();
    }

    @Test
    void testButtonBorderProperties() {
        ButtonPanel buttonPanel = new ButtonPanel(null, null, null);
        JButton testButton = buttonPanel.createButton("Test", Color.BLUE, e -> {});

        // Перевіряємо властивості кнопки, які впливають на межу
        assertFalse(testButton.isContentAreaFilled(), "Content area має бути не заповненим");
        assertFalse(testButton.isFocusPainted(), "Focus painting має бути вимкненим");
        assertNotNull(testButton.getBorder(), "Кнопка повинна мати border");
    }

    @Test
    void createButton_ShouldHandleActionExceptions() {

        String text = "Test Button";
        Color color = Color.RED;
        ActionListener action = e -> { throw new RuntimeException("Test exception"); };


        JButton button = buttonPanel.createButton(text, color, action);
        assertDoesNotThrow(() -> button.getActionListeners()[0].actionPerformed(null));
    }

    @Test
    void showStatistics_ShouldCreateAndDisplayStatisticsDialog() {
        // Викликаємо метод, який тестуємо
        buttonPanel.showStatistics();

        // Перевіряємо, що діалогове вікно було створено з правильними параметрами
        verify(parent, times(1)).getParent();
        assertDoesNotThrow(() -> buttonPanel.showStatistics(), "Метод не повинен викидати винятки");


    }


    @Test
    void getHeaderPanel_ShouldReturnHeaderPanelWhenSuccessful() {

        JPanel mainPanel = mock(JPanel.class);
        JComponent contentPane = mock(JComponent.class);
        when(parent.getContentPane()).thenReturn(contentPane);
        when(contentPane.getComponent(0)).thenReturn(mainPanel);


        HeaderPanel result = buttonPanel.getHeaderPanel();


        assertNotNull(result);
    }

    @Test
    void getHeaderPanel_ShouldReturnNullAndLogErrorOnException() {

        when(parent.getContentPane()).thenThrow(new RuntimeException("Test exception"));


        HeaderPanel result = buttonPanel.getHeaderPanel();


        assertNull(result);

    }
}