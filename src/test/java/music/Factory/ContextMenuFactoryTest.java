package music.Factory;

import music.Music.MusicCompilation;
import music.MusicAppGUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ContextMenuFactoryTest {

    private JList<MusicCompilation> mockList;
    private JPopupMenu mockMenu;
    private MouseEvent mockEvent;
    private MusicAppGUI mockApp;
    private ContextMenuFactory.Consumer<MusicAppGUI> mockConsumer;

    @BeforeEach
    void setUp() {
        mockList = Mockito.mock(JList.class);
        mockMenu = Mockito.mock(JPopupMenu.class);
        mockEvent = Mockito.mock(MouseEvent.class);
        mockApp = Mockito.mock(MusicAppGUI.class);
        mockConsumer = Mockito.mock(ContextMenuFactory.Consumer.class);

        when(mockList.getMouseListeners()).thenReturn(new MouseAdapter[0]);
    }

    @Test
    void testMousePressedPopupTrigger() {
        // Налаштовуємо подію миші
        when(mockEvent.isPopupTrigger()).thenReturn(true);
        when(mockEvent.getPoint()).thenReturn(new Point(100, 100));
        when(mockEvent.getX()).thenReturn(100);
        when(mockEvent.getY()).thenReturn(100);
        when(mockList.locationToIndex(any(Point.class))).thenReturn(1);

        // Викликаємо метод, який тестуємо
        ContextMenuFactory.addMouseListener(mockList, mockMenu);

        // Отримуємо доданий адаптер
        ArgumentCaptor<MouseAdapter> adapterCaptor = ArgumentCaptor.forClass(MouseAdapter.class);
        verify(mockList).addMouseListener(adapterCaptor.capture());

        // Імітуємо подію миші
        adapterCaptor.getValue().mousePressed(mockEvent);

        // Перевіряємо очікувані виклики
        verify(mockList).setSelectedIndex(1);
        verify(mockMenu).show(mockList, 100, 100);
    }

    @Test
    void testMouseReleasedPopupTrigger() {
        when(mockEvent.isPopupTrigger()).thenReturn(true);
        when(mockEvent.getPoint()).thenReturn(new Point(150, 150));
        when(mockEvent.getX()).thenReturn(150);
        when(mockEvent.getY()).thenReturn(150);
        when(mockList.locationToIndex(any(Point.class))).thenReturn(2);

        ContextMenuFactory.addMouseListener(mockList, mockMenu);

        ArgumentCaptor<MouseAdapter> adapterCaptor = ArgumentCaptor.forClass(MouseAdapter.class);
        verify(mockList).addMouseListener(adapterCaptor.capture());

        adapterCaptor.getValue().mouseReleased(mockEvent);

        verify(mockList).setSelectedIndex(2);
        verify(mockMenu).show(mockList, 150, 150);
    }

    @Test
    void testNonPopupTriggerEvent() {
        when(mockEvent.isPopupTrigger()).thenReturn(false);

        ContextMenuFactory.addMouseListener(mockList, mockMenu);

        ArgumentCaptor<MouseAdapter> adapterCaptor = ArgumentCaptor.forClass(MouseAdapter.class);
        verify(mockList).addMouseListener(adapterCaptor.capture());

        adapterCaptor.getValue().mousePressed(mockEvent);
        adapterCaptor.getValue().mouseReleased(mockEvent);

        verify(mockList, never()).setSelectedIndex(anyInt());
        verify(mockMenu, never()).show(any(), anyInt(), anyInt());
    }

    @Test
    void testExceptionHandling() {
        when(mockEvent.isPopupTrigger()).thenReturn(true);
        when(mockEvent.getPoint()).thenReturn(new Point(100, 100));
        when(mockList.locationToIndex(any(Point.class))).thenThrow(new RuntimeException("Test exception"));

        ContextMenuFactory.addMouseListener(mockList, mockMenu);

        ArgumentCaptor<MouseAdapter> adapterCaptor = ArgumentCaptor.forClass(MouseAdapter.class);
        verify(mockList).addMouseListener(adapterCaptor.capture());

        adapterCaptor.getValue().mousePressed(mockEvent);

        verify(mockList).locationToIndex(any(Point.class));
    }

    @Test
    void testExecuteActionSuccess() {
        try (MockedStatic<SwingUtilities> mockedSwing = Mockito.mockStatic(SwingUtilities.class)) {
            mockedSwing.when(() -> SwingUtilities.getAncestorOfClass(MusicAppGUI.class, mockList))
                    .thenReturn(mockApp);

            ContextMenuFactory.executeAction(mockList, mockConsumer, "test action");

            verify(mockConsumer).accept(mockApp);
            // Можна додати перевірку логування, якщо використовується мок логера
        }
    }

    @Test
    void testExecuteActionNoParentComponent() {
        try (MockedStatic<SwingUtilities> mockedSwing = Mockito.mockStatic(SwingUtilities.class)) {
            mockedSwing.when(() -> SwingUtilities.getAncestorOfClass(MusicAppGUI.class, mockList))
                    .thenReturn(null);

            ContextMenuFactory.executeAction(mockList, mockConsumer, "test action");

            verify(mockConsumer, never()).accept(any());
            // Можна перевірити логування попередження
        }
    }

    @Test
    void testExecuteActionWithException() {
        try (MockedStatic<SwingUtilities> mockedSwing = Mockito.mockStatic(SwingUtilities.class)) {
            mockedSwing.when(() -> SwingUtilities.getAncestorOfClass(MusicAppGUI.class, mockList))
                    .thenReturn(mockApp);

            doThrow(new RuntimeException("Test exception")).when(mockConsumer).accept(mockApp);

            ContextMenuFactory.executeAction(mockList, mockConsumer, "test action");

            verify(mockConsumer).accept(mockApp);
            // Можна перевірити логування помилки
        }
    }

    @Test
    void testExecuteActionWithNullParameters() {
        assertDoesNotThrow(() -> {
            ContextMenuFactory.executeAction(null, null, null);
        });
    }

    @Test
    void testConfigureMenuItems_AddsEditAndDeleteItems() {
        // Підготовка
        JPopupMenu mockMenu = Mockito.mock(JPopupMenu.class);
        JList<MusicCompilation> mockList = Mockito.mock(JList.class);

        // Виклик методу, який тестуємо
        ContextMenuFactory.configureMenuItems(mockMenu, mockList);

        // Перевірка, що пункти меню були додані
        ArgumentCaptor<JMenuItem> menuItemCaptor = ArgumentCaptor.forClass(JMenuItem.class);
        verify(mockMenu, times(2)).add(menuItemCaptor.capture());

        List<JMenuItem> addedItems = menuItemCaptor.getAllValues();
        assertEquals(2, addedItems.size());
        assertEquals("Змінити назву", addedItems.get(0).getText());
        assertEquals("Видалити", addedItems.get(1).getText());
    }



    @Test
    void testConfigureMenuItems_MenuItemProperties() {
        JPopupMenu mockMenu = Mockito.mock(JPopupMenu.class);
        JList<MusicCompilation> mockList = Mockito.mock(JList.class);

        ContextMenuFactory.configureMenuItems(mockMenu, mockList);

        ArgumentCaptor<JMenuItem> menuItemCaptor = ArgumentCaptor.forClass(JMenuItem.class);
        verify(mockMenu, times(2)).add(menuItemCaptor.capture());

        List<JMenuItem> items = menuItemCaptor.getAllValues();

        // Перевіряємо властивості пунктів меню
        assertEquals(ContextMenuFactory.MAIN_FONT, items.get(0).getFont());
        assertEquals(ContextMenuFactory.MAIN_FONT, items.get(1).getFont());
        assertNotNull(items.get(0).getIcon());
        assertNotNull(items.get(1).getIcon());
        assertEquals("Змінити назву", items.get(0).getText());
        assertEquals("Видалити", items.get(1).getText());
    }

}