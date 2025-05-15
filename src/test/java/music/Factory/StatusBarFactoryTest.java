package music.Factory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.swing.*;
import java.awt.*;

@ExtendWith(MockitoExtension.class)
class StatusBarFactoryTest {

    @Test
    void createStatusBar_shouldReturnConfiguredJLabel() {
        // Act
        JLabel statusBar = StatusBarFactory.createStatusBar();

        // Assert
        assertNotNull(statusBar);
        assertEquals(" CREATED BY IHOR", statusBar.getText());
        assertEquals(new Font("Segoe UI", Font.PLAIN, 14), statusBar.getFont());
        assertEquals(new Color(100, 100, 100), statusBar.getForeground());
        assertEquals(new Color(255, 255, 255), statusBar.getBackground());
        assertTrue(statusBar.isOpaque());
        assertNotNull(statusBar.getBorder());
    }

    @Test
    void createStatusBar_shouldThrowRuntimeExceptionWhenCreationFails() {
        try (MockedConstruction<JLabel> mocked = mockConstruction(JLabel.class,
                withSettings().defaultAnswer(invocation -> {
                    throw new RuntimeException("Simulated error during JLabel creation");
                }))) {

            // Act & Assert
            Exception exception = assertThrows(RuntimeException.class, () -> {
                StatusBarFactory.createStatusBar();
            });

            assertEquals("Не вдалося створити статусний рядок", exception.getMessage());
            assertTrue(exception.getCause() instanceof RuntimeException);
            assertEquals("Simulated error during JLabel creation", exception.getCause().getMessage());
        }
    }

    @Test
    void configureStatusBar_shouldSetCorrectProperties() {
        // Arrange
        JLabel testLabel = new JLabel();

        // Act
        StatusBarFactory.configureStatusBar(testLabel);

        // Assert
        assertNotNull(testLabel.getBorder());
        assertEquals(new Font("Segoe UI", Font.PLAIN, 14), testLabel.getFont());
        assertEquals(new Color(100, 100, 100), testLabel.getForeground());
        assertEquals(new Color(255, 255, 255), testLabel.getBackground());
        assertTrue(testLabel.isOpaque());
    }
}