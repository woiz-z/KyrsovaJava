package music;

import music.Dialog.CompilationDetailsDialog;
import music.Factory.DialogFactory;
import music.Factory.StatusBarFactory;
import music.Factory.ToolBarFactory;
import music.Manager.DiscManager;
import music.Music.MusicCompilation;
import music.Panel.CompilationListPanel;
import music.Panel.CompilationSearchPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Клас MusicAppGUI реалізує графічний інтерфейс користувача для управління музичними збірками.
 * Дозволяє додавати, перейменовувати, видаляти, зберігати та завантажувати музичні збірки,
 * а також переглядати їх деталі.
 */
public class MusicAppGUI extends JFrame {
    private static final Logger logger = LogManager.getLogger(MusicAppGUI.class);
    private static final Color BACKGROUND_COLOR = new Color(245, 248, 250);
    private static final int WINDOW_WIDTH = 900;
    private static final int WINDOW_HEIGHT = 650;

    private final DiscManager discManager;
    private final DefaultListModel<MusicCompilation> listModel;
    private JList<MusicCompilation> compilationList;
    private JLabel statusBar;
    private CompilationSearchPanel searchPanel;

    /**
     * Конструктор ініціалізує графічний інтерфейс та налаштовує список збірок.
     */
    public MusicAppGUI() {
        discManager = new DiscManager();
        listModel = new DefaultListModel<>();
        initializeUI();
        refreshCompilationList();
    }

    /**
     * Ініціалізує основні компоненти графічного інтерфейсу.
     */
    private void initializeUI() {
        configureWindow();
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
        setupLookAndFeel();
    }

    /**
     * Налаштовує параметри вікна програми.
     */
    private void configureWindow() {
        setTitle("Менеджер музичних збірок");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    /**
     * Створює головну панель із панеллю інструментів, списком збірок та статусним рядком.
     *
     * @return налаштована головна панель
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        JToolBar toolBar = ToolBarFactory.createToolBar(
                this::loadFromFile,
                this::saveToFile,
                this::addCompilation,
                this::renameCompilation,
                this::deleteCompilation
        );
        mainPanel.add(toolBar, BorderLayout.NORTH);

        JScrollPane scrollPane = CompilationListPanel.createScrollPane(listModel, this::showDetails);
        compilationList = CompilationListPanel.getCompilationList();
        searchPanel = CompilationListPanel.getSearchPanel();
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        statusBar = StatusBarFactory.createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Налаштовує зовнішній вигляд інтерфейсу відповідно до системного стилю.
     */
    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("ScrollPane.background", BACKGROUND_COLOR);
            UIManager.put("Viewport.background", Color.WHITE);
            UIManager.put("List.background", Color.WHITE);
            UIManager.put("Panel.background", BACKGROUND_COLOR);
            UIManager.put("ToolBar.background", Color.WHITE);
        } catch (Exception e) {
            logger.error("Помилка налаштування вигляду інтерфейсу: {}", e.getMessage(), e);
        }
    }

    /**
     * Відображає діалогове вікно з деталями обраної збірки.
     *
     * @param compilation музична збірка для відображення
     */
    private void showDetails(MusicCompilation compilation) {
        new CompilationDetailsDialog(this, compilation).setVisible(true);
    }

    /**
     * Завантажує дані з файлу, оновлює список збірок і відображає статус.
     */
    private void loadFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                discManager.loadFromFile(filePath);
                refreshCompilationList();
                statusBar.setText(" Успішно завантажено з файлу");
            } catch (IOException | ClassNotFoundException ex) {
                showError("Помилка завантаження з файлу", ex.getMessage());
            }
        }
    }

    /**
     * Зберігає дані у файл і відображає статус.
     */
    private void saveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getPath();
                discManager.saveToFile(filePath);
                statusBar.setText(" Успішно збережено у файл");
            } catch (IOException ex) {
                showError("Помилка збереження у файл", ex.getMessage());
            }
        }
    }

    /**
     * Відкриває діалогове вікно для додавання нової збірки.
     */
    private void addCompilation() {
        DialogFactory.showAddCompilationDialog(this, discManager, listModel, statusBar);
    }

    /**
     * Відкриває діалогове вікно для перейменування обраної збірки.
     */
    public void renameCompilation() {
        MusicCompilation selected = compilationList.getSelectedValue();
        if (selected == null) {
            showError("Помилка", "Спочатку виберіть збірку для перейменування");
            return;
        }
        DialogFactory.showRenameCompilationDialog(this, discManager, listModel, statusBar, selected);
    }

    /**
     * Відкриває діалогове вікно для видалення обраної збірки.
     */
    public void deleteCompilation() {
        MusicCompilation selected = compilationList.getSelectedValue();
        if (selected == null) {
            showError("Помилка", "Спочатку виберіть збірку для видалення");
            return;
        }
        DialogFactory.showDeleteCompilationDialog(this, discManager, listModel, statusBar, selected);
    }

    /**
     * Оновлює список збірок у моделі та пошуковій панелі.
     */
    private void refreshCompilationList() {
        listModel.clear();
        discManager.getCompilations().forEach(listModel::addElement);
        if (searchPanel != null) {
            searchPanel.updateCompilationList(discManager.getCompilations());
        }
    }

    /**
     * Відображає повідомлення про помилку у статусному рядку та діалоговому вікні.
     *
     * @param title   заголовок помилки
     * @param message текст помилки
     */
    private void showError(String title, String message) {
        statusBar.setText(" Помилка: " + message);
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Точка входу програми. Запускає графічний інтерфейс.
     *
     * @param args аргументи командного рядка (не використовуються)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MusicAppGUI app = new MusicAppGUI();
            app.setVisible(true);
        });
    }
}