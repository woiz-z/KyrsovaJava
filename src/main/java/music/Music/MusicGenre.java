package music.Music;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serializable;

/**
 * Перерахування, що представляє різні жанри музики.
 * Кожен жанр має назву, яка використовується для відображення.
 * Реалізує інтерфейс Serializable для підтримки серіалізації.
 */
public enum MusicGenre implements Serializable {
    ROCK("Rock"),
    POP("Pop"),
    JAZZ("Jazz"),
    CLASSICAL("Classical"),
    ELECTRONIC("Electronic"),
    HIP_HOP("Hip Hop"),
    RAP("Rap"),
    BLUES("Blues"),
    COUNTRY("Country"),
    FOLK("Folk"),
    REGGAE("Reggae"),
    METAL("Metal"),
    PUNK("Punk"),
    ALTERNATIVE("Alternative"),
    INDIE("Indie"),
    SOUL("Soul"),
    FUNK("Funk"),
    RNB("R&B"),
    GOSPEL("Gospel"),
    LATIN("Latin"),
    SALSA("Salsa"),
    TANGO("Tango"),
    FLAMENCO("Flamenco"),
    K_POP("K-Pop"),
    J_POP("J-Pop"),
    WORLD("World"),
    AMBIENT("Ambient"),
    TRANCE("Trance"),
    TECHNO("Techno"),
    HOUSE("House"),
    DUBSTEP("Dubstep"),
    DRUM_AND_BASS("Drum and Bass"),
    CHILL("Chill"),
    OPERA("Opera"),
    ORCHESTRAL("Orchestral"),
    BAROQUE("Baroque"),
    DISCO("Disco"),
    SKA("Ska"),
    BLUEGRASS("Bluegrass"),
    NEW_AGE("New Age");

    private final String name;
    private static final Logger logger = LogManager.getLogger(MusicGenre.class);

    /**
     * Конструктор для ініціалізації жанру музики з назвою.
     *
     * @param name Назва жанру музики.
     * @throws RuntimeException Якщо ініціалізація жанру не вдалася.
     */
    MusicGenre(String name) {
        try {
            this.name = name;
        } catch (Exception e) {
            throw new RuntimeException("Не вдалося ініціалізувати жанр музики", e);
        }
    }

    /**
     * Повертає назву жанру музики у вигляді рядка.
     *
     * @return Назва жанру або "Невідомий жанр" у разі помилки.
     */
    @Override
    public String toString() {
        try {
            logger.debug("Отримано назву жанру: {}", name);
            return name;
        } catch (Exception e) {
            logger.error("Помилка при отриманні назви жанру: {}", name, e);
            return "Невідомий жанр";
        }
    }
}