package music.Renderer;

import music.Music.MusicTrack;
import javax.swing.*;
import java.awt.*;
import java.time.Duration;

/**
 * Клас ModernTrackListRenderer відповідає за кастомізоване відображення елементів списку музичних треків у JList.
 * Використовує HTML-форматування для створення сучасного вигляду з назвою треку, виконавцем, жанром і тривалістю.
 * Забезпечує стилізацію для виділеного та невиділеного станів, а також чергування кольорів фону для парного/непарного індексу.
 */
public class ModernTrackListRenderer extends DefaultListCellRenderer {

    // Константи для стилізації
    private static final Color SELECTED_BACKGROUND = new Color(220, 240, 255);
    private static final Color SELECTED_BORDER_COLOR = new Color(180, 220, 255);
    private static final Color EVEN_ROW_BACKGROUND = new Color(255, 255, 255, 200);
    private static final Color ODD_ROW_BACKGROUND = new Color(245, 248, 250, 200);
    private static final int PADDING = 5;
    private static final int BORDER_WIDTH = 1;

    /**
     * Повертає компонент для відображення елемента списку JList.
     * Налаштовує текст, фон, межі та форматування для кожного треку.
     *
     * @param list         JList, в якому відображається елемент
     * @param value        Об'єкт, що представляє елемент списку (очікується MusicTrack)
     * @param index        Індекс елемента в списку
     * @param isSelected   Вказує, чи вибрано елемент
     * @param cellHasFocus Вказує, чи має елемент фокус
     * @return Налаштований компонент для відображення
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        // Виклик батьківського методу для базового налаштування
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Налаштування базового відступу
        setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING * 2, PADDING, PADDING * 2));

        // Перевірка, чи є об'єкт MusicTrack
        if (value instanceof MusicTrack) {
            MusicTrack track = (MusicTrack) value;
            configureTrackDisplay(track, isSelected, index);
        }

        return this;
    }

    /**
     * Налаштовує відображення інформації про трек, включаючи текст, фон і межі.
     *
     * @param track      Музичний трек для відображення
     * @param isSelected Чи вибрано трек
     * @param index      Індекс треку в списку
     */
    private void configureTrackDisplay(MusicTrack track, boolean isSelected, int index) {
        // Форматування тексту треку з використанням HTML
        setText(formatTrackText(track));

        // Налаштування стилю в залежності від стану вибору
        if (isSelected) {
            setBackground(SELECTED_BACKGROUND);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, BORDER_WIDTH, SELECTED_BORDER_COLOR),
                    BorderFactory.createEmptyBorder(PADDING, PADDING * 2, PADDING, PADDING * 2)));
        } else {
            setBackground(index % 2 == 0 ? EVEN_ROW_BACKGROUND : ODD_ROW_BACKGROUND);
        }
    }

    /**
     * Формує HTML-рядок для відображення інформації про трек.
     *
     * @param track Музичний трек
     * @return HTML-рядок з форматованою інформацією
     */
    private String formatTrackText(MusicTrack track) {
        Duration duration = track.getDuration();
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;

        return String.format(
                "<html><div style='padding:%dpx;'>" +
                        "<b style='font-size:14px; color:#333;'>%s</b><br>" +
                        "<span style='color:#666; font-size:12px;'>%s • <span style='color:#5e8c31;'>%s</span> • %d:%02d</span>" +
                        "</div></html>",
                PADDING,
                track.getTitle(),
                track.getArtist(),
                track.getGenre(),
                minutes,
                seconds);
    }
}