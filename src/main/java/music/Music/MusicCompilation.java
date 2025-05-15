package music.Music;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Клас MusicCompilation представляє компіляцію музичних треків.
 * Дозволяє керувати списком треків, обчислювати загальну тривалість,
 * сортувати треки за жанром та шукати треки за діапазоном тривалості.
 * Реалізує інтерфейс Serializable для можливості серіалізації.
 */
public class MusicCompilation implements Serializable {
    private static final Logger logger = LogManager.getLogger(MusicCompilation.class);
    private Long id; // Ідентифікатор для інтеграції з базою даних
    private String title; // Назва компіляції
    private final List<MusicTrack> tracks; // Список треків

    /**
     * Конструктор для створення нової компіляції з назвою.
     *
     * @param title Назва компіляції, не може бути null або порожньою.
     * @throws IllegalArgumentException якщо назва порожня або null.
     */
    public MusicCompilation(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва компіляції не може бути порожньою");
        }
        this.title = title;
        this.tracks = new ArrayList<>();
        logger.info("Створено нову компіляцію: {}", title);
    }

    /**
     * Отримує ідентифікатор компіляції.
     *
     * @return Ідентифікатор компіляції або null, якщо не встановлено.
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор компіляції.
     *
     * @param id Ідентифікатор, не може бути від'ємним.
     * @throws IllegalArgumentException якщо id від'ємний.
     */
    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID не може бути від'ємним");
        }
        this.id = id;
        logger.debug("Встановлено ID компіляції: {}", id);
    }

    /**
     * Отримує назву компіляції.
     *
     * @return Назва компіляції.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Встановлює нову назву компіляції.
     *
     * @param title Нова назва, не може бути null або порожньою.
     * @throws IllegalArgumentException якщо назва порожня або null.
     */
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Назва компіляції не може бути порожньою");
        }
        logger.debug("Змінено назву компіляції з {} на {}", this.title, title);
        this.title = title;
    }

    /**
     * Отримує копію списку треків компіляції.
     *
     * @return Новий список треків.
     */
    public List<MusicTrack> getTracks() {
        return new ArrayList<>(tracks);
    }

    /**
     * Додає трек до компіляції.
     *
     * @param track Музичний трек, не може бути null.
     * @throws IllegalArgumentException якщо трек null.
     */
    public void addTrack(MusicTrack track) {
        if (track == null) {
            throw new IllegalArgumentException("Трек не може бути null");
        }
        tracks.add(track);
        logger.info("Додано трек {} до компіляції {}", track, title);
    }

    /**
     * Обчислює загальну тривалість усіх треків у компіляції.
     *
     * @return Загальна тривалість компіляції.
     * @throws RuntimeException якщо сталася помилка під час обчислення.
     */
    public Duration calculateTotalDuration() {
        Duration total = tracks.stream()
                .map(MusicTrack::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
        logger.debug("Розраховано загальну тривалість компіляції {}: {} хвилин",
                title, total.toMinutes());
        return total;
    }

    /**
     * Сортує треки в компіляції за жанром.
     *
     * @throws RuntimeException якщо сталася помилка під час сортування.
     */
    public void sortByGenre() {
        tracks.sort(Comparator.comparing(track -> track.getGenre().toString()));
        logger.info("Компіляцію {} відсортовано за жанром", title);
    }

    /**
     * Шукає треки в заданому діапазоні тривалості.
     *
     * @param min Мінімальна тривалість (включно), не може бути null або від'ємною.
     * @param max Максимальна тривалість (включно), не може бути null або від'ємною.
     * @return Список треків у заданому діапазоні тривалості.
     * @throws IllegalArgumentException якщо параметри некоректні.
     */
    public List<MusicTrack> findTracksByDurationRange(Duration min, Duration max) {
        if (min == null || max == null) {
            throw new IllegalArgumentException("Мінімальна або максимальна тривалість не може бути null");
        }
        if (min.isNegative() || max.isNegative()) {
            throw new IllegalArgumentException("Тривалість не може бути від'ємною");
        }
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Мінімальна тривалість не може бути більшою за максимальну");
        }

        List<MusicTrack> result = tracks.stream()
                .filter(track -> !track.getDuration().minus(min).isNegative() &&
                        !track.getDuration().minus(max).isPositive())
                .collect(Collectors.toList());

        logger.info("Знайдено {} треків у діапазоні від {} до {} для компіляції {}",
                result.size(), min.toMinutes(), max.toMinutes(), title);
        return result;
    }

    /**
     * Повертає текстове представлення компіляції для відображення в інтерфейсі.
     *
     * @return Рядок з назвою, кількістю треків і загальною тривалістю.
     */
    @Override
    public String toString() {
        return title + " (" + tracks.size() + " треків, " +
                calculateTotalDuration().toMinutes() + " хв)";
    }

    /**
     * Застарілий метод, повертає null.
     *
     * @return Завжди null.
     * @deprecated Не використовується, залишений для сумісності.
     */
    @Deprecated
    public Throwable getName() {
        return null;
    }
}