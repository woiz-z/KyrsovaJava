package music.Renderer;

import music.Models.MusicCompilation;
import music.Service.MusicCompilationService;

import javax.swing.*;
import java.awt.*;

/**
 * Клас CompilationListRenderer відповідає за кастомізоване відображення елементів списку музичних компіляцій
 * у графічному інтерфейсі Swing. Розширює DefaultListCellRenderer для створення стилізованого вигляду
 * елементів списку з інформацією про назву компіляції, кількість треків та загальну тривалість.
 */
public class CompilationListRenderer extends DefaultListCellRenderer {
    private static final Color SELECTED_BACKGROUND = new Color(220, 240, 255);
    private static final Color SELECTED_BORDER = new Color(180, 220, 255);
    private static final Color EVEN_ROW_BACKGROUND = new Color(255, 255, 255);
    private static final Color ODD_ROW_BACKGROUND = new Color(245, 248, 250);
    private static final int PADDING = 5;
    private static final int BORDER_WIDTH = 15;
    private final MusicCompilationService compilationService=new MusicCompilationService();
    /**
     * Налаштовує компонент для відображення елемента списку.
     *
     * @param list         JList, у якому відображається елемент
     * @param value        Об'єкт, що представляє елемент списку (очікується MusicCompilation)
     * @param index        Індекс елемента у списку
     * @param isSelected   Вказує, чи вибрано елемент
     * @param cellHasFocus Вказує, чи має елемент фокус
     * @return Налаштований компонент для відображення
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        if (value instanceof MusicCompilation) {
            MusicCompilation compilation = (MusicCompilation) value;
            configureText(compilation);
            configureBorders(isSelected);
            configureBackground(index, isSelected);
        }

        return this;
    }

    /**
     * Налаштовує текстовий вміст компонента з інформацією про компіляцію.
     *
     * @param compilation Об'єкт MusicCompilation, що містить дані про компіляцію
     */
    void configureText(MusicCompilation compilation) {
        setText(String.format(
                "<html><div style='padding:%dpx;'>" +
                        "<b style='font-size:14px; color:#333;'>%s</b><br>" +
                        "<span style='color:#666; font-size:12px;'>%d треків • %d хв</span>" +
                        "</div></html>",
                PADDING,
                compilation.getTitle(),
                compilation.getTracks().size(),
                compilationService.calculateTotalDuration(compilation.getTracks()).toMinutes()
        ));
    }

    /**
     * Налаштовує рамки компонента залежно від стану вибору.
     *
     * @param isSelected Вказує, чи вибрано елемент
     */
    void configureBorders(boolean isSelected) {
        if (isSelected) {
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, SELECTED_BORDER),
                    BorderFactory.createEmptyBorder(PADDING, BORDER_WIDTH, PADDING, BORDER_WIDTH)
            ));
        } else {
            setBorder(BorderFactory.createEmptyBorder(PADDING, BORDER_WIDTH, PADDING, BORDER_WIDTH));
        }
    }

    /**
     * Налаштовує колір фону компонента залежно від індексу та стану вибору.
     *
     * @param index      Індекс елемента у списку
     * @param isSelected Вказує, чи вибрано елемент
     */
    void configureBackground(int index, boolean isSelected) {
        if (isSelected) {
            setBackground(SELECTED_BACKGROUND);
        } else {
            setBackground(index % 2 == 0 ? EVEN_ROW_BACKGROUND : ODD_ROW_BACKGROUND);
        }
    }
}