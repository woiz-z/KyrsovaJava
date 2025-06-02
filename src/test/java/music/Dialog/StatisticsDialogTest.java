package music.Dialog;

import music.Models.MusicCompilation;
import music.Service.MusicCompilationService;
import music.Models.MusicGenre;
import music.Models.MusicTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.time.Duration;
import java.util.ArrayList;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.doubleThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsDialogTest {

    @Mock
    private MusicCompilation mockCompilation;

    private JFrame realParentFrame;

    @Mock
    private Logger mockLogger;

    private StatisticsDialog statisticsDialog;

    private MockedStatic<LogManager> mockedLogManager;
    private MockedStatic<JOptionPane> mockedJOptionPane;
    private static final MusicCompilationService compilationService=new MusicCompilationService();

    @BeforeEach
    void setUp() {
        mockedLogManager = Mockito.mockStatic(LogManager.class);
        mockedLogManager.when(() -> LogManager.getLogger(StatisticsDialog.class)).thenReturn(mockLogger);
        mockedLogManager.when(() -> LogManager.getLogger(any(Class.class))).thenReturn(mockLogger);

        mockedJOptionPane = Mockito.mockStatic(JOptionPane.class);
        realParentFrame = new JFrame();


        when(mockCompilation.getTitle()).thenReturn("Test Compilation");


        if (mockCompilation.getTracks() == null) {
            when(mockCompilation.getTracks()).thenReturn(new ArrayList<>());
        }
        if (compilationService.calculateTotalDuration(mockCompilation.getTracks()) == null) {
            when(compilationService.calculateTotalDuration(mockCompilation.getTracks())).thenReturn(Duration.ZERO);
        }

        statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation);
    }

    @AfterEach
    void tearDown() {
        if (statisticsDialog != null) {
            statisticsDialog.dispose();
        }
        if (realParentFrame != null) {
            realParentFrame.dispose();
        }
        mockedLogManager.close();
        mockedJOptionPane.close();
    }


    @Test
    void testInitializeUI() {

        if (statisticsDialog == null) {
            when(mockCompilation.getTracks()).thenReturn(new ArrayList<>());
            when(compilationService.calculateTotalDuration(mockCompilation.getTracks())).thenReturn(Duration.ZERO);
            statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation);
        }
        assertNotNull(statisticsDialog.getContentPane().getComponent(0));
        assertTrue(statisticsDialog.getContentPane().getComponent(0) instanceof JPanel);
    }

    @Test
    void testConfigureWindowProperties() {
        if (statisticsDialog == null) {
            when(mockCompilation.getTracks()).thenReturn(new ArrayList<>());
            when(compilationService.calculateTotalDuration(mockCompilation.getTracks())).thenReturn(Duration.ZERO);
            statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation);
        }
        assertEquals(1400, statisticsDialog.getWidth());
        assertEquals(900, statisticsDialog.getHeight());
        assertTrue(statisticsDialog.isResizable());
    }


    @Test
    void testCreateHeaderPanel() {
        // `mockCompilation.getTracks()` is already stubbed in `setUp`.
        // `statisticsDialog` is also initialized in `setUp`.
        // The previous incorrect `when(compilationService.calculateTotalDuration(...))` line has been removed.

        JPanel headerPanel = statisticsDialog.createHeaderPanel();
        assertNotNull(headerPanel);
        assertTrue(headerPanel.getLayout() instanceof BorderLayout);
        assertFalse(headerPanel.isOpaque());
        Component centerComponent = ((BorderLayout)headerPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        Component southComponent = ((BorderLayout)headerPanel.getLayout()).getLayoutComponent(BorderLayout.SOUTH);
        assertNotNull(centerComponent, "");
        assertTrue(centerComponent instanceof JLabel);
        assertNotNull(southComponent, "");
        assertTrue(southComponent instanceof JLabel);
    }

    @Test
    void testCreateInfoLabel() {
        List<MusicTrack> tracksForInfo = new ArrayList<>();
        tracksForInfo.add(new MusicTrack("T1", "A1", MusicGenre.POP, Duration.ofMinutes(3)));
        tracksForInfo.add(new MusicTrack("T2", "A2", MusicGenre.ROCK, Duration.ofMinutes(4)));
        when(mockCompilation.getTracks()).thenReturn(tracksForInfo); // Correctly stubs mockCompilation.getTracks() for *this test*.

        // REMOVED: This `when` call was incorrect as `compilationService` in the test is a real object.
        // The dialog itself uses its *own* internal `MusicCompilationService` instance (also real).
        // To test the info label, we want `statisticsDialog` to compute the actual duration.
        // when(compilationService.calculateTotalDuration(mockCompilation.getTracks())).thenReturn(Duration.ofMinutes(7));

        JLabel infoLabel = statisticsDialog.createInfoLabel();
        assertNotNull(infoLabel);
        // The `expectedText` relies on the `statisticsDialog`'s internal `compilationService`
        // correctly calculating the total duration of the `tracksForInfo` list (3+4=7 minutes).
        String expectedText = String.format("%d треків • %d хв %d сек",
                2, 7, 0);
        assertEquals(expectedText, infoLabel.getText());
        assertEquals("Segoe UI", infoLabel.getFont().getName());
        assertEquals(14, infoLabel.getFont().getSize());
    }

    @Test
    void testCreateTabbedPane() {

        when(mockCompilation.getTracks()).thenReturn(new ArrayList<>());
        if (statisticsDialog == null) { statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation); }


        JTabbedPane tabbedPane = statisticsDialog.createTabbedPane();
        assertNotNull(tabbedPane);
        assertEquals(3, tabbedPane.getTabCount());
        assertEquals("Тривалість", tabbedPane.getTitleAt(0));
        assertEquals("Жанри", tabbedPane.getTitleAt(1));
        assertEquals("Виконавці", tabbedPane.getTitleAt(2));
        assertFalse(tabbedPane.isOpaque());
    }


    @Test
    void testCreateButtonPanel() {
        // `statisticsDialog` is initialized in `@BeforeEach`.
        JPanel buttonPanel = statisticsDialog.createButtonPanel();
        assertNotNull(buttonPanel);
        assertTrue(buttonPanel.getLayout() instanceof FlowLayout);
        assertEquals(FlowLayout.RIGHT, ((FlowLayout) buttonPanel.getLayout()).getAlignment());
        assertFalse(buttonPanel.isOpaque());

        JButton closeButton = (JButton) buttonPanel.getComponent(0);
        assertEquals("Закрити", closeButton.getText());

        ActionEvent mockEvent = new ActionEvent(closeButton, ActionEvent.ACTION_PERFORMED, "");

        MusicCompilation newMockCompilation = mock(MusicCompilation.class);
        when(newMockCompilation.getTitle()).thenReturn("New Comp");
        // CORRECTED: Line 195 - Ensure it returns a List<MusicTrack>.
        when(newMockCompilation.getTracks()).thenReturn(new ArrayList<>());
        // REMOVED: This was another attempt to stub a real object (compilationService in the test class).
        // when(compilationService.calculateTotalDuration(mockCompilation.getTracks())).thenReturn(Duration.ZERO);

        JFrame localParent = new JFrame();
        StatisticsDialog dialogToDispose = spy(new StatisticsDialog(localParent, newMockCompilation));

        JPanel panelForSpiedDialog = dialogToDispose.createButtonPanel();
        JButton buttonForSpiedDialog = (JButton) panelForSpiedDialog.getComponent(0);

        buttonForSpiedDialog.getActionListeners()[0].actionPerformed(mockEvent);
        verify(dialogToDispose).dispose();
        dialogToDispose.dispose();
        localParent.dispose();
    }


    @Test
    void testCreateDurationStatsPanel() {
        if (statisticsDialog == null) { statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation); }
        Duration total = Duration.ofMinutes(10);
        Duration avg = Duration.ofMinutes(2);
        Duration shortest = Duration.ofMinutes(1);
        Duration longest = Duration.ofMinutes(3);
        JPanel panel = statisticsDialog.createDurationStatsPanel(total, avg, shortest, longest);
        assertNotNull(panel);
        assertEquals(8, panel.getComponentCount());
        assertEquals("Загальна тривалість:", ((JLabel) panel.getComponent(0)).getText());
        assertEquals(statisticsDialog.formatDuration(total), ((JLabel) panel.getComponent(1)).getText());
    }

    @Test
    void testCreateGenreStatsPanel() {
        if (statisticsDialog == null) { statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation); }
        Map<MusicGenre, Long> genreCounts = new HashMap<>();
        genreCounts.put(MusicGenre.ROCK, 2L);
        genreCounts.put(MusicGenre.POP, 1L);
        List<Map.Entry<MusicGenre, Long>> sortedGenres = genreCounts.entrySet().stream()
                .sorted(Map.Entry.<MusicGenre, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        JPanel panel = statisticsDialog.createGenreStatsPanel(sortedGenres);
        assertNotNull(panel);
        assertEquals(6, panel.getComponentCount()); // 2 header + 2x2 data rows
        assertEquals("Жанр", ((JLabel) panel.getComponent(0)).getText());
        assertEquals(MusicGenre.ROCK.toString(), ((JLabel) panel.getComponent(2)).getText());
        assertEquals("2", ((JLabel) panel.getComponent(3)).getText());
    }


    @Test
    void testCreateArtistStatsPanel() {
        if (statisticsDialog == null) { statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation); }
        Map<String, Long> artistCounts = new HashMap<>();
        artistCounts.put("Артист A", 2L);
        artistCounts.put("Артист B", 1L);
        List<Map.Entry<String, Long>> sortedArtists = artistCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        JPanel panel = statisticsDialog.createArtistStatsPanel(sortedArtists);
        assertNotNull(panel);
        assertEquals(6, panel.getComponentCount());
        assertEquals("Виконавець", ((JLabel) panel.getComponent(0)).getText());
        assertEquals("Артист A", ((JLabel) panel.getComponent(2)).getText());
        assertEquals("2", ((JLabel) panel.getComponent(3)).getText());
    }


    @Test
    void testCreateStatLabel() {
        if (statisticsDialog == null) { statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation); }
        JLabel label1 = statisticsDialog.createStatLabel("Test Label");
        assertEquals("Test Label", label1.getText());
        assertEquals(Font.PLAIN, label1.getFont().getStyle());

        JLabel label2 = statisticsDialog.createStatLabel("Header Label", true);
        assertEquals("Header Label", label2.getText());
        assertEquals(Font.BOLD, label2.getFont().getStyle());
    }

    @Test
    void testCreateStatValue() {
        if (statisticsDialog == null) { statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation); }
        JLabel label = statisticsDialog.createStatValue("Test Value");
        assertEquals("Test Value", label.getText());
        assertEquals(Font.BOLD, label.getFont().getStyle());
        assertEquals(new Color(70, 130, 180), label.getForeground());
    }

    @Test
    void testFormatDuration() {
        if (statisticsDialog == null) { statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation); }
        assertEquals("5 хв 30 сек", statisticsDialog.formatDuration(Duration.ofSeconds(330)));
        assertEquals("0 хв 00 сек", statisticsDialog.formatDuration(Duration.ZERO));
        assertEquals("1 хв 00 сек", statisticsDialog.formatDuration(Duration.ofMinutes(1)));
    }

    private StatisticsDialog.HistogramPanel getHistogramPanelFromDialog(StatisticsDialog dialog) {

        if (dialog.tabbedPane == null) {
            dialog.initializeUI();
        }
        JTabbedPane tabbedPane = dialog.tabbedPane;

        if (tabbedPane == null && dialog.getContentPane().getComponentCount() > 0) {
            Component mainPanelComp = dialog.getContentPane().getComponent(0);
            if (mainPanelComp instanceof JPanel) {
                JPanel mainPanel = (JPanel) mainPanelComp;
                if (mainPanel.getLayout() instanceof BorderLayout) {
                    Component centerComp = ((BorderLayout)mainPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    if (centerComp instanceof JTabbedPane) {
                        tabbedPane = (JTabbedPane) centerComp;
                    }
                }
            }
        }
        assertNotNull(tabbedPane, "");

        JPanel durationTabContent = (JPanel) tabbedPane.getComponentAt(0);
        JScrollPane scrollPane = (JScrollPane) ((BorderLayout)durationTabContent.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        return (StatisticsDialog.HistogramPanel) scrollPane.getViewport().getView();
    }


    private StatisticsDialog.BarChartPanel getBarChartPanelFromDialog(StatisticsDialog dialog, int tabIndex, String expectedTabTitle) {
        if (dialog.tabbedPane == null) {
            dialog.initializeUI();
        }
        JTabbedPane tabbedPane = dialog.tabbedPane;
        if (tabbedPane == null && dialog.getContentPane().getComponentCount() > 0) {
            Component mainPanelComp = dialog.getContentPane().getComponent(0);
            if (mainPanelComp instanceof JPanel) {
                JPanel mainPanel = (JPanel) mainPanelComp;
                if (mainPanel.getLayout() instanceof BorderLayout) {
                    Component centerComp = ((BorderLayout)mainPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                    if (centerComp instanceof JTabbedPane) {
                        tabbedPane = (JTabbedPane) centerComp;
                    }
                }
            }
        }
        assertNotNull(tabbedPane, "");
        assertTrue(tabbedPane.getTabCount() > tabIndex, "");
        assertEquals(expectedTabTitle, tabbedPane.getTitleAt(tabIndex), "");

        Component tabContent = tabbedPane.getComponentAt(tabIndex);
        if (tabContent instanceof JPanel) {
            JPanel specificTabPanel = (JPanel) tabContent;
            if (specificTabPanel.getLayout() instanceof BorderLayout) {
                Component centerComponent = ((BorderLayout) specificTabPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
                if (centerComponent instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) centerComponent;
                    Component view = scrollPane.getViewport().getView();
                    if (view instanceof StatisticsDialog.BarChartPanel) {
                        return (StatisticsDialog.BarChartPanel) view;
                    } else {
                        fail(" " + (view != null ? view.getClass().getName() : "null"));
                    }
                } else {
                    fail(" " + (centerComponent != null ? centerComponent.getClass().getName() : "null"));
                }
            } else {
                fail("");
            }
        } else {
            fail("");
        }
        return null;
    }


    private Graphics2D createMockGraphics2D() {
        Graphics2D g2dMock = mock(Graphics2D.class);
        FontMetrics mockFm = mock(FontMetrics.class);


        Mockito.lenient().when(g2dMock.create()).thenReturn(g2dMock);
        Mockito.lenient().when(g2dMock.getClipBounds()).thenReturn(new Rectangle(0, 0, 1000, 1000));
        Mockito.lenient().when(g2dMock.getColor()).thenReturn(Color.BLACK); // Default color
        Mockito.lenient().when(g2dMock.getFont()).thenReturn(new Font("SansSerif", Font.PLAIN, 12));
        Mockito.lenient().when(g2dMock.getTransform()).thenReturn(new AffineTransform());

        Mockito.lenient().when(g2dMock.getFontMetrics(any(Font.class))).thenReturn(mockFm);
        Mockito.lenient().when(mockFm.stringWidth(anyString())).thenReturn(50);
        Mockito.lenient().when(mockFm.getHeight()).thenReturn(15);
        Mockito.lenient().when(mockFm.getAscent()).thenReturn(12);



        return g2dMock;
    }

    @Test
    void testDrawBar_LogicViaPaintComponent() {
        Graphics2D g2dMock = createMockGraphics2D();

        List<MusicTrack> fewTracks = new ArrayList<>();
        MusicTrack trackShort = new MusicTrack("ShortName", "ArtistS", MusicGenre.JAZZ, Duration.ofSeconds(120));
        fewTracks.add(trackShort);

        when(mockCompilation.getTracks()).thenReturn(fewTracks);

        if (statisticsDialog != null) statisticsDialog.dispose();
        statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation);
        StatisticsDialog.HistogramPanel panelFewTracks = getHistogramPanelFromDialog(statisticsDialog);
        panelFewTracks.setSize(600, 400);

        panelFewTracks.paintComponent(g2dMock);

        int panelPadding = 80;
        long maxSecsFew = panelFewTracks.getMaxValue();
        assertEquals(120L, maxSecsFew);
        int chartHeightFew = panelFewTracks.getHeight() - 2 * panelPadding;
        int barHeightFew = (int) (chartHeightFew * trackShort.getDuration().getSeconds() / maxSecsFew);
        int chartWidthFew = panelFewTracks.getWidth() - 2 * panelPadding;
        int barWidthFew = Math.max(10, chartWidthFew / (fewTracks.size() * 2));
        int xFew = panelPadding + barWidthFew / 2;
        Color expectedBarColor = new Color(70, 130, 180, 200);

        verify(g2dMock).setColor(expectedBarColor);
        verify(g2dMock).fillRect(eq(xFew), eq(panelFewTracks.getHeight() - panelPadding - barHeightFew), eq(barWidthFew), eq(barHeightFew));
        verify(g2dMock).setColor(expectedBarColor.darker());
        verify(g2dMock).drawRect(eq(xFew), eq(panelFewTracks.getHeight() - panelPadding - barHeightFew), eq(barWidthFew), eq(barHeightFew));

        verify(g2dMock).setColor(new Color(70, 70, 70));
        // Use doubleThat for comparing doubles with a tolerance
        verify(g2dMock).rotate(eq(-Math.PI / 4), doubleThat(d -> Math.abs(d - (xFew + (double)barWidthFew / 2)) < 1e-9), doubleThat(d -> Math.abs(d - (panelFewTracks.getHeight() - panelPadding + 15)) < 1e-9) );
        verify(g2dMock).drawString(eq("ShortNa..."), eq(xFew - 10), eq(panelFewTracks.getHeight() - panelPadding + 15));
        verify(g2dMock).rotate(eq(Math.PI / 4), doubleThat(d -> Math.abs(d - (xFew + (double)barWidthFew / 2)) < 1e-9), doubleThat(d -> Math.abs(d - (panelFewTracks.getHeight() - panelPadding + 15)) < 1e-9));

        clearInvocations(g2dMock);

        List<MusicTrack> manyTracks = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            manyTracks.add(new MusicTrack("Track" + i, "ArtistM", MusicGenre.POP, Duration.ofSeconds(60 + i * 5)));
        }
        when(mockCompilation.getTracks()).thenReturn(manyTracks);
        if (statisticsDialog != null) statisticsDialog.dispose();
        statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation);
        StatisticsDialog.HistogramPanel panelManyTracks = getHistogramPanelFromDialog(statisticsDialog);
        panelManyTracks.setSize(1200, 400);

        panelManyTracks.paintComponent(g2dMock);

        int labelsDrawnCount = 0;
        int panelPaddingMany = 80;
        int chartWidthMany = panelManyTracks.getWidth() - 2 * panelPaddingMany;
        int barWidthMany = Math.max(10, chartWidthMany / (manyTracks.size() * 2));

        for (int i = 0; i < manyTracks.size(); i++) {
            MusicTrack currentTrack = manyTracks.get(i);
            int currentX = panelPaddingMany + barWidthMany / 2 + (i * barWidthMany * 2);
            if (i % 5 == 0) {
                labelsDrawnCount++;
                String title = currentTrack.getTitle().length() > 10 ? currentTrack.getTitle().substring(0, 7) + "..." : currentTrack.getTitle();
                verify(g2dMock).drawString(eq(title), eq(currentX - 10), eq(panelManyTracks.getHeight() - panelPaddingMany + 15));
            }
        }
        verify(g2dMock, times(labelsDrawnCount)).setColor(new Color(70, 70, 70));
        verify(g2dMock, times(labelsDrawnCount)).rotate(eq(-Math.PI / 4), anyDouble(), anyDouble());
        verify(g2dMock, times(labelsDrawnCount)).rotate(eq(Math.PI / 4), anyDouble(), anyDouble());

        clearInvocations(g2dMock);

        List<MusicTrack> truncationTracks = new ArrayList<>();
        MusicTrack longTitleTrack = new MusicTrack("ThisIsAVeryLongTrackTitleIndeed", "ArtistL", MusicGenre.CLASSICAL, Duration.ofSeconds(180));
        truncationTracks.add(longTitleTrack);

        when(mockCompilation.getTracks()).thenReturn(truncationTracks);
        if (statisticsDialog != null) statisticsDialog.dispose();
        statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation);
        StatisticsDialog.HistogramPanel panelTruncation = getHistogramPanelFromDialog(statisticsDialog);
        panelTruncation.setSize(600, 400);

        panelTruncation.paintComponent(g2dMock);

        String expectedTruncatedTitle = "ThisIsA...";
        int panelPaddingTrunc = 80;
        int chartWidthTrunc = panelTruncation.getWidth() - 2 * panelPaddingTrunc;
        int barWidthTrunc = Math.max(10, chartWidthTrunc / (truncationTracks.size() * 2));
        int xTrunc = panelPaddingTrunc + barWidthTrunc / 2;

        verify(g2dMock).drawString(eq(expectedTruncatedTitle), eq(xTrunc - 10), eq(panelTruncation.getHeight() - panelPaddingTrunc + 15));
    }

    @Test
    void testDrawArtistBar_EffectsViaPaintComponent() {

        Map.Entry<String, Long> artist1 = new AbstractMap.SimpleEntry<>("ArtistUno", 70L);
        Map.Entry<String, Long> artist2 = new AbstractMap.SimpleEntry<>("LongArtistNameNumeroDos", 30L);


        List<MusicTrack> tracks = new ArrayList<>();

        for (int i = 0; i < artist1.getValue(); i++) {
            tracks.add(new MusicTrack("T_A1_" + i, artist1.getKey(), MusicGenre.ROCK, Duration.ofMinutes(3)));
        }
        for (int i = 0; i < artist2.getValue(); i++) {
            tracks.add(new MusicTrack("T_A2_" + i, artist2.getKey(), MusicGenre.POP, Duration.ofMinutes(4)));
        }
        when(mockCompilation.getTracks()).thenReturn(tracks);


        if (statisticsDialog != null) statisticsDialog.dispose();
        statisticsDialog = new StatisticsDialog(realParentFrame, mockCompilation);


        StatisticsDialog.BarChartPanel artistChartPanel = getBarChartPanelFromDialog(statisticsDialog, 2, "Виконавці");
        assertNotNull(artistChartPanel, "");


        @SuppressWarnings("unchecked")
        List<Map.Entry<String, Long>> panelEntries = (List<Map.Entry<String, Long>>) artistChartPanel.entries;

        assertNotNull(panelEntries);
        assertEquals(2, panelEntries.size(), "Panel should have 2 artist entries based on input tracks.");

        assertEquals(artist1.getKey(), panelEntries.get(0).getKey());
        assertEquals(artist1.getValue(), panelEntries.get(0).getValue());
        assertEquals(artist2.getKey(), panelEntries.get(1).getKey());
        assertEquals(artist2.getValue(), panelEntries.get(1).getValue());


        Graphics2D g2dMock = createMockGraphics2D();


        artistChartPanel.setSize(800, 500);
        artistChartPanel.paintComponent(g2dMock);


        int panelWidth = artistChartPanel.getWidth();
        int panelHeight = artistChartPanel.getHeight();
        int padding = 80;
        int chartWidth = panelWidth - 2 * padding;
        int chartHeight = panelHeight - 2 * padding;
        long maxCount = artist1.getValue();


        int expectedBarWidth = Math.max(20, chartWidth / (panelEntries.size() * 2));

        Color[] predefinedColors = {
                new Color(70, 130, 180), new Color(76, 175, 80), new Color(244, 67, 54),
                new Color(255, 152, 0), new Color(156, 39, 176), new Color(96, 125, 139),
                new Color(121, 85, 72), new Color(233, 30, 99), new Color(0, 150, 136),
                new Color(63, 81, 181)
        };

        InOrder inOrder = Mockito.inOrder(g2dMock);


        Map.Entry<String, Long> entry1 = panelEntries.get(0);
        int barHeight1 = (int) (chartHeight * entry1.getValue() / maxCount);

        Color color1 = predefinedColors[0 % predefinedColors.length];
        int x1 = padding + expectedBarWidth / 2;
        int y_rect1 = panelHeight - padding - barHeight1;

        inOrder.verify(g2dMock).setColor(color1);
        inOrder.verify(g2dMock).fillRect(x1, y_rect1, expectedBarWidth, barHeight1);
        inOrder.verify(g2dMock).setColor(color1.darker());
        inOrder.verify(g2dMock).drawRect(x1, y_rect1, expectedBarWidth, barHeight1);


        String labelText1 = entry1.getKey();
        double rotateCenterX1 = x1 + (double)expectedBarWidth / 2;
        double rotateCenterY1 = panelHeight - padding + 15;
        inOrder.verify(g2dMock).setColor(new Color(70, 70, 70));
        inOrder.verify(g2dMock).rotate(eq(-Math.PI / 4), doubleThat(d -> Math.abs(d - rotateCenterX1) < 1e-9), doubleThat(d -> Math.abs(d - rotateCenterY1) < 1e-9));
        inOrder.verify(g2dMock).drawString("ArtistU...", x1 - 10, panelHeight - padding + 15);
        inOrder.verify(g2dMock).rotate(eq(Math.PI / 4), doubleThat(d -> Math.abs(d - rotateCenterX1) < 1e-9), doubleThat(d -> Math.abs(d - rotateCenterY1) < 1e-9));

        inOrder.verify(g2dMock).drawString(entry1.getValue().toString(), x1 + 5, panelHeight - padding - barHeight1 - 5);


        Map.Entry<String, Long> entry2 = panelEntries.get(1);
        int barHeight2 = (int) (chartHeight * entry2.getValue() / maxCount);
        Color color2 = predefinedColors[1 % predefinedColors.length];
        int x2 = x1 + expectedBarWidth * 2;
        int y_rect2 = panelHeight - padding - barHeight2;

        inOrder.verify(g2dMock).setColor(color2);
        inOrder.verify(g2dMock).fillRect(x2, y_rect2, expectedBarWidth, barHeight2);
        inOrder.verify(g2dMock).setColor(color2.darker());
        inOrder.verify(g2dMock).drawRect(x2, y_rect2, expectedBarWidth, barHeight2);


        String originalLabelText2 = entry2.getKey();
        String expectedLabelText2 = originalLabelText2.substring(0, 7) + "...";
        double rotateCenterX2 = x2 + (double)expectedBarWidth / 2;
        double rotateCenterY2 = panelHeight - padding + 15;
        inOrder.verify(g2dMock).setColor(new Color(70, 70, 70));
        inOrder.verify(g2dMock).rotate(eq(-Math.PI / 4), doubleThat(d -> Math.abs(d - rotateCenterX2) < 1e-9), doubleThat(d -> Math.abs(d - rotateCenterY2) < 1e-9));
        inOrder.verify(g2dMock).drawString(expectedLabelText2, x2 - 10, panelHeight - padding + 15);
        inOrder.verify(g2dMock).rotate(eq(Math.PI / 4), doubleThat(d -> Math.abs(d - rotateCenterX2) < 1e-9), doubleThat(d -> Math.abs(d - rotateCenterY2) < 1e-9));


        inOrder.verify(g2dMock).drawString(entry2.getValue().toString(), x2 + 5, panelHeight - padding - barHeight2 - 5);


        inOrder.verifyNoMoreInteractions();
    }
}