package music.Models;

import music.Service.MusicCompilationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Клас MusicCompilation представляє збірку музичних треків.
 * Дозволяє керувати списком треків, обчислювати загальну тривалість,
 * сортувати треки за жанром та шукати треки за діапазоном тривалості.
 * Реалізує інтерфейс Serializable для можливості серіалізації.
 */
public class MusicCompilation implements Serializable {
    private static final Logger logger = LogManager.getLogger(MusicCompilation.class);
    private Long id;
    private String title;
    private final List<MusicTrack> tracks;
    private static final MusicCompilationService compilationService=new MusicCompilationService();
    /**
     * Конструктор для створення нової збірки з назвою.
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
     * Отримує ідентифікатор збірки.
     *
     * @return Ідентифікатор збірки або null, якщо не встановлено.
     */
    public Long getId() {
        return id;
    }

    /**
     * Встановлює ідентифікатор збірки.
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
     * Отримує назву збірки.
     *
     * @return Назва збірки.
     */
    public String getTitle() {
        return title;
    }


    /**
     * Встановлює нову назву збірки.
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

    public void addTrack(MusicTrack track) {
        if (track == null) {
            throw new IllegalArgumentException("Трек не може бути null");
        }
        tracks.add(track);
        logger.info("Додано трек {} до компіляції {}", track, this.title);
    }

    /**
     * Отримує копію списку треків збірки.
     *
     * @return Новий список треків.
     */
    public List<MusicTrack> getTracks() {
        return new ArrayList<>(tracks);
    }



    /**
     * Повертає текстове представлення компіляції для відображення в інтерфейсі.
     *
     * @return Рядок з назвою, кількістю треків і загальною тривалістю.
     */
    @Override
    public String toString() {
        return title + " (" + tracks.size() + " треків, " +
                compilationService.calculateTotalDuration(tracks).toMinutes() + " хв)";
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