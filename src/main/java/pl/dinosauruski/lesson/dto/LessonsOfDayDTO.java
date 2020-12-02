package pl.dinosauruski.lesson.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class LessonsOfDayDTO {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    private int dayOfWeek;
    private List<LessonViewDTO> lessonViewDTOS;

}
