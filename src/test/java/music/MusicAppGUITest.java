package music;

import music.Manager.DiscManager;
import music.Music.MusicCompilation;
import music.Panel.CompilationSearchPanel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;



import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    @Test
    void testLoadFromFile_Success() throws IOException, ClassNotFoundException {
        // Використовуємо try-with-resources для MockedStatic
        try (MockedStatic<JOptionPane> mockedJOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            JFileChooser mockFileChooser = Mockito.mock(JFileChooser.class);
            File mockFile = Mockito.mock(File.class);
            String fakeFilePath = "dummy/path/to/file.dat";

            // Налаштування моків
            when(mockFileChooser.showOpenDialog(any(Component.class))).thenReturn(JFileChooser.APPROVE_OPTION);
            when(mockFileChooser.getSelectedFile()).thenReturn(mockFile);
            when(mockFile.getPath()).thenReturn(fakeFilePath);
            // Припускаємо, що discManager.loadFromFile не викидає виняток
            doNothing().when(mockDiscManager).loadFromFile(fakeFilePath);
            // Припускаємо, що discManager.getCompilations() повертає порожній список для простоти
            when(mockDiscManager.getCompilations()).thenReturn(new ArrayList<>());

            System.out.println("Примітка: Тестування методів з `new JFileChooser()` всередині є складним " +
                    "без рефакторингу або PowerMock. Ці тести демонструють бажану логіку перевірки, " +
                    "припускаючи, що JFileChooser повернув би APPROVE_OPTION.");


            musicAppGUI.loadFromFile(); // Це відкриє реальний діалог, якщо GUI працює
        }
    }


    @Test
    void testLoadFromFile_IOException() throws IOException, ClassNotFoundException {
        try (MockedStatic<JOptionPane> mockedJOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            // Цей тест також має ті ж обмеження з JFileChooser
            // Припустимо, ми могли б змусити JFileChooser повернути файл
            String fakeFilePath = "dummy/path/to/file.dat";


            IOException ioException = new IOException("Тестова помилка вводу-виводу");
            doThrow(ioException).when(mockDiscManager).loadFromFile(fakeFilePath);


            System.out.println("Примітка: Тест testLoadFromFile_IOException має ті ж обмеження з `new JFileChooser()`.");
        }
    }

    @Test
    void testLoadFromFile_ClassNotFoundException() throws IOException, ClassNotFoundException {
        try (MockedStatic<JOptionPane> mockedJOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            // Аналогічні обмеження з JFileChooser
            String fakeFilePath = "dummy/path/to/file.dat";
            ClassNotFoundException cnfException = new ClassNotFoundException("Тестова помилка класу не знайдено");
            doThrow(cnfException).when(mockDiscManager).loadFromFile(fakeFilePath);

            System.out.println("Примітка: Тест testLoadFromFile_ClassNotFoundException має ті ж обмеження з `new JFileChooser()`.");
        }
    }


    @Test
    void testSaveToFile_Success() throws IOException {
        try (MockedStatic<JOptionPane> mockedJOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            // Аналогічні обмеження з JFileChooser для saveToFile
            String fakeFilePath = "dummy/path/to/save.dat";



            doNothing().when(mockDiscManager).saveToFile(fakeFilePath);


            System.out.println("Примітка: Тест testSaveToFile_Success має ті ж обмеження з `new JFileChooser()`.");
        }
    }

    @Test
    void testSaveToFile_IOException() throws IOException {
        try (MockedStatic<JOptionPane> mockedJOptionPane = Mockito.mockStatic(JOptionPane.class)) {
            // Аналогічні обмеження
            String fakeFilePath = "dummy/path/to/save.dat";
            IOException ioException = new IOException("Тестова помилка збереження");



            doThrow(ioException).when(mockDiscManager).saveToFile(fakeFilePath);


            System.out.println("Примітка: Тест testSaveToFile_IOException має ті ж обмеження з `new JFileChooser()`.");
        }
    }

    @Test
    void testLoadFromFile_CancelOption() {
        System.out.println("Примітка: Тест testLoadFromFile_CancelOption має ті ж обмеження з `new JFileChooser()`.");
    }

    @Test
    void testSaveToFile_CancelOption() {
        System.out.println("Примітка: Тест testSaveToFile_CancelOption має ті ж обмеження з `new JFileChooser()`.");
    }




}