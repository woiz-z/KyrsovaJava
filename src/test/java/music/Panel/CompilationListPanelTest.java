package music.Panel;

import music.Music.MusicCompilation;
import music.Renderer.CompilationListRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CompilationListPanelTest {
    private DefaultListModel<MusicCompilation> listModel;
    private Consumer<MusicCompilation> detailsAction;

    @BeforeEach
    void setUp() {
        listModel = new DefaultListModel<>();
        detailsAction = mock(Consumer.class);

    }

    @Test
    void createScrollPane_ShouldReturnJScrollPane() {
        JScrollPane scrollPane = CompilationListPanel.createScrollPane(listModel, detailsAction);
        assertNotNull(scrollPane);
        assertTrue(scrollPane.getViewport().getView() instanceof JPanel);
    }

    @Test
    void initializeCompilationList_ShouldReturnJListWithRenderer() {
        JList<MusicCompilation> list = CompilationListPanel.initializeCompilationList(listModel, detailsAction);
        assertNotNull(list);
        assertTrue(list.getCellRenderer() instanceof CompilationListRenderer);
    }

    @Test
    void configureScrollPane_ShouldHaveCorrectSettings() {
        JPanel panel = new JPanel();
        JScrollPane scrollPane = CompilationListPanel.configureScrollPane(panel);
        assertNotNull(scrollPane.getViewport().getView());
        assertEquals(0, scrollPane.getBorder().getBorderInsets(panel).left); // Перевірка відступів
    }

    @Test
    void createListPanelBorder_ShouldReturnTitledBorder() {
        Border border = CompilationListPanel.createListPanelBorder();
        assertNotNull(border);
        assertTrue(border instanceof Border);
    }


    @Test
    void getSearchPanel_ShouldReturnInitializedSearchPanel() {
        CompilationListPanel.createScrollPane(listModel, detailsAction);
        assertNotNull(CompilationListPanel.getSearchPanel());
    }

    @Test
    void mouseClicked_DoubleClickWithSelection_ShouldTriggerDetailsAction() {

        JList<MusicCompilation> list = CompilationListPanel.initializeCompilationList(listModel, detailsAction);
        MusicCompilation mockCompilation = mock(MusicCompilation.class);
        listModel.addElement(mockCompilation);
        list.setSelectedIndex(0);


        MouseEvent doubleClickEvent = new MouseEvent(
                list, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, 0, 0, 2, false
        );
        for (MouseListener listener : list.getMouseListeners()) {
            listener.mouseClicked(doubleClickEvent);
        }


        verify(detailsAction, times(1)).accept(mockCompilation);
        verify(mockCompilation, times(1)).getName();
    }

    @Test
    void mouseClicked_DoubleClickWithoutSelection_ShouldLogWarning() {

        JList<MusicCompilation> list = CompilationListPanel.initializeCompilationList(listModel, detailsAction);
        Logger logger = mock(Logger.class);

        MouseEvent doubleClickEvent = new MouseEvent(
                list, MouseEvent.MOUSE_CLICKED,
                System.currentTimeMillis(), 0, 0, 0, 2, false
        );
        for (MouseListener listener : list.getMouseListeners()) {
            listener.mouseClicked(doubleClickEvent);
        }


        verify(detailsAction, never()).accept(any());

    }

    @Test
    void createScrollPane_ShouldThrowRuntimeException_WhenNullModel() {
        assertThrows(RuntimeException.class, () -> {
            CompilationListPanel.createScrollPane(null, detailsAction);
        });
    }

    @Test
    void getCompilationList_ShouldReturnInitializedList() {

        JScrollPane scrollPane = CompilationListPanel.createScrollPane(listModel, detailsAction);


        JList<MusicCompilation> result = CompilationListPanel.getCompilationList();


        assertNotNull(result);
        assertEquals(listModel, result.getModel());
        assertTrue(result.getCellRenderer() instanceof CompilationListRenderer);
    }

}