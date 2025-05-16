package music.Dialog;

import music.Music.MusicCompilation;
import music.Music.MusicGenre;
import music.Music.MusicTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Діалогове вікно для відображення статистики музичної збірки.
 * Містить три вкладки: "Тривалість", "Жанри" та "Виконавці" з відповідними діаграмами.
 */
public class StatisticsDialog extends JDialog {
    private static final Logger logger = LogManager.getLogger(StatisticsDialog.class);
    private final MusicCompilation compilation;
    JTabbedPane tabbedPane;

    /**
     * Конструктор діалогового вікна статистики.
     *
     * @param parent      батьківський фрейм
     * @param compilation музична збірка для аналізу
     */
    public StatisticsDialog(JFrame parent, MusicCompilation compilation) {
        super(parent, "Статистика: " + compilation.getTitle(), true);
        this.compilation = compilation;
        logger.info("Ініціалізація діалогу статистики для збірки: {}", compilation.getTitle());

        try {
            initializeUI();
        } catch (Exception e) {
            logger.error("Помилка під час ініціалізації UI для збірки {}: {}", compilation.getTitle(), e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Виникла помилка при ініціалізації інтерфейсу.", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------------------------------------------------------------------------------------------
    // Основні методи ініціалізації UI
    // ---------------------------------------------------------------------------------------------

    /**
     * Ініціалізація користувацького інтерфейсу діалогового вікна.
     */
    void initializeUI() {
        configureWindowProperties();
        JPanel mainPanel = createMainPanel();
        add(mainPanel);
    }

    void configureWindowProperties() {
        setSize(1400, 900);
        setLocationRelativeTo(getParent());
        setResizable(true);
    }

    JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintGradientBackground(g);
            }
        };
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createTabbedPane(), BorderLayout.CENTER);
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        return mainPanel;
    }

    private void paintGradientBackground(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Color color1 = new Color(245, 248, 250);
        Color color2 = new Color(230, 235, 240);
        GradientPaint gp = new GradientPaint(0, 0, color1, 0, getHeight(), color2);
        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }

    // ---------------------------------------------------------------------------------------------
    // Компоненти головного інтерфейсу
    // ---------------------------------------------------------------------------------------------

    JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.setOpaque(false);

        JLabel titleLabel = createTitleLabel();
        JLabel infoLabel = createInfoLabel();

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(infoLabel, BorderLayout.SOUTH);

        return headerPanel;
    }

    JLabel createTitleLabel() {
        JLabel label = new JLabel("Статистика збірки: " + compilation.getTitle()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                // Тінь тексту
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawString(getText(), 3, 23);

                // Основний текст
                g2.setColor(new Color(50, 50, 50));
                g2.drawString(getText(), 2, 22);

                // Додатковий ефект
                g2.setColor(new Color(70, 130, 180, 100));
                g2.drawString(getText(), 1, 21);
            }
        };
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        return label;
    }

    JLabel createInfoLabel() {
        JLabel label = new JLabel(String.format(
                "%d треків • %d хв %d сек",
                compilation.getTracks().size(),
                compilation.calculateTotalDuration().toMinutes(),
                compilation.calculateTotalDuration().getSeconds() % 60
        ));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(100, 100, 100));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        return label;
    }

    JTabbedPane createTabbedPane() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setOpaque(false);

        try {
            addDurationTab();
            addGenreTab();
            addArtistTab();
        } catch (Exception e) {
            logger.error("Помилка під час додавання вкладок: {}", e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Виникла помилка при створенні вкладок.", "Помилка", JOptionPane.ERROR_MESSAGE);
        }

        return tabbedPane;
    }

    JPanel createButtonPanel() {
        JButton closeButton = createModernButton("Закрити", new Color(120, 120, 120));
        closeButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        return buttonPanel;
    }

    // ---------------------------------------------------------------------------------------------
    // Вкладки статистики
    // ---------------------------------------------------------------------------------------------

    void addDurationTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<MusicTrack> tracks = compilation.getTracks();
        Duration totalDuration = compilation.calculateTotalDuration();
        Duration avgDuration = tracks.isEmpty() ? Duration.ZERO :
                Duration.ofSeconds(totalDuration.getSeconds() / tracks.size());
        Duration shortest = tracks.stream().map(MusicTrack::getDuration)
                .min(Duration::compareTo).orElse(Duration.ZERO);
        Duration longest = tracks.stream().map(MusicTrack::getDuration)
                .max(Duration::compareTo).orElse(Duration.ZERO);

        JPanel statsPanel = createDurationStatsPanel(totalDuration, avgDuration, shortest, longest);
        panel.add(statsPanel, BorderLayout.NORTH);

        try {
            JPanel histogramPanel = createDurationHistogram(tracks);
            panel.add(new JScrollPane(histogramPanel), BorderLayout.CENTER);
        } catch (Exception e) {
            logger.error("Помилка при створенні гістограми тривалості: {}", e.getMessage(), e);
        }

        tabbedPane.addTab("Тривалість", panel);
    }

    JPanel createDurationStatsPanel(Duration total, Duration avg, Duration shortest, Duration longest) {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        panel.add(createStatLabel("Загальна тривалість:"));
        panel.add(createStatValue(formatDuration(total)));
        panel.add(createStatLabel("Середня тривалість:"));
        panel.add(createStatValue(formatDuration(avg)));
        panel.add(createStatLabel("Найкоротший трек:"));
        panel.add(createStatValue(formatDuration(shortest)));
        panel.add(createStatLabel("Найдовший трек:"));
        panel.add(createStatValue(formatDuration(longest)));

        return panel;
    }

    void addGenreTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Map<MusicGenre, Long> genreCounts = compilation.getTracks().stream()
                .collect(Collectors.groupingBy(MusicTrack::getGenre, Collectors.counting()));

        List<Map.Entry<MusicGenre, Long>> sortedGenres = genreCounts.entrySet().stream()
                .sorted(Map.Entry.<MusicGenre, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        JPanel statsPanel = createGenreStatsPanel(sortedGenres);
        panel.add(statsPanel, BorderLayout.NORTH);

        try {
            JPanel chartPanel = createGenreChart(sortedGenres);
            panel.add(new JScrollPane(chartPanel), BorderLayout.CENTER);
        } catch (Exception e) {
            logger.error("Помилка при створенні діаграми жанрів: {}", e.getMessage(), e);
        }

        tabbedPane.addTab("Жанри", panel);
    }

    JPanel createGenreStatsPanel(List<Map.Entry<MusicGenre, Long>> genres) {
        JPanel panel = new JPanel(new GridLayout(genres.size() + 1, 2, 10, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        panel.add(createStatLabel("Жанр", true));
        panel.add(createStatLabel("Кількість треків", true));

        for (Map.Entry<MusicGenre, Long> entry : genres) {
            panel.add(createStatLabel(entry.getKey().toString()));
            panel.add(createStatValue(entry.getValue().toString()));
        }

        return panel;
    }

    void addArtistTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Map<String, Long> artistCounts = compilation.getTracks().stream()
                .collect(Collectors.groupingBy(MusicTrack::getArtist, Collectors.counting()));

        List<Map.Entry<String, Long>> sortedArtists = artistCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        int maxArtists = Math.min(10, sortedArtists.size());
        sortedArtists = sortedArtists.subList(0, maxArtists);

        JPanel statsPanel = createArtistStatsPanel(sortedArtists);
        panel.add(statsPanel, BorderLayout.NORTH);

        try {
            JPanel chartPanel = createArtistChart(sortedArtists);
            panel.add(new JScrollPane(chartPanel), BorderLayout.CENTER);
        } catch (Exception e) {
            logger.error("Помилка при створенні діаграми виконавців: {}", e.getMessage(), e);
        }

        tabbedPane.addTab("Виконавці", panel);
    }

    JPanel createArtistStatsPanel(List<Map.Entry<String, Long>> artists) {
        JPanel panel = new JPanel(new GridLayout(artists.size() + 1, 2, 10, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        panel.add(createStatLabel("Виконавець", true));
        panel.add(createStatLabel("Кількість треків", true));

        for (Map.Entry<String, Long> entry : artists) {
            panel.add(createStatLabel(entry.getKey()));
            panel.add(createStatValue(entry.getValue().toString()));
        }

        return panel;
    }

    // ---------------------------------------------------------------------------------------------
    // Методи для створення діаграм
    // ---------------------------------------------------------------------------------------------

    JPanel createDurationHistogram(List<MusicTrack> tracks) {
        return new HistogramPanel(tracks) {
            @Override
            protected void drawData(Graphics2D g2d, int width, int height, int padding, int chartWidth, int chartHeight) {
                // Знаходимо максимальну тривалість для масштабування
                Duration maxDuration = tracks.stream()
                        .map(MusicTrack::getDuration)
                        .max(Duration::compareTo)
                        .orElse(Duration.ofMinutes(5));
                long maxSeconds = maxDuration.getSeconds();

                // Малюємо стовпці
                int barWidth = Math.max(10, chartWidth / (tracks.size() * 2));
                int x = padding + barWidth / 2;
                Color barColor = new Color(70, 130, 180, 200);

                for (MusicTrack track : tracks) {
                    drawBar(g2d, track, x, height, padding, chartHeight, maxSeconds, barWidth, barColor);
                    x += barWidth * 2;
                }
            }

            private void drawBar(Graphics2D g2d, MusicTrack track, int x, int height, int padding,
                                 int chartHeight, long maxSeconds, int barWidth, Color barColor) {
                long seconds = track.getDuration().getSeconds();
                int barHeight = (int) (chartHeight * seconds / maxSeconds);

                g2d.setColor(barColor);
                g2d.fillRect(x, height - padding - barHeight, barWidth, barHeight);
                g2d.setColor(barColor.darker());
                g2d.drawRect(x, height - padding - barHeight, barWidth, barHeight);

                if (tracks.size() < 15 || tracks.indexOf(track) % 5 == 0) {
                    drawTrackLabel(g2d, track, x, height, padding, barWidth);
                }
            }

            private void drawTrackLabel(Graphics2D g2d, MusicTrack track, int x, int height, int padding, int barWidth) {
                String title = track.getTitle().length() > 10 ?
                        track.getTitle().substring(0, 7) + "..." : track.getTitle();
                g2d.setColor(new Color(70, 70, 70));
                g2d.rotate(-Math.PI / 4, x + barWidth / 2, height - padding + 15);
                g2d.drawString(title, x - 10, height - padding + 15);
                g2d.rotate(Math.PI / 4, x + barWidth / 2, height - padding + 15);
            }

            @Override
            protected String getYAxisLabel(long maxValue, int i) {
                return String.format("%d:%02d", (maxValue * i / 5) / 60, (maxValue * i / 5) % 60);
            }

            @Override
            protected long getMaxValue() {
                return tracks.stream()
                        .map(MusicTrack::getDuration)
                        .mapToLong(Duration::getSeconds)
                        .max()
                        .orElse(300); // 5 хвилин за замовчуванням
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 300);
            }
        };
    }

    JPanel createGenreChart(List<Map.Entry<MusicGenre, Long>> genres) {
        return new BarChartPanel(genres) {
            @Override
            protected void drawBars(Graphics2D g2d, int x, int height, int padding, int chartHeight, long maxCount) {
                Color[] colors = {
                        new Color(70, 130, 180),
                        new Color(76, 175, 80),
                        new Color(244, 67, 54),
                        new Color(255, 152, 0),
                        new Color(156, 39, 176)
                };

                for (Map.Entry<MusicGenre, Long> entry : genres) {
                    drawGenreBar(g2d, entry, x, height, padding, chartHeight, maxCount, colors);
                    x += barWidth * 2;
                }
            }

            private void drawGenreBar(Graphics2D g2d, Map.Entry<MusicGenre, Long> entry, int x, int height,
                                      int padding, int chartHeight, long maxCount, Color[] colors) {
                int barHeight = (int) (chartHeight * entry.getValue() / maxCount);
                Color color = colors[genres.indexOf(entry) % colors.length];

                g2d.setColor(color);
                g2d.fillRect(x, height - padding - barHeight, barWidth, barHeight);
                g2d.setColor(color.darker());
                g2d.drawRect(x, height - padding - barHeight, barWidth, barHeight);

                drawGenreLabel(g2d, entry, x, height, padding);
                drawValueLabel(g2d, entry.getValue().toString(), x, height, padding, barHeight);
            }

            private void drawGenreLabel(Graphics2D g2d, Map.Entry<MusicGenre, Long> entry,
                                        int x, int height, int padding) {
                String genre = entry.getKey().toString();
                if (genre.length() > 10) genre = genre.substring(0, 7) + "...";
                g2d.setColor(new Color(70, 70, 70));
                g2d.drawString(genre, x - 10, height - padding + 15);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 300);
            }
        };
    }

    JPanel createArtistChart(List<Map.Entry<String, Long>> artists) {
        return new BarChartPanel(artists) {
            @Override
            protected void drawBars(Graphics2D g2d, int x, int height, int padding, int chartHeight, long maxCount) {
                Color[] colors = {
                        new Color(70, 130, 180),
                        new Color(76, 175, 80),
                        new Color(244, 67, 54),
                        new Color(255, 152, 0),
                        new Color(156, 39, 176),
                        new Color(96, 125, 139),
                        new Color(121, 85, 72),
                        new Color(233, 30, 99),
                        new Color(0, 150, 136),
                        new Color(63, 81, 181)
                };

                for (Map.Entry<String, Long> entry : artists) {
                    drawArtistBar(g2d, entry, x, height, padding, chartHeight, maxCount, colors);
                    x += barWidth * 2;
                }
            }

            private void drawArtistBar(Graphics2D g2d, Map.Entry<String, Long> entry, int x, int height,
                                       int padding, int chartHeight, long maxCount, Color[] colors) {
                int barHeight = (int) (chartHeight * entry.getValue() / maxCount);
                Color color = colors[artists.indexOf(entry) % colors.length];

                g2d.setColor(color);
                g2d.fillRect(x, height - padding - barHeight, barWidth, barHeight);
                g2d.setColor(color.darker());
                g2d.drawRect(x, height - padding - barHeight, barWidth, barHeight);

                drawArtistLabel(g2d, entry, x, height, padding);
                drawValueLabel(g2d, entry.getValue().toString(), x, height, padding, barHeight);
            }

            private void drawArtistLabel(Graphics2D g2d, Map.Entry<String, Long> entry,
                                         int x, int height, int padding) {
                String artist = entry.getKey();
                if (artist.length() > 10) artist = artist.substring(0, 7) + "...";
                g2d.setColor(new Color(70, 70, 70));
                g2d.rotate(-Math.PI / 4, x + barWidth / 2, height - padding + 15);
                g2d.drawString(artist, x - 10, height - padding + 15);
                g2d.rotate(Math.PI / 4, x + barWidth / 2, height - padding + 15);
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(800, 300);
            }
        };
    }

    // ---------------------------------------------------------------------------------------------
    // Допоміжні методи
    // ---------------------------------------------------------------------------------------------

    JLabel createStatLabel(String text) {
        return createStatLabel(text, false);
    }

    JLabel createStatLabel(String text, boolean isHeader) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", isHeader ? Font.BOLD : Font.PLAIN, 14));
        label.setForeground(isHeader ? new Color(70, 70, 70) : new Color(50, 50, 50));
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return label;
    }

    JLabel createStatValue(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(70, 130, 180));
        label.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        return label;
    }

    String formatDuration(Duration duration) {
        return String.format("%d хв %02d сек",
                duration.toMinutes(),
                duration.getSeconds() % 60);
    }

    JButton createModernButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(color.brighter());
                } else {
                    g2.setColor(color);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 50));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
            }
        };

        button.setContentAreaFilled(false);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    // ---------------------------------------------------------------------------------------------
    // Абстрактні класи для діаграм
    // ---------------------------------------------------------------------------------------------

    abstract class HistogramPanel extends JPanel {
        protected final List<MusicTrack> tracks;

        public HistogramPanel(List<MusicTrack> tracks) {
            this.tracks = tracks;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 80;
            int chartWidth = width - 2 * padding;
            int chartHeight = height - 2 * padding;

            drawAxes(g2d, width, height, padding);
            drawData(g2d, width, height, padding, chartWidth, chartHeight);
        }

        protected void drawAxes(Graphics2D g2d, int width, int height, int padding) {
            g2d.setColor(new Color(100, 100, 100));
            g2d.drawLine(padding, height - padding, width - padding, height - padding); // X
            g2d.drawLine(padding, height - padding, padding, padding); // Y

            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            long maxValue = getMaxValue();

            for (int i = 0; i <= 5; i++) {
                int y = height - padding - (i * (height - 2 * padding) / 5);
                String label = getYAxisLabel(maxValue, i);
                g2d.drawString(label, padding - 30, y + 5);
                g2d.drawLine(padding, y, padding - 5, y);
            }
        }

        protected abstract void drawData(Graphics2D g2d, int width, int height, int padding,
                                         int chartWidth, int chartHeight);
        protected abstract String getYAxisLabel(long maxValue, int i);
        protected abstract long getMaxValue();
    }

    abstract class BarChartPanel extends JPanel {
        protected int barWidth;
        protected final List<? extends Map.Entry<?, Long>> entries;

        public BarChartPanel(List<? extends Map.Entry<?, Long>> entries) {
            this.entries = entries;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 80;
            int chartWidth = width - 2 * padding;
            int chartHeight = height - 2 * padding;

            barWidth = Math.max(20, chartWidth / (entries.size() * 2));

            drawAxes(g2d, width, height, padding, chartHeight);
            drawBars(g2d, padding + barWidth / 2, height, padding, chartHeight, getMaxCount());
        }

        protected void drawAxes(Graphics2D g2d, int width, int height, int padding, int chartHeight) {
            g2d.setColor(new Color(100, 100, 100));
            g2d.drawLine(padding, height - padding, width - padding, height - padding); // X
            g2d.drawLine(padding, height - padding, padding, padding); // Y

            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            long maxCount = getMaxCount();

            for (int i = 0; i <= 5; i++) {
                int y = height - padding - (i * chartHeight / 5);
                g2d.drawString(String.valueOf(maxCount * i / 5), padding - 30, y + 5);
                g2d.drawLine(padding, y, padding - 5, y);
            }
        }

        protected void drawValueLabel(Graphics2D g2d, String value, int x, int height, int padding, int barHeight) {
            g2d.drawString(value, x + 5, height - padding - barHeight - 5);
        }

        protected abstract void drawBars(Graphics2D g2d, int x, int height, int padding,
                                         int chartHeight, long maxCount);

        protected long getMaxCount() {
            return entries.stream()
                    .mapToLong(Map.Entry::getValue)
                    .max()
                    .orElse(1);
        }
    }
}