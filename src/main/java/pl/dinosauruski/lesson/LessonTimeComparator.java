package pl.dinosauruski.lesson;

import pl.dinosauruski.lesson.dto.LessonViewDTO;

import java.util.Comparator;

public class LessonTimeComparator implements Comparator<LessonViewDTO> {
    @Override
    public int compare(LessonViewDTO o1, LessonViewDTO o2) {
        if (o1.getTime().isAfter(o2.getTime())) {
            return 1;
        } else if (o2.getTime().isAfter(o1.getTime())) {
            return -1;
        } else {
            return 0;
        }
    }
}
