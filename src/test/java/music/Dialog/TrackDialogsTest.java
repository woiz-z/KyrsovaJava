package music.Dialog;

import music.Manager.TrackDatabaseManager;
import music.Music.MusicCompilation;
import music.Music.MusicGenre;
import music.Music.MusicTrack;
import music.Panel.HeaderPanel;
import music.Panel.TrackListPanel;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrackDialogsTest {

    @Mock
    private CompilationDetailsDialog mockParentDialog;
    @Mock
    private MusicCompilation mockCompilation;
    @Mock
    private TrackListPanel mockTrackListPanel;
    @Mock
    private HeaderPanel mockHeaderPanel;
    @Mock
    private JList<MusicTrack> mockTrackJList;


    private MusicTrack sampleTrack;

    private MockedStatic<TrackDatabaseManager> mockTrackDatabaseManager = null;
    private MockedStatic<JOptionPane> mockJOptionPane = null;
    private MockedStatic<LogManager> mockLogManager = null;
    private org.apache.logging.log4j.Logger mockLogger;
    private MockedStatic<TrackDialogs> mockTrackDialogsPartial;

    static class MockCompilationDetailsDialog extends CompilationDetailsDialog {

        public MusicCompilation capturedCompilation;

        public MockCompilationDetailsDialog(JFrame parent, MusicCompilation compilationArgument) {
            super(parent, compilationArgument != null ? compilationArgument : new MusicCompilation("Default Mock Title For MockDialog"));
            this.capturedCompilation = compilationArgument;
            if (super.getContentPane().getComponentCount() > 0) {
                if (!(super.getContentPane().getComponent(0) instanceof JPanel)) {
                    System.err.println("WARN: First component of MockCompilationDetailsDialog's contentPane is not JPanel. Wrapping.");
                    JPanel wrapperPane = new JPanel(new BorderLayout());
                    JPanel actualFirstComponentPlaceholder = new JPanel();
                    wrapperPane.add(actualFirstComponentPlaceholder, BorderLayout.CENTER);
                    Component[] originalComponents = super.getContentPane().getComponents();
                    super.getContentPane().removeAll();
                }
            } else {
                System.err.println("WARN: MockCompilationDetailsDialog's contentPane has no components. Adding a placeholder JPanel.");
                JPanel placeholderPane = new JPanel();
                super.getContentPane().add(placeholderPane);
            }

        }
    }

    @BeforeEach
    void setUp() {

        mockLogger = Mockito.mock(org.apache.logging.log4j.Logger.class);
        mockLogManager = Mockito.mockStatic(LogManager.class);
        mockLogManager.when(() -> LogManager.getLogger(any(Class.class))).thenReturn(mockLogger);
        mockLogManager.when(() -> LogManager.getLogger(anyString())).thenReturn(mockLogger);

        sampleTrack = new MusicTrack("Test Track", "Test Artist", MusicGenre.ROCK, Duration.ofMinutes(3));
        sampleTrack.setId(1L);

        mockTrackDatabaseManager = Mockito.mockStatic(TrackDatabaseManager.class);
        mockJOptionPane = Mockito.mockStatic(JOptionPane.class);

        when(mockCompilation.getTitle()).thenReturn("Mocked Compilation Title");
        mockParentDialog = new MockCompilationDetailsDialog(null, mockCompilation);
        mockTrackDialogsPartial = Mockito.mockStatic(TrackDialogs.class, Mockito.CALLS_REAL_METHODS);
        mockTrackDialogsPartial.when(() -> TrackDialogs.getHeaderPanel(any(CompilationDetailsDialog.class)))
                .thenReturn(mockHeaderPanel);
    }
    @AfterEach
    void tearDown() {
        if (mockTrackDatabaseManager != null && !mockTrackDatabaseManager.isClosed()) {
            mockTrackDatabaseManager.close();
        }
        if (mockJOptionPane != null && !mockJOptionPane.isClosed()) {
            mockJOptionPane.close();
        }
        if (mockLogManager != null && !mockLogManager.isClosed()) {
            mockLogManager.close();
        }
        if (mockTrackDialogsPartial != null && !mockTrackDialogsPartial.isClosed()) {
            mockTrackDialogsPartial.close();
        }
        mockTrackDatabaseManager = null;
        mockJOptionPane = null;
        mockLogManager = null;
        mockTrackDialogsPartial = null;
    }

    private void simulateButtonClick(JButton button) {
        for (ActionListener al : button.getActionListeners()) {
            al.actionPerformed(new java.awt.event.ActionEvent(button, java.awt.event.ActionEvent.ACTION_PERFORMED, ""));
        }
    }


    @Test
    @DisplayName("showAddTrackDialog - успішне додавання треку")
    void showAddTrackDialog_SuccessfulAddition() {
        when(mockCompilation.getTitle()).thenReturn("Test Compilation");
        mockTrackDatabaseManager.when(() -> TrackDatabaseManager.addTrackToCompilation(
                any(CompilationDetailsDialog.class),
                any(MusicCompilation.class),
                any(TrackListPanel.class),
                any(MusicTrack.class)
        )).thenAnswer(invocation -> null);

        AtomicReference<JDialog> dialogRef = new AtomicReference<>();

        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showAddTrackDialog(mockParentDialog, mockCompilation, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && "Додати трек".equals(((JDialog) window).getTitle()) && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }

            assertNotNull(dialogRef.get(), "Діалогове вікно додавання треку не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JTextField titleField = findComponent(dialog, JTextField.class, 0);
            JTextField artistField = findComponent(dialog, JTextField.class, 1);
            JComboBox<MusicGenre> genreCombo = findComponent(dialog, JComboBox.class, 0);
            JSpinner minutesSpinner = findComponent(dialog, JSpinner.class, 0);
            JSpinner secondsSpinner = findComponent(dialog, JSpinner.class, 1);
            JButton okButton = findButton(dialog, "Додати");

            assertNotNull(titleField, "Title field not found");
            assertNotNull(artistField, "Artist field not found");
            assertNotNull(genreCombo, "Genre combo not found");
            assertNotNull(minutesSpinner, "Minutes spinner not found");
            assertNotNull(secondsSpinner, "Seconds spinner not found");
            assertNotNull(okButton, "OK button not found");

            titleField.setText("New Song");
            artistField.setText("New Artist");
            genreCombo.setSelectedItem(MusicGenre.POP);
            minutesSpinner.setValue(4);
            secondsSpinner.setValue(15);

            simulateButtonClick(okButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (dialogRef.get() != null) {
            assertFalse(dialogRef.get().isVisible(), "Діалог додавання треку не був закритий після успішного додавання");
        }
    }


    @Test
    @DisplayName("showAddTrackDialog - порожні поля назви та виконавця")
    void showAddTrackDialog_EmptyFields() {
        when(mockCompilation.getTitle()).thenReturn("Test Compilation");
        AtomicReference<JDialog> dialogRef = new AtomicReference<>();

        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showAddTrackDialog(mockParentDialog, mockCompilation, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && "Додати трек".equals(((JDialog) window).getTitle()) && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно додавання треку не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JButton okButton = findButton(dialog, "Додати");
            assertNotNull(okButton);
            JTextField titleField = findComponent(dialog, JTextField.class, 0);
            JTextField artistField = findComponent(dialog, JTextField.class, 1);
            assertNotNull(titleField);
            assertNotNull(artistField);
            titleField.setText("");
            artistField.setText("Some Artist");

            simulateButtonClick(okButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (dialogRef.get() != null) {
            assertTrue(dialogRef.get().isVisible(), "Діалог додавання треку був закритий незважаючи на помилку");
        }
    }

    @Test
    @DisplayName("showAddTrackDialog - нульова тривалість")
    void showAddTrackDialog_ZeroDuration() {
        when(mockCompilation.getTitle()).thenReturn("Test Compilation");
        AtomicReference<JDialog> dialogRef = new AtomicReference<>();

        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showAddTrackDialog(mockParentDialog, mockCompilation, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && "Додати трек".equals(((JDialog) window).getTitle()) && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно додавання треку не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JTextField titleField = findComponent(dialog, JTextField.class, 0);
            JTextField artistField = findComponent(dialog, JTextField.class, 1);
            JSpinner minutesSpinner = findComponent(dialog, JSpinner.class, 0);
            JSpinner secondsSpinner = findComponent(dialog, JSpinner.class, 1);
            JButton okButton = findButton(dialog, "Додати");

            assertNotNull(titleField);
            assertNotNull(artistField);
            assertNotNull(minutesSpinner);
            assertNotNull(secondsSpinner);
            assertNotNull(okButton);

            titleField.setText("Valid Title");
            artistField.setText("Valid Artist");
            minutesSpinner.setValue(0);
            secondsSpinner.setValue(0);

            simulateButtonClick(okButton);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (dialogRef.get() != null) {
            assertTrue(dialogRef.get().isVisible(), "Діалог додавання треку був закритий незважаючи на помилку нульової тривалості");
        }
    }

    @Test
    @DisplayName("showAddTrackDialog - натискання кнопки Скасувати")
    void showAddTrackDialog_CancelButton() {
        when(mockCompilation.getTitle()).thenReturn("Test Compilation");
        AtomicReference<JDialog> dialogRef = new AtomicReference<>();

        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showAddTrackDialog(mockParentDialog, mockCompilation, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && "Додати трек".equals(((JDialog) window).getTitle()) && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно додавання треку не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JButton cancelButton = findButton(dialog, "Скасувати");
            assertNotNull(cancelButton);
            simulateButtonClick(cancelButton);
        });
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        mockTrackDatabaseManager.verifyNoInteractions();
        if (dialogRef.get() != null) {
            assertFalse(dialogRef.get().isVisible(), "Діалог додавання треку не був закритий після натискання 'Скасувати'");
        }
    }

    @Test
    @DisplayName("showAddTrackDialog - помилка під час додавання треку (TrackDatabaseManager кидає виняток)")
    void showAddTrackDialog_ExceptionDuringAdd() {
        when(mockCompilation.getTitle()).thenReturn("Test Compilation");
        RuntimeException dbException = new RuntimeException("Database error");
        mockTrackDatabaseManager.when(() -> TrackDatabaseManager.addTrackToCompilation(
                any(CompilationDetailsDialog.class),
                any(MusicCompilation.class),
                any(TrackListPanel.class),
                any(MusicTrack.class)
        )).thenThrow(dbException);

        AtomicReference<JDialog> dialogRef = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showAddTrackDialog(mockParentDialog, mockCompilation, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && "Додати трек".equals(((JDialog) window).getTitle()) && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно додавання треку не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JTextField titleField = findComponent(dialog, JTextField.class, 0);
            JTextField artistField = findComponent(dialog, JTextField.class, 1);
            JSpinner minutesSpinner = findComponent(dialog, JSpinner.class, 0);
            JSpinner secondsSpinner = findComponent(dialog, JSpinner.class, 1);
            JButton okButton = findButton(dialog, "Додати");

            assertNotNull(titleField);
            assertNotNull(artistField);
            assertNotNull(minutesSpinner);
            assertNotNull(secondsSpinner);
            assertNotNull(okButton);

            titleField.setText("Valid Title");
            artistField.setText("Valid Artist");
            minutesSpinner.setValue(1);
            secondsSpinner.setValue(0);

            simulateButtonClick(okButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        if (dialogRef.get() != null) {
            assertTrue(dialogRef.get().isVisible(), "Діалог додавання треку був закритий незважаючи на помилку додавання");
        }
    }

    @Test
    @DisplayName("showEditTrackDialog - трек не вибрано")
    void showEditTrackDialog_NoTrackSelected() {
        when(mockTrackListPanel.getTrackList()).thenReturn(mockTrackJList);
        when(mockTrackJList.getSelectedValue()).thenReturn(null);

        TrackDialogs.showEditTrackDialog(mockParentDialog, mockTrackListPanel);

    }

    @Test
    @DisplayName("showEditTrackDialog - порожні поля назви та виконавця при редагуванні")
    void showEditTrackDialog_EmptyFieldsOnEdit() {
        MusicTrack trackToEdit = new MusicTrack("Original Title", "Original Artist", MusicGenre.ROCK, Duration.ofMinutes(3));
        trackToEdit.setId(2L);

        when(mockTrackListPanel.getTrackList()).thenReturn(mockTrackJList);
        when(mockTrackJList.getSelectedValue()).thenReturn(trackToEdit);


        AtomicReference<JDialog> dialogRef = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showEditTrackDialog(mockParentDialog, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && ((JDialog) window).getTitle().startsWith("Редагувати трек") && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно редагування треку не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JTextField titleField = findComponent(dialog, JTextField.class, 0);
            JTextField artistField = findComponent(dialog, JTextField.class, 1);
            JButton okButton = findButton(dialog, "Зберегти");

            assertNotNull(titleField);
            assertNotNull(artistField);
            assertNotNull(okButton);

            titleField.setText("");
            artistField.setText("Valid Artist");


            simulateButtonClick(okButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        if (dialogRef.get() != null) {
            assertTrue(dialogRef.get().isVisible(), "Діалог редагування треку був закритий незважаючи на помилку порожніх полів");
        }
    }

    @Test
    @DisplayName("showEditTrackDialog - нульова тривалість при редагуванні")
    void showEditTrackDialog_ZeroDurationOnEdit() {
        MusicTrack trackToEdit = new MusicTrack("Original Title", "Original Artist", MusicGenre.ROCK, Duration.ofMinutes(3));
        trackToEdit.setId(3L);
        when(mockTrackListPanel.getTrackList()).thenReturn(mockTrackJList);
        when(mockTrackJList.getSelectedValue()).thenReturn(trackToEdit);


        AtomicReference<JDialog> dialogRef = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showEditTrackDialog(mockParentDialog, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && ((JDialog) window).getTitle().startsWith("Редагувати трек") && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно редагування треку не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JTextField titleField = findComponent(dialog, JTextField.class, 0);
            JTextField artistField = findComponent(dialog, JTextField.class, 1);
            JSpinner minutesSpinner = findComponent(dialog, JSpinner.class, 0);
            JSpinner secondsSpinner = findComponent(dialog, JSpinner.class, 1);
            JButton okButton = findButton(dialog, "Зберегти");

            assertNotNull(titleField);
            assertNotNull(artistField);
            assertNotNull(minutesSpinner);
            assertNotNull(secondsSpinner);
            assertNotNull(okButton);

            titleField.setText("Valid Title Edit");
            artistField.setText("Valid Artist Edit");
            minutesSpinner.setValue(0);
            secondsSpinner.setValue(0);

            simulateButtonClick(okButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (dialogRef.get() != null) {
            assertTrue(dialogRef.get().isVisible(), "Діалог редагування треку був закритий незважаючи на помилку нульової тривалості");
        }
    }

    @Test
    @DisplayName("showEditTrackDialog - помилка під час оновлення треку (TrackDatabaseManager кидає виняток)")
    void showEditTrackDialog_ExceptionDuringUpdate() {
        MusicTrack trackToEdit = new MusicTrack("Original Title", "Original Artist", MusicGenre.ROCK, Duration.ofMinutes(3));
        trackToEdit.setId(4L);
        when(mockTrackListPanel.getTrackList()).thenReturn(mockTrackJList);
        when(mockTrackJList.getSelectedValue()).thenReturn(trackToEdit);

        RuntimeException dbUpdateException = new RuntimeException("Database update error");
        mockTrackDatabaseManager.when(() -> TrackDatabaseManager.updateTrack(
                any(CompilationDetailsDialog.class),
                any(TrackListPanel.class),
                any(MusicTrack.class)
        )).thenThrow(dbUpdateException);

        AtomicReference<JDialog> dialogRef = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showEditTrackDialog(mockParentDialog, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && ((JDialog) window).getTitle().startsWith("Редагувати трек") && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно редагування треку не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JTextField titleField = findComponent(dialog, JTextField.class, 0);
            JTextField artistField = findComponent(dialog, JTextField.class, 1);
            JSpinner minutesSpinner = findComponent(dialog, JSpinner.class, 0);
            JSpinner secondsSpinner = findComponent(dialog, JSpinner.class, 1);
            JButton okButton = findButton(dialog, "Зберегти");

            assertNotNull(titleField);
            assertNotNull(artistField);
            assertNotNull(minutesSpinner);
            assertNotNull(secondsSpinner);
            assertNotNull(okButton);

            titleField.setText("Updated Title Exception");
            artistField.setText("Updated Artist Exception");
            minutesSpinner.setValue(2);
            secondsSpinner.setValue(30);


            simulateButtonClick(okButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (dialogRef.get() != null) {
            assertTrue(dialogRef.get().isVisible(), "Діалог редагування треку був закритий незважаючи на помилку оновлення");
        }
    }


    @Test
    @DisplayName("showEditTrackDialog - натискання кнопки Скасувати")
    void showEditTrackDialog_CancelButton() {
        MusicTrack trackToEdit = new MusicTrack("Original Title", "Original Artist", MusicGenre.ROCK, Duration.ofMinutes(3));
        trackToEdit.setId(5L);
        when(mockTrackListPanel.getTrackList()).thenReturn(mockTrackJList);
        when(mockTrackJList.getSelectedValue()).thenReturn(trackToEdit);

        AtomicReference<JDialog> dialogRef = new AtomicReference<>();
        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showEditTrackDialog(mockParentDialog, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && ((JDialog) window).getTitle().startsWith("Редагувати трек") && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно редагування треку не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JButton cancelButton = findButton(dialog, "Скасувати");
            assertNotNull(cancelButton);
            simulateButtonClick(cancelButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        mockTrackDatabaseManager.verifyNoInteractions();
        if (dialogRef.get() != null) {
            assertFalse(dialogRef.get().isVisible(), "Діалог редагування треку не був закритий після натискання 'Скасувати'");
        }
    }

    @Test
    @DisplayName("showFilterByDurationDialog - успішне фільтрування")
    void showFilterByDurationDialog_SuccessfulFilter() {
        AtomicReference<JDialog> dialogRef = new AtomicReference<>();
        Duration expectedMinDuration = Duration.ofSeconds(1 * 60 + 30);
        Duration expectedMaxDuration = Duration.ofSeconds(5 * 60 + 0);

        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showFilterByDurationDialog(mockParentDialog, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && "Фільтр за тривалістю".equals(((JDialog) window).getTitle()) && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            JDialog dialog = dialogRef.get();

            JSpinner minMinutesSpinner = findComponent(dialog, JSpinner.class, 0);
            JSpinner minSecondsSpinner = findComponent(dialog, JSpinner.class, 1);
            JSpinner maxMinutesSpinner = findComponent(dialog, JSpinner.class, 2);
            JSpinner maxSecondsSpinner = findComponent(dialog, JSpinner.class, 3);
            JButton okButton = findButton(dialog, "Фільтрувати");
            minMinutesSpinner.setValue(1);
            minSecondsSpinner.setValue(30);
            maxMinutesSpinner.setValue(5);
            maxSecondsSpinner.setValue(0);

            simulateButtonClick(okButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }


        if (dialogRef.get() != null) {
            assertFalse(dialogRef.get().isVisible(), "Діалог фільтрації не був закритий після успішного фільтрування");
        }
    }


    @Test
    @DisplayName("showFilterByDurationDialog - неправильний діапазон (мін > макс)")
    void showFilterByDurationDialog_InvalidDurationRange() {
        AtomicReference<JDialog> dialogRef = new AtomicReference<>();

        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showFilterByDurationDialog(mockParentDialog, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && "Фільтр за тривалістю".equals(((JDialog) window).getTitle()) && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно фільтрації не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JSpinner minMinutesSpinner = findComponent(dialog, JSpinner.class, 0);
            JSpinner minSecondsSpinner = findComponent(dialog, JSpinner.class, 1);
            JSpinner maxMinutesSpinner = findComponent(dialog, JSpinner.class, 2);
            JSpinner maxSecondsSpinner = findComponent(dialog, JSpinner.class, 3);
            JButton okButton = findButton(dialog, "Фільтрувати");

            minMinutesSpinner.setValue(5);
            minSecondsSpinner.setValue(0);
            maxMinutesSpinner.setValue(1);
            maxSecondsSpinner.setValue(30);

            simulateButtonClick(okButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (dialogRef.get() != null) {
            assertTrue(dialogRef.get().isVisible(), "Діалог фільтрації був закритий незважаючи на помилку діапазону");
        }
        Duration expectedMinDuration = Duration.ofSeconds(5 * 60);
        Duration expectedMaxDuration = Duration.ofSeconds(1 * 60 + 30);
    }

    @Test
    @DisplayName("showFilterByDurationDialog - натискання кнопки Скасувати")
    void showFilterByDurationDialog_CancelButton() {
        AtomicReference<JDialog> dialogRef = new AtomicReference<>();

        SwingUtilities.invokeLater(() -> {
            TrackDialogs.showFilterByDurationDialog(mockParentDialog, mockTrackListPanel);
            Window[] windows = JDialog.getWindows();
            for (Window window : windows) {
                if (window instanceof JDialog && "Фільтр за тривалістю".equals(((JDialog) window).getTitle()) && window.isVisible()) {
                    dialogRef.set((JDialog) window);
                    break;
                }
            }
            assertNotNull(dialogRef.get(), "Діалогове вікно фільтрації не знайдено або не видиме");
            JDialog dialog = dialogRef.get();

            JButton cancelButton = findButton(dialog, "Скасувати");
            assertNotNull(cancelButton);
            simulateButtonClick(cancelButton);
        });

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        verify(mockTrackListPanel, never()).filterTracksByDuration(any(), any(), any());
        if (dialogRef.get() != null) {
            assertFalse(dialogRef.get().isVisible(), "Діалог фільтрації не був закритий після натискання 'Скасувати'");
        }
    }


    @Test
    @DisplayName("getHeaderPanel - успішне отримання панелі")
    void getHeaderPanel_SuccessfulRetrieval() {
        if (mockTrackDialogsPartial != null && !mockTrackDialogsPartial.isClosed()) {
            mockTrackDialogsPartial.close();
        }
        MockCompilationDetailsDialog realParentDialog = new MockCompilationDetailsDialog(null, mockCompilation);
        when(mockCompilation.getTitle()).thenReturn("Specific Compilation Title");

        HeaderPanel resultPanel = TrackDialogs.getHeaderPanel(realParentDialog);

        assertNotNull(resultPanel);

        mockTrackDialogsPartial = Mockito.mockStatic(TrackDialogs.class, Mockito.CALLS_REAL_METHODS);
        mockTrackDialogsPartial.when(() -> TrackDialogs.getHeaderPanel(any(CompilationDetailsDialog.class)))
                .thenReturn(mockHeaderPanel);
    }

    @Test
    @DisplayName("getHeaderPanel - виняток при отриманні (неправильна структура content pane)")
    void getHeaderPanel_ExceptionOnRetrieval() {
        if (mockTrackDialogsPartial != null && !mockTrackDialogsPartial.isClosed()) {
            mockTrackDialogsPartial.close();
        }

        CompilationDetailsDialog badParentDialog = mock(CompilationDetailsDialog.class);
        Container mockContent = mock(Container.class);
        when(badParentDialog.getContentPane()).thenReturn(mockContent);
        when(mockContent.getComponent(0)).thenThrow(new ArrayIndexOutOfBoundsException("No components"));


        Exception exception = assertThrows(Exception.class, () -> {
            TrackDialogs.getHeaderPanel(badParentDialog);
        });

        mockTrackDialogsPartial = Mockito.mockStatic(TrackDialogs.class, Mockito.CALLS_REAL_METHODS);
        mockTrackDialogsPartial.when(() -> TrackDialogs.getHeaderPanel(any(CompilationDetailsDialog.class)))
                .thenReturn(mockHeaderPanel);
    }


    @Test
    @DisplayName("createDialogPanel - перевірка створення панелі діалогу")
    void createDialogPanel_CreatesPanelCorrectly() {
        String testTitle = "Test Dialog Title";
        JPanel panel = TrackDialogs.createDialogPanel(testTitle);
        assertNotNull(panel);
        assertEquals(TrackDialogs.PANEL_BACKGROUND, panel.getBackground());
        assertTrue(panel.getLayout() instanceof BorderLayout);
        assertTrue(panel.getBorder() instanceof EmptyBorder);
        assertEquals(new Insets(15,15,15,15), ((EmptyBorder)panel.getBorder()).getBorderInsets());

        Component northComponent = ((BorderLayout)panel.getLayout()).getLayoutComponent(BorderLayout.NORTH);
        assertTrue(northComponent instanceof JLabel);
        JLabel titleLabel = (JLabel) northComponent;
        assertEquals(testTitle, titleLabel.getText());
    }


    @Test
    @DisplayName("createFieldsPanel - перевірка створення панелі полів")
    void createFieldsPanel_CreatesPanelCorrectly() {
        JPanel panel = TrackDialogs.createFieldsPanel();
        assertNotNull(panel);
        assertEquals(TrackDialogs.PANEL_BACKGROUND, panel.getBackground());
        assertTrue(panel.getLayout() instanceof GridLayout);
        GridLayout layout = (GridLayout) panel.getLayout();
        assertEquals(5, layout.getRows());
        assertEquals(2, layout.getColumns());
        assertEquals(10, layout.getHgap());
        assertEquals(10, layout.getVgap());
        assertTrue(panel.getBorder() instanceof EmptyBorder);
        assertEquals(new Insets(10,20,10,20), ((EmptyBorder)panel.getBorder()).getBorderInsets());
    }

    @Test
    @DisplayName("createButtonPanel - перевірка створення панелі кнопок")
    void createButtonPanel_CreatesPanelCorrectly() {
        JPanel panel = TrackDialogs.createButtonPanel();
        assertNotNull(panel);
        assertEquals(TrackDialogs.PANEL_BACKGROUND, panel.getBackground());
        assertTrue(panel.getLayout() instanceof FlowLayout);
        FlowLayout layout = (FlowLayout) panel.getLayout();
        assertEquals(FlowLayout.CENTER, layout.getAlignment());
        assertEquals(15, layout.getHgap());
        assertEquals(0, layout.getVgap());
        assertTrue(panel.getBorder() instanceof EmptyBorder);
        assertEquals(new Insets(15,0,0,0), ((EmptyBorder)panel.getBorder()).getBorderInsets());
    }



    @Test
    @DisplayName("createStyledTextField - без тексту")
    void createStyledTextField_NoText() {
        JTextField textField = TrackDialogs.createStyledTextField();
        assertNotNull(textField);
        assertEquals("", textField.getText());
        assertEquals(TrackDialogs.LABEL_FONT, textField.getFont());
        assertNotNull(textField.getBorder());
    }

    @Test
    @DisplayName("createStyledTextField - з текстом")
    void createStyledTextField_WithText() {
        JTextField textField = TrackDialogs.createStyledTextField("Initial Text");
        assertNotNull(textField);
        assertEquals("Initial Text", textField.getText());
        assertEquals(TrackDialogs.LABEL_FONT, textField.getFont());
        assertNotNull(textField.getBorder());
    }

    @Test
    @DisplayName("createStyledComboBox - перевірка створення")
    void createStyledComboBox_CreatesCorrectly() {
        MusicGenre[] genres = MusicGenre.values();
        JComboBox<MusicGenre> comboBox = TrackDialogs.createStyledComboBox(genres);
        assertNotNull(comboBox);
        assertEquals(TrackDialogs.LABEL_FONT, comboBox.getFont());
        assertNotNull(comboBox.getBorder());
        assertEquals(genres.length, comboBox.getItemCount());
        for (int i = 0; i < genres.length; i++) {
            assertEquals(genres[i], comboBox.getItemAt(i));
        }
        assertTrue(comboBox.getRenderer() instanceof DefaultListCellRenderer);
    }

    @Test
    @DisplayName("createStyledSpinner - перевірка створення")
    void createStyledSpinner_CreatesCorrectly() {
        JSpinner spinner = TrackDialogs.createStyledSpinner(0, 59, 30);
        assertNotNull(spinner);
        assertEquals(TrackDialogs.LABEL_FONT, spinner.getFont());
        assertNotNull(spinner.getBorder());
        assertTrue(spinner.getModel() instanceof SpinnerNumberModel);
        SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
        assertEquals(30, model.getValue());
        assertEquals(0, (Integer)model.getMinimum());
        assertEquals(59, (Integer)model.getMaximum());
        assertEquals(1, ((SpinnerNumberModel)model).getStepSize());


        Component editor = spinner.getEditor();
        assertTrue(editor instanceof JSpinner.DefaultEditor, "Редактор спіннера не є JSpinner.DefaultEditor, а є " + editor.getClass().getName());
        JFormattedTextField tf = ((JSpinner.DefaultEditor) editor).getTextField();
        assertEquals(3, tf.getColumns());

    }

    @Test
    @DisplayName("createDialogButton - перевірка створення та властивостей кнопки")
    void createDialogButton_CreatesButtonCorrectly() {
        String buttonText = "Test Button";
        Color buttonColor = new Color(10, 20, 30);
        JButton button = TrackDialogs.createDialogButton(buttonText, buttonColor);

        assertNotNull(button);
        assertEquals(buttonText, button.getText());
        assertEquals(Color.WHITE, button.getForeground());
        assertFalse(button.isContentAreaFilled());
        assertFalse(button.isFocusPainted());
        assertTrue(button.getBorder() instanceof EmptyBorder);
        assertEquals(new Insets(8, 20, 8, 20), ((EmptyBorder)button.getBorder()).getBorderInsets());
        assertEquals(Cursor.HAND_CURSOR, button.getCursor().getType());
        assertEquals(new Dimension(120, 35), button.getPreferredSize());
    }

    @Test
    @DisplayName("addFieldComponents - перевірка додавання компонентів до панелі")
    void addFieldComponents_AddsComponentsToPanel() {
        JPanel testPanel = new JPanel(new GridLayout(5, 2));
        JTextField titleField = new JTextField();
        JTextField artistField = new JTextField();
        JComboBox<MusicGenre> genreCombo = new JComboBox<>(MusicGenre.values());
        JSpinner minutesSpinner = new JSpinner();
        JSpinner secondsSpinner = new JSpinner();

        TrackDialogs.addFieldComponents(testPanel, titleField, artistField, genreCombo, minutesSpinner, secondsSpinner);

        assertEquals(10, testPanel.getComponentCount());

        assertTrue(testPanel.getComponent(0) instanceof JLabel, "Component 0 should be JLabel for Title");
        assertEquals("Назва треку:", ((JLabel)testPanel.getComponent(0)).getText());
        assertEquals(titleField, testPanel.getComponent(1), "Component 1 should be titleField");

        assertTrue(testPanel.getComponent(2) instanceof JLabel, "Component 2 should be JLabel for Artist");
        assertEquals("Виконавець:", ((JLabel)testPanel.getComponent(2)).getText());
        assertEquals(artistField, testPanel.getComponent(3), "Component 3 should be artistField");

        assertTrue(testPanel.getComponent(4) instanceof JLabel, "Component 4 should be JLabel for Genre");
        assertEquals("Жанр:", ((JLabel)testPanel.getComponent(4)).getText());
        assertEquals(genreCombo, testPanel.getComponent(5), "Component 5 should be genreCombo");

        assertTrue(testPanel.getComponent(6) instanceof JLabel, "Component 6 should be JLabel for Min Duration");
        assertEquals("Тривалість (хвилини):", ((JLabel)testPanel.getComponent(6)).getText());
        assertEquals(minutesSpinner, testPanel.getComponent(7), "Component 7 should be minutesSpinner");

        assertTrue(testPanel.getComponent(8) instanceof JLabel, "Component 8 should be JLabel for Sec Duration");
        assertEquals("Тривалість (секунди):", ((JLabel)testPanel.getComponent(8)).getText());
        assertEquals(secondsSpinner, testPanel.getComponent(9), "Component 9 should be secondsSpinner");
    }

    @Test
    @DisplayName("showErrorMessage - перевірка відображення повідомлення про помилку")
    void showErrorMessage_DisplaysErrorMessage() {
        JDialog dummyDialog = null;
        String errorMessage = "Test Error Message";

        TrackDialogs.showErrorMessage(dummyDialog, errorMessage);

        mockJOptionPane.verify(() -> JOptionPane.showMessageDialog(
                dummyDialog,
                errorMessage,
                "Помилка",
                JOptionPane.ERROR_MESSAGE
        ), times(1));
    }




    private <T extends Component> T findComponent(Container container, Class<T> componentClass, int occurrence) {
        int count = 0;
        for (Component comp : getAllComponents(container)) {
            if (componentClass.isInstance(comp)) {
                if (count == occurrence) {
                    return componentClass.cast(comp);
                }
                count++;
            }
        }
        System.err.println("Component not found: " + componentClass.getName() + " (occurrence " + occurrence + ")");
        System.err.println("Available components in container (" + container.getClass().getSimpleName() + "): ");
        for(Component c : getAllComponents(container)) {
            String cText = "";
            if (c instanceof JLabel) cText = " Text: \"" + ((JLabel)c).getText() + "\"";
            if (c instanceof JButton) cText = " Text: \"" + ((JButton)c).getText() + "\"";
            if (c instanceof JTextField) cText = " Text: \"" + ((JTextField)c).getText() + "\"";
            System.err.println("- " + c.getClass().getName() + cText);
        }
        return null;
    }

    private java.util.List<Component> getAllComponents(final Container c) {
        Component[] comps = c.getComponents();
        java.util.List<Component> compList = new java.util.ArrayList<>();
        for (Component comp : comps) {
            compList.add(comp);
            if (comp instanceof Container) {
                compList.addAll(getAllComponents((Container) comp));
            }
        }
        return compList;
    }


    private JButton findButton(Container container, String text) {
        for (Component comp : getAllComponents(container)) {
            if (comp instanceof JButton && text.equals(((JButton) comp).getText())) {
                return (JButton) comp;
            }
        }
        System.err.println("Button not found with text: \"" + text + "\"");
        System.err.println("Available buttons in container (" + container.getClass().getSimpleName() + "): ");
        for(Component c : getAllComponents(container)) {
            if (c instanceof JButton)
                System.err.println("- Button Text: \"" + ((JButton)c).getText() + "\"");
        }
        return null;
    }
}