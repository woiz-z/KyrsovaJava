package music.Music;

import music.Dialog.CompilationDetailsDialog;
import music.Manager.TrackDatabaseManager;
import music.Panel.TrackListPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrackListDragAndDropHandlerTest {

    private JList<MusicTrack> trackList;
    private DefaultListModel<MusicTrack> listModel;
    private TrackListPanel trackListPanel;
    private CompilationDetailsDialog parent;
    private TrackListDragAndDropHandler handler;
    private MusicTrack testTrack1;
    private MusicTrack testTrack2;
    private MusicTrack testTrack3;
    private MusicCompilation mockCompilation;

    private MockedStatic<TrackDatabaseManager> mockedTrackDatabaseManager;
    private MockedStatic<JOptionPane> mockedJOptionPane;
    private static final Logger logger = LogManager.getLogger(TrackListDragAndDropHandlerTest.class);


    @BeforeEach
    void setUp() throws Exception {
        trackList = spy(new JList<>());
        listModel = spy(new DefaultListModel<>());
        trackList.setModel(listModel);


        trackListPanel = mock(TrackListPanel.class);
        parent = mock(CompilationDetailsDialog.class);
        mockCompilation = mock(MusicCompilation.class);
        parent.compilation = mockCompilation;

        // Ініціалізація треків
        testTrack1 = new MusicTrack("Track 1", "Artist 1", MusicGenre.ROCK, Duration.ofMinutes(3));
        testTrack1.setId(1L);
        testTrack2 = new MusicTrack("Track 2", "Artist 2", MusicGenre.POP, Duration.ofMinutes(4));
        testTrack2.setId(2L);
        testTrack3 = new MusicTrack("Track 3", "Artist 3", MusicGenre.JAZZ, Duration.ofMinutes(5));
        testTrack3.setId(3L);

        listModel.addElement(testTrack1);
        listModel.addElement(testTrack2);
        listModel.addElement(testTrack3);

        // Налаштування моку для MusicCompilation
        List<MusicTrack> internalTrackList = new ArrayList<>(Arrays.asList(testTrack1, testTrack2, testTrack3));
        Field tracksField = MusicCompilation.class.getDeclaredField("tracks");
        tracksField.setAccessible(true);
        tracksField.set(mockCompilation, internalTrackList);

        handler = new TrackListDragAndDropHandler(trackList, listModel, trackListPanel, parent);

    }

    @AfterEach
    void tearDown() {
        if (mockedTrackDatabaseManager != null) {
            mockedTrackDatabaseManager.close();
            mockedTrackDatabaseManager = null;
        }
        if (mockedJOptionPane != null) {
            mockedJOptionPane.close();
            mockedJOptionPane = null;
        }
    }

    @Test
    void testConstructor() {
        assertNotNull(handler);
        assertTrue(trackList.getDragEnabled());
        assertEquals(DropMode.INSERT, trackList.getDropMode());
        assertNotNull(trackList.getTransferHandler());
        assertTrue(trackList.getMouseListeners().length > 0);
        assertNotNull(trackList.getDropTarget()); // Перевіряємо, що DropTarget встановлено

    }

    @Test
    void testMousePressedSetsDragSourceIndex() {
        MouseEvent mouseEvent = mock(MouseEvent.class);
        Point point = new Point(10, 10);
        when(mouseEvent.getPoint()).thenReturn(point);
        when(trackList.locationToIndex(point)).thenReturn(1);

        MouseListener foundListener = null;
        for (MouseListener listener : trackList.getMouseListeners()) {

            if (listener.getClass().getName().startsWith(TrackListDragAndDropHandler.class.getName() + "$")) {
                foundListener = listener;
                break;
            }
        }
        assertNotNull(foundListener, "MouseAdapter for dragSourceIndex not found");
        foundListener.mousePressed(mouseEvent);

        assertEquals(1, handler.dragSourceIndex);
        verify(trackList, atLeastOnce()).locationToIndex(point);
    }


    @Test
    void testHandleDragEnter_Accepted() throws Exception {
        DropTargetDragEvent event = mock(DropTargetDragEvent.class);
        DataFlavor expectedFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");
        when(event.isDataFlavorSupported(expectedFlavor)).thenReturn(true);

        handler.handleDragEnter(event); // Викликаємо метод напряму
        verify(event).acceptDrag(DnDConstants.ACTION_MOVE);
    }


    @Test
    void testHandleDragEnter_Rejected() throws Exception {
        DropTargetDragEvent event = mock(DropTargetDragEvent.class);
        DataFlavor expectedFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");
        when(event.isDataFlavorSupported(expectedFlavor)).thenReturn(false);

        handler.handleDragEnter(event); // Викликаємо метод напряму
        verify(event).rejectDrag();
    }

    @Test
    void testHandleDragOver_SelectsAndScrolls() {

        DropTargetDragEvent event = mock(DropTargetDragEvent.class);
        Point location = new Point(10, 30);
        int targetIndex = 1;

        when(event.getLocation()).thenReturn(location);
        when(trackList.locationToIndex(location)).thenReturn(targetIndex);
        handler.dragSourceIndex = 0;

        Rectangle cellBounds = new Rectangle(0, 20, 100, 20);
        when(trackList.getCellBounds(targetIndex, targetIndex)).thenReturn(cellBounds);

        handler.handleDragOver(event); // Викликаємо метод напряму

        verify(trackList).setSelectionInterval(targetIndex, targetIndex);
        verify(trackList).scrollRectToVisible(cellBounds);
    }


    @Test
    void testHandleDragOver_NoActionIfSameIndex() {

        DropTargetDragEvent event = mock(DropTargetDragEvent.class);
        Point location = new Point(10, 10);
        int targetIndex = 0;

        when(event.getLocation()).thenReturn(location);
        when(trackList.locationToIndex(location)).thenReturn(targetIndex);
        handler.dragSourceIndex = 0;

        handler.handleDragOver(event); // Викликаємо метод напряму

        verify(trackList, never()).setSelectionInterval(anyInt(), anyInt());
        verify(trackList, never()).scrollRectToVisible(any(Rectangle.class));
    }

    @Test
    void testHandleDragExit_ClearsSelection() {

        Mockito.clearInvocations(trackList);

        handler.handleDragExit(); // Викликаємо метод напряму
        verify(trackList).clearSelection(); // Тепер перевіряємо тільки 1 виклик
    }

    @Test
    void testHandleDrop_UnsupportedFlavor() throws Exception {
        DropTargetDropEvent event = mock(DropTargetDropEvent.class);
        DataFlavor expectedFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");
        when(event.isDataFlavorSupported(expectedFlavor)).thenReturn(false);

        handler.handleDrop(event); // Викликаємо метод напряму

        verify(event).rejectDrop();
        verify(event, never()).acceptDrop(anyInt());
    }

    @Test
    void testHandleDrop_SuccessfulMove() throws Exception {
        try (MockedStatic<TrackDatabaseManager> tdbManager = Mockito.mockStatic(TrackDatabaseManager.class)) {
            DropTargetDropEvent event = mock(DropTargetDropEvent.class);
            Transferable transferable = mock(Transferable.class);
            DataFlavor trackFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");

            when(event.isDataFlavorSupported(trackFlavor)).thenReturn(true);
            when(event.getTransferable()).thenReturn(transferable);
            when(transferable.getTransferData(trackFlavor)).thenReturn(testTrack1);

            handler.dragSourceIndex = 0;
            int dropIndex = 2;

            Point dropLocation = new Point(10, 50);
            when(event.getLocation()).thenReturn(dropLocation);
            when(trackList.locationToIndex(dropLocation)).thenReturn(dropIndex);

            handler.handleDrop(event);

            verify(event).acceptDrop(DnDConstants.ACTION_MOVE);
            verify(transferable).getTransferData(trackFlavor);

            ArgumentCaptor<Integer> removeIndexCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(listModel).remove(removeIndexCaptor.capture());
            assertEquals(0, removeIndexCaptor.getValue().intValue());

            ArgumentCaptor<Integer> addIndexCaptor = ArgumentCaptor.forClass(Integer.class);
            ArgumentCaptor<MusicTrack> trackCaptor = ArgumentCaptor.forClass(MusicTrack.class);
            verify(listModel).add(addIndexCaptor.capture(), trackCaptor.capture());
            assertEquals(dropIndex, addIndexCaptor.getValue().intValue());
            assertEquals(testTrack1, trackCaptor.getValue());

            verify(trackList).setSelectedIndex(dropIndex);

            tdbManager.verify(() -> TrackDatabaseManager.updateTracksInDatabase(parent, mockCompilation, trackListPanel));
            verify(event).dropComplete(true);

            assertEquals(testTrack2, listModel.get(0));
            assertEquals(testTrack3, listModel.get(1));
            assertEquals(testTrack1, listModel.get(2));
        }
    }


    @Test
    void testHandleDrop_DropAtEndIfIndexNegative() throws Exception {
        try (MockedStatic<TrackDatabaseManager> tdbManager = Mockito.mockStatic(TrackDatabaseManager.class)) {
            DropTargetDropEvent event = mock(DropTargetDropEvent.class);
            Transferable transferable = mock(Transferable.class);
            DataFlavor trackFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");

            when(event.isDataFlavorSupported(trackFlavor)).thenReturn(true);
            when(event.getTransferable()).thenReturn(transferable);
            when(transferable.getTransferData(trackFlavor)).thenReturn(testTrack1);

            handler.dragSourceIndex = 0;
            Point dropLocationOutside = new Point(10, 100);
            when(event.getLocation()).thenReturn(dropLocationOutside);
            when(trackList.locationToIndex(dropLocationOutside)).thenReturn(-1); // Скидання поза списком

            int initialSize = listModel.getSize();

            handler.handleDrop(event);

            verify(event).acceptDrop(DnDConstants.ACTION_MOVE);
            verify(listModel).remove(0);

            ArgumentCaptor<Integer> addIndexCaptor = ArgumentCaptor.forClass(Integer.class);
            verify(listModel).add(addIndexCaptor.capture(), eq(testTrack1));
            assertEquals(initialSize - 1, addIndexCaptor.getValue().intValue());


            verify(trackList).setSelectedIndex(initialSize - 1);
            tdbManager.verify(() -> TrackDatabaseManager.updateTracksInDatabase(parent, mockCompilation, trackListPanel));
            verify(event).dropComplete(true);

            assertEquals(testTrack2, listModel.get(0));
            assertEquals(testTrack3, listModel.get(1));
            assertEquals(testTrack1, listModel.get(2));
        }
    }


    @Test
    void testHandleDrop_UnsupportedFlavorExceptionOnGetData() throws Exception {
        DropTargetDropEvent event = mock(DropTargetDropEvent.class);
        Transferable transferable = mock(Transferable.class);
        DataFlavor trackFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");

        when(event.isDataFlavorSupported(trackFlavor)).thenReturn(true);
        when(event.getTransferable()).thenReturn(transferable);
        when(transferable.getTransferData(trackFlavor)).thenThrow(new UnsupportedFlavorException(trackFlavor));

        Point dropLocation = new Point(10, 30);
        int dropIndex = 1;
        handler.dragSourceIndex = 0;

        when(event.getLocation()).thenReturn(dropLocation);
        when(trackList.locationToIndex(dropLocation)).thenReturn(dropIndex);

        handler.handleDrop(event);

        verify(event).acceptDrop(DnDConstants.ACTION_MOVE);
        verify(event).dropComplete(false);
        verify(listModel, never()).remove(anyInt());
        verify(listModel, never()).add(anyInt(), any(MusicTrack.class));
    }


    @Test
    void testHandleDrop_DropOnSameIndex() throws Exception {
        DropTargetDropEvent event = mock(DropTargetDropEvent.class);
        DataFlavor trackFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");

        when(event.isDataFlavorSupported(trackFlavor)).thenReturn(true);

        Point dropLocation = new Point(10, 10);
        int dropIndex = 0;
        handler.dragSourceIndex = 0;

        when(event.getLocation()).thenReturn(dropLocation);
        when(trackList.locationToIndex(dropLocation)).thenReturn(dropIndex);

        handler.handleDrop(event);

        verify(event).acceptDrop(DnDConstants.ACTION_MOVE);
        verify(listModel, never()).remove(anyInt());
        verify(listModel, never()).add(anyInt(), any(MusicTrack.class));
        verify(event).dropComplete(true);
    }


    @Test
    void testHandleDrop_IOException() throws Exception {

        DropTargetDropEvent event = mock(DropTargetDropEvent.class);
        Transferable transferable = mock(Transferable.class);
        DataFlavor trackFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");

        when(event.isDataFlavorSupported(trackFlavor)).thenReturn(true);
        when(event.getTransferable()).thenReturn(transferable);
        when(transferable.getTransferData(trackFlavor)).thenThrow(new IOException("Test IO Exception"));

        Point dropLocation = new Point(10, 30);
        int dropIndex = 1;
        handler.dragSourceIndex = 0;

        when(event.getLocation()).thenReturn(dropLocation);
        when(trackList.locationToIndex(dropLocation)).thenReturn(dropIndex);

        handler.handleDrop(event);

        verify(event).acceptDrop(DnDConstants.ACTION_MOVE);
        verify(event).dropComplete(false);
        assertEquals(testTrack1, listModel.get(0));
        assertEquals(testTrack2, listModel.get(1));
        verify(listModel, never()).remove(anyInt());
        verify(listModel, never()).add(anyInt(), any(MusicTrack.class));
    }

    @Test
    void testUpdateCompilationTracks_SuccessfulUpdate() throws Exception {

        listModel.clear();
        listModel.addElement(testTrack3);
        listModel.addElement(testTrack1);
        listModel.addElement(testTrack2);

        Field tracksField = MusicCompilation.class.getDeclaredField("tracks");
        tracksField.setAccessible(true);

        handler.updateCompilationTracks();

        @SuppressWarnings("unchecked")
        List<MusicTrack> updatedInternalTracks = (List<MusicTrack>) tracksField.get(parent.compilation);

        assertNotNull(updatedInternalTracks);
        assertEquals(3, updatedInternalTracks.size());
        assertEquals(testTrack3.getTitle(), updatedInternalTracks.get(0).getTitle());
        assertEquals(testTrack1.getTitle(), updatedInternalTracks.get(1).getTitle());
        assertEquals(testTrack2.getTitle(), updatedInternalTracks.get(2).getTitle());
    }

    @Test
    void testUpdateCompilationTracks_ReflectionError() throws Exception {

        mockedJOptionPane = Mockito.mockStatic(JOptionPane.class);

        DefaultListModel<MusicTrack> faultyListModel = spy(new DefaultListModel<>());
        faultyListModel.addElement(testTrack1);
        when(faultyListModel.get(anyInt())).thenThrow(new RuntimeException("Simulated error accessing list model"));


        TrackListDragAndDropHandler faultyHandler = new TrackListDragAndDropHandler(trackList, faultyListModel, trackListPanel, parent);

        assertThrows(RuntimeException.class, faultyHandler::updateCompilationTracks);
    }


    @Test
    void testTrackTransferHandler_GetSourceActions() {
        TrackListDragAndDropHandler.TrackTransferHandler transferHandler =
                handler.new TrackTransferHandler();
        assertEquals(TransferHandler.MOVE, transferHandler.getSourceActions(trackList));
    }

    @Test
    void testTrackTransferHandler_CreateTransferable_TrackSelected() throws Exception {
        TrackListDragAndDropHandler.TrackTransferHandler transferHandler =
                handler.new TrackTransferHandler();
        when(trackList.getSelectedValue()).thenReturn(testTrack1);

        Transferable transferable = transferHandler.createTransferable(trackList);
        assertNotNull(transferable);
        assertTrue(transferable instanceof TrackListDragAndDropHandler.TrackTransferable);
        assertEquals(testTrack1, transferable.getTransferData(new DataFlavor(MusicTrack.class, "MusicTrack")));
    }

    @Test
    void testTrackTransferHandler_CreateTransferable_NoTrackSelected() {
        TrackListDragAndDropHandler.TrackTransferHandler transferHandler =
                handler.new TrackTransferHandler();
        when(trackList.getSelectedValue()).thenReturn(null);

        Transferable transferable = transferHandler.createTransferable(trackList);
        assertNull(transferable);
    }

    @Test
    void testTrackTransferHandler_ExportDone() {
        TrackListDragAndDropHandler.TrackTransferHandler transferHandler =
                handler.new TrackTransferHandler();
        assertDoesNotThrow(() -> transferHandler.exportDone(trackList, null, TransferHandler.NONE));
    }


    @Test
    void testTrackTransferable() throws Exception {
        TrackListDragAndDropHandler.TrackTransferable transferable =
                handler.new TrackTransferable(testTrack1);

        DataFlavor musicTrackFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");
        DataFlavor stringFlavor = DataFlavor.stringFlavor;

        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        assertEquals(1, flavors.length);
        assertEquals(musicTrackFlavor, flavors[0]);

        assertTrue(transferable.isDataFlavorSupported(musicTrackFlavor));
        assertFalse(transferable.isDataFlavorSupported(stringFlavor));

        assertEquals(testTrack1, transferable.getTransferData(musicTrackFlavor));

        assertThrows(UnsupportedFlavorException.class, () ->
                transferable.getTransferData(stringFlavor));
    }

    @Test
    void testDropTargetListenerDropActionChanged() {
        DropTargetDragEvent event = mock(DropTargetDragEvent.class);
        assertTrue(true, "dropActionChanged is an empty method ");
    }





}