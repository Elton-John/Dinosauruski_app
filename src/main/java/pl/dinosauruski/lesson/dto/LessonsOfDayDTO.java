package pl.dinosauruski.lesson.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class LessonsOfDayDTO {
    private LocalDate date;
    private int dayOfWeek;
    private List<LessonViewDTO> lessonViewDTOS;

    @Override
    public String toString() {
        return "LessonsOfDayDTO{" +
                "date=" + date +
                ", dayOfWeek=" + dayOfWeek +
                ", lessonViewDTOS=" + lessonViewDTOS +
                '}';
    }
}
