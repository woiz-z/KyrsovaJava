package music.Models;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;
import java.time.Duration;

/**
 * Клас, що представляє музичний трек із основними атрибутами, такими як назва, виконавець, жанр та тривалість.
 * Реалізує інтерфейс Serializable для підтримки серіалізації.
 */
public class MusicTrack implements Serializable {
    private static final Logger logger = LogManager.getLogger(MusicTrack.class);

    private Long id;
    private String title;
    private String artist;
    private MusicGenre genre;
    private Duration duration;

    /**
     * Конструктор для створення нового музичного треку.
     *
     * @param title    Назва треку
     * @param artist   Виконавець треку
     * @param genre    Жанр треку
     * @param duration Тривалість треку
     * @throws IllegalArgumentException якщо передані некоректні параметри
     * @throws RuntimeException якщо сталася помилка при створенні треку
     */
    public MusicTrack(String title, String artist, MusicGenre genre, Duration duration) {
        try {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Назва треку не може бути порожньою");
            }
            if (artist == null || artist.trim().isEmpty()) {
                throw new IllegalArgumentException("Виконавець не може бути порожнім");
            }
            if (genre == null) {
                throw new IllegalArgumentException("Жанр не може бути null");
            }
            if (duration == null || duration.isNegative() || duration.isZero()) {
                throw new IllegalArgumentException("Тривалість має бути додатнім значенням");
            }

            this.title = title;
            this.artist = artist;
            this.genre = genre;
            this.duration = duration;
            logger.info("Створено новий трек: {} - {} (жанр: {}, тривалість: {} хв)",
                    title, artist, genre, duration.toMinutes());
        } catch (IllegalArgumentException e) {
            logger.error("Невірні параметри для створення треку: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Встановлює ідентифікатор треку.
     *
     * @param id Ідентифікатор для встановлення
     * @throws IllegalArgumentException якщо id є null або від'ємним
     * @throws RuntimeException якщо сталася помилка при встановленні ID
     */
    public void setId(Long id) {
        try {
            if (id == null || id < 0) {
                throw new IllegalArgumentException("ID не може бути null або від'ємним");
            }
            this.id = id;
            logger.debug("Встановлено ID {} для треку {}", id, title);
        } catch (IllegalArgumentException e) {
            logger.error("Невірний ID для треку {}: {}", title, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Встановлює назву треку.
     *
     * @param title Нова назва треку
     * @throws IllegalArgumentException якщо назва є null або порожня
     * @throws RuntimeException якщо сталася помилка при зміні назви
     */
    public void setTitle(String title) {
        try {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Назва треку не може бути порожньою");
            }
            logger.debug("Змінено назву треку з {} на {}", this.title, title);
            this.title = title;
        } catch (IllegalArgumentException e) {
            logger.error("Невірна назва треку: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Встановлює виконавця треку.
     *
     * @param artist Новий виконавець треку
     * @throws IllegalArgumentException якщо виконавець є null або порожній
     * @throws RuntimeException якщо сталася помилка при зміні виконавця
     */
    public void setArtist(String artist) {
        try {
            if (artist == null || artist.trim().isEmpty()) {
                throw new IllegalArgumentException("Виконавець не може бути порожнім");
            }
            logger.debug("Змінено виконавця треку з {} на {}", this.artist, artist);
            this.artist = artist;
        } catch (IllegalArgumentException e) {
            logger.error("Невірний виконавець треку: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Встановлює жанр треку.
     *
     * @param genre Новий жанр треку
     * @throws IllegalArgumentException якщо жанр є null
     * @throws RuntimeException якщо сталася помилка при зміні жанру
     */
    public void setGenre(MusicGenre genre) {
        try {
            if (genre == null) {
                throw new IllegalArgumentException("Жанр не може бути null");
            }
            logger.debug("Змінено жанр треку з {} на {}", this.genre, genre);
            this.genre = genre;
        } catch (IllegalArgumentException e) {
            logger.error("Невірний жанр треку: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Встановлює тривалість треку.
     *
     * @param duration Нова тривалість треку
     * @throws IllegalArgumentException якщо тривалість є null, від'ємна або нульова
     * @throws RuntimeException якщо сталася помилка при зміні тривалості
     */
    public void setDuration(Duration duration) {
        try {
            if (duration == null || duration.isNegative() || duration.isZero()) {
                throw new IllegalArgumentException("Тривалість має бути додатнім значенням");
            }
            logger.debug("Змінено тривалість треку з {} на {}", this.duration, duration);
            this.duration = duration;
        } catch (IllegalArgumentException e) {
            logger.error("Невірна тривалість треку: {}", e.getMessage(), e);
            throw e;
        }
    }


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public MusicGenre getGenre() {
        return genre;
    }

    public Duration getDuration() {
        return duration;
    }

    @Override
    public String toString() {
            String result = String.format("%s - %s (%s, %d min)",
                    title, artist, genre, duration.toMinutes());
            logger.debug("Отримано рядкове представлення треку: {}", result);
            return result;
        }
}