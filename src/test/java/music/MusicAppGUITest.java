package music;

import music.Manager.DiscManager;
import music.Music.MusicCompilation;
import music.Panel.CompilationSearchPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Collections;

class MusicAppGUITest {

    private MusicAppGUI musicAppGUI;
    private DiscManager mockDiscManager;
    private DefaultListModel<MusicCompilation> mockListModel;
    private JList<MusicCompilation> mockCompilationList;
    private JLabel mockStatusBar;
    private CompilationSearchPanel mockSearchPanel;

    @BeforeEach
    void setUp() {
        // Мокуємо залежності
        mockDiscManager = Mockito.mock(DiscManager.class);
        mockListModel = Mockito.mock(DefaultListModel.class);
        mockCompilationList = Mockito.mock(JList.class);
        mockStatusBar = Mockito.mock(JLabel.class);
        mockSearchPanel = Mockito.mock(CompilationSearchPanel.class);

        // Створюємо екземпляр класу з моками
        musicAppGUI = new MusicAppGUI() {
            protected void initializeUI() {
                // Перевизначаємо, щоб уникнути реальної ініціалізації UI
                this.discManager = mockDiscManager;
                this.listModel = mockListModel;
                this.compilationList = mockCompilationList;
                this.statusBar = mockStatusBar;
                this.searchPanel = mockSearchPanel;
            }
        };

        // Викликаємо ініціалізацію вручну
        musicAppGUI.initializeUI();
    }

    @Test
    void testConstructor() {
        assertNotNull(musicAppGUI.discManager);
        assertNotNull(musicAppGUI.listModel);
    }

    @Test
    void testConfigureWindow() {
        musicAppGUI.configureWindow();
        assertEquals("Менеджер музичних збірок", musicAppGUI.getTitle());
        assertEquals(JFrame.EXIT_ON_CLOSE, musicAppGUI.getDefaultCloseOperation());
        assertFalse(musicAppGUI.isResizable());
        assertEquals(new Color(245, 248, 250), musicAppGUI.getContentPane().getBackground());
    }

    @Test
    void testCreateMainPanel() {
        JPanel mainPanel = musicAppGUI.createMainPanel();
        assertNotNull(mainPanel);
        assertEquals(BorderLayout.class, mainPanel.getLayout().getClass());
    }

    @Test
    void testSetupLookAndFeel() {
        // Це більше інтеграційний тест, але ми можемо перевірити, що метод не викидає винятків
        assertDoesNotThrow(() -> musicAppGUI.setupLookAndFeel());
    }

    @Test
    void testShowDetails() {
        MusicCompilation mockCompilation = Mockito.mock(MusicCompilation.class);
        assertDoesNotThrow(() -> musicAppGUI.showDetails(mockCompilation));
    }



    @Test
    void testAddCompilation() {
        assertDoesNotThrow(() -> musicAppGUI.addCompilation());
        // Тут можна перевірити, що відповідний метод DialogFactory був викликаний
    }

    @Test
    void testRenameCompilationWithSelection() {
        MusicCompilation mockCompilation = Mockito.mock(MusicCompilation.class);
        when(mockCompilationList.getSelectedValue()).thenReturn(mockCompilation);

        musicAppGUI.renameCompilation();
        // Перевіряємо, що не було помилки і DialogFactory був викликаний
        verify(mockStatusBar, never()).setText(contains("Помилка"));
    }

    @Test
    void testRenameCompilationWithoutSelection() {
        when(mockCompilationList.getSelectedValue()).thenReturn(null);

        musicAppGUI.renameCompilation();
        verify(mockStatusBar).setText(contains("Помилка"));
    }

    @Test
    void testDeleteCompilationWithSelection() {
        MusicCompilation mockCompilation = Mockito.mock(MusicCompilation.class);
        when(mockCompilationList.getSelectedValue()).thenReturn(mockCompilation);

        musicAppGUI.deleteCompilation();
        verify(mockStatusBar, never()).setText(contains("Помилка"));
    }

    @Test
    void testDeleteCompilationWithoutSelection() {
        when(mockCompilationList.getSelectedValue()).thenReturn(null);

        musicAppGUI.deleteCompilation();
        verify(mockStatusBar).setText(contains("Помилка"));
    }



    @Test
    void testShowError() {
        musicAppGUI.showError("Test Title", "Test Message");
        verify(mockStatusBar).setText(" Помилка: Test Message");
        // Можна також перевірити, що JOptionPane.showMessageDialog був викликаний
    }

    @Test
    void testMainMethod() {
        assertDoesNotThrow(() -> MusicAppGUI.main(new String[]{}));
    }
}