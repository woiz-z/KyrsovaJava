package music.Music;

import music.Dialog.CompilationDetailsDialog;
import music.Manager.TrackDatabaseManager;
import music.Panel.TrackListPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Клас для обробки Drag-and-Drop функціоналу списку треків у музичній компіляції.
 * Забезпечує можливість перетягування треків для зміни їх порядку в списку.
 */
public class TrackListDragAndDropHandler {
    private static final Logger logger = LogManager.getLogger(TrackListDragAndDropHandler.class);
    private final JList<MusicTrack> trackList;
    private final DefaultListModel<MusicTrack> listModel;
    private final TrackListPanel trackListPanel;
    private final CompilationDetailsDialog parent;
    private int dragSourceIndex;

    /**
     * Конструктор обробника Drag-and-Drop.
     *
     * @param trackList      JList зі списком треків
     * @param listModel      Модель списку треків
     * @param trackListPanel Панель, що містить список треків
     * @param parent         Діалогове вікно компіляції
     */
    public TrackListDragAndDropHandler(JList<MusicTrack> trackList, DefaultListModel<MusicTrack> listModel,
                                       TrackListPanel trackListPanel, CompilationDetailsDialog parent) {
        this.trackList = trackList;
        this.listModel = listModel;
        this.trackListPanel = trackListPanel;
        this.parent = parent;
        setupDragAndDrop();
    }

    /**
     * Налаштування функціоналу Drag-and-Drop для списку треків.
     */
    private void setupDragAndDrop() {
        trackList.setDragEnabled(true);
        trackList.setDropMode(DropMode.INSERT);
        trackList.setTransferHandler(new TrackTransferHandler());

        trackList.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragSourceIndex = trackList.locationToIndex(e.getPoint());
            }
        });

        new DropTarget(trackList, new DropTargetListener() {
            @Override
            public void dragEnter(DropTargetDragEvent dtde) {
                handleDragEnter(dtde);
            }

            @Override
            public void dragOver(DropTargetDragEvent dtde) {
                handleDragOver(dtde);
            }

            @Override
            public void dropActionChanged(DropTargetDragEvent dtde) {}

            @Override
            public void dragExit(DropTargetEvent dte) {
                handleDragExit();
            }

            @Override
            public void drop(DropTargetDropEvent dtde) {
                handleDrop(dtde);
            }
        });
    }

    /**
     * Обробка початку перетягування.
     */
    private void handleDragEnter(DropTargetDragEvent dtde) {
        if (dtde.isDataFlavorSupported(new DataFlavor(MusicTrack.class, "MusicTrack"))) {
            dtde.acceptDrag(DnDConstants.ACTION_MOVE);
        } else {
            dtde.rejectDrag();
            logger.warn("Перетягування відхилено: непідтримуваний тип даних");
        }
    }

    /**
     * Обробка перетягування над списком.
     */
    private void handleDragOver(DropTargetDragEvent dtde) {
        Point location = dtde.getLocation();
        int index = trackList.locationToIndex(location);

        if (index >= 0 && index != dragSourceIndex) {
            Rectangle cellBounds = trackList.getCellBounds(index, index);
            trackList.setSelectionInterval(index, index);
            trackList.scrollRectToVisible(cellBounds);
        }
    }

    /**
     * Обробка виходу з зони перетягування.
     */
    private void handleDragExit() {
        trackList.clearSelection();
    }

    /**
     * Обробка скидання треку.
     */
    private void handleDrop(DropTargetDropEvent dtde) {
        DataFlavor trackFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");
        if (!dtde.isDataFlavorSupported(trackFlavor)) {
            dtde.rejectDrop();
            logger.warn("Скидання відхилено: непідтримуваний тип даних");
            return;
        }

        dtde.acceptDrop(DnDConstants.ACTION_MOVE);
        Point location = dtde.getLocation();
        int dropIndex = trackList.locationToIndex(location);

        if (dropIndex < 0) {
            dropIndex = listModel.getSize() - 1;
        }

        if (dragSourceIndex != dropIndex) {
            try {
                Transferable transferable = dtde.getTransferable();
                MusicTrack draggedTrack = (MusicTrack) transferable.getTransferData(trackFlavor);

                listModel.remove(dragSourceIndex);
                listModel.add(dropIndex, draggedTrack);
                trackList.setSelectedIndex(dropIndex);

                updateCompilationTracks();
                TrackDatabaseManager.updateTracksInDatabase(parent, parent.compilation, trackListPanel);

                dtde.dropComplete(true);
            } catch (UnsupportedFlavorException | IOException e) {
                logger.error("Помилка при переміщенні треку: {}", e.getMessage(), e);
                dtde.dropComplete(false);
            }
        } else {
            dtde.dropComplete(true);
        }
    }

    /**
     * Оновлення списку треків компіляції після переміщення.
     */
    private void updateCompilationTracks() {
        List<MusicTrack> updatedTracks = new ArrayList<>();
        for (int i = 0; i < listModel.getSize(); i++) {
            updatedTracks.add(listModel.get(i));
        }

        try {
            java.lang.reflect.Field tracksField = MusicCompilation.class.getDeclaredField("tracks");
            tracksField.setAccessible(true);
            List<MusicTrack> internalList = (List<MusicTrack>) tracksField.get(parent.compilation);
            internalList.clear();
            internalList.addAll(updatedTracks);
        } catch (Exception e) {
            logger.error("Помилка при оновленні списку треків: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(parent,
                    "Помилка при оновленні порядку треків: " + e.getMessage(),
                    "Помилка",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Внутрішній клас для обробки передачі даних при Drag-and-Drop.
     */
    private class TrackTransferHandler extends TransferHandler {
        private final DataFlavor trackFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");

        @Override
        public int getSourceActions(JComponent c) {
            return MOVE;
        }

        @Override
        protected Transferable createTransferable(JComponent c) {
            MusicTrack track = trackList.getSelectedValue();
            return track != null ? new TrackTransferable(track) : null;
        }

        @Override
        protected void exportDone(JComponent source, Transferable data, int action) {}
    }

    /**
     * Внутрішній клас для створення об'єкта передачі даних треку.
     */
    private class TrackTransferable implements Transferable {
        private final MusicTrack track;
        private final DataFlavor trackFlavor = new DataFlavor(MusicTrack.class, "MusicTrack");

        public TrackTransferable(MusicTrack track) {
            this.track = track;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{trackFlavor};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor.equals(trackFlavor);
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
            if (!isDataFlavorSupported(flavor)) {
                throw new UnsupportedFlavorException(flavor);
            }
            return track;
        }
    }
}