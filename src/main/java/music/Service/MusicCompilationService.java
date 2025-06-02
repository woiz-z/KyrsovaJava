package music.Service;

import music.Models.MusicTrack;

import java.time.Duration;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Сервісний клас для операцій з музичними компіляціями.
 */
public class MusicCompilationService {

    public MusicCompilationService() {

    }
    /**
     * Обчислює загальну тривалість треків.
     */
    public Duration calculateTotalDuration(List<MusicTrack> tracks) {
        if (tracks == null) {
            throw new IllegalArgumentException("Список треків не може бути null");
        }
        return tracks.stream()
                .map(MusicTrack::getDuration)
                .reduce(Duration.ZERO, Duration::plus);
    }

    /**
     * Сортує треки за жанром.
     */
    public void sortByGenre(List<MusicTrack> tracks) {
        if (tracks == null) {
            throw new IllegalArgumentException("Список треків не може бути null");
        }
        tracks.sort(Comparator.comparing(track -> track.getGenre().toString()));
    }

    /**
     * Фільтрує треки за діапазоном тривалості.
     */
    public List<MusicTrack> filterByDurationRange(List<MusicTrack> tracks, Duration min, Duration max) {
        if (tracks == null) {
            throw new IllegalArgumentException("Список треків не може бути null");
        }
        if (min == null || max == null) {
            throw new IllegalArgumentException("Мінімальна або максимальна тривалість не може бути null");
        }
        if (min.isNegative() || max.isNegative()) {
            throw new IllegalArgumentException("Тривалість не може бути від'ємною");
        }
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException("Мінімальна тривалість не може бути більшою за максимальну");
        }

        return tracks.stream()
                .filter(track -> track != null)
                .filter(track -> !track.getDuration().minus(min).isNegative() &&
                        !max.minus(track.getDuration()).isNegative())
                .collect(Collectors.toList());
    }

}